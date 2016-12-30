package com.azias.module.addons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azias.module.version.Version;
import com.azias.module.version.VersionDeserialiser;
import com.azias.module.version.VersionSerialiser;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Herwin Bozet
 * @version 0.1.0
 */
public class AddonLoader {
	private final static Logger logger = LoggerFactory.getLogger(AddonLoader.class);
	protected String[] addonsIds;
	protected String addonsFolder;
	
	protected HashMap<String, AddonInfo> addonsInfos;
	protected List<Pair> loadingTasks;
	protected int currentTaskIndex = 0, currentTaskStep = 0;
	
	protected final GsonBuilder gsonBuilder = new GsonBuilder();
	protected Gson gson;
	
	protected List<Class<?>> addonsClasses;
	
	public AddonLoader(String[] addonsIds) {
		this(addonsIds, "./addons/");
	}
	
	public AddonLoader(String[] addonsIds, String addonsFolder) {
		logger.debug("Calling AddonLoader constructor with {} and \"{}\" as arguments", Arrays.toString(addonsIds), addonsFolder);
		this.addonsIds = addonsIds;
		this.addonsFolder = addonsFolder;
	}
	
	/**
	 * Initialize the AddonLoader and load the addons info files and check if
	 * everything is ok.
	 * 
	 * @throws AddonException
	 *             Thrown when the addons list is empty, might get removed later
	 * @throws FileNotFoundException
	 *             Thrown when a "addon.json" file isn't found when it should.
	 * @throws IOException
	 *             Thrown if an error occurs when reading a "addon.json" file.
	 */
	public void initialize() throws AddonException, IOException {
		logger.debug("Initializing the AddonLoader with {} addon(s)", this.addonsIds.length);
		
		// Adding Serialiser and Deserialiser for the Version object.
		this.gsonBuilder.registerTypeAdapter(Version.class, new VersionSerialiser());
		this.gsonBuilder.registerTypeAdapter(Version.class, new VersionDeserialiser());
		this.gson = this.gsonBuilder.create();
		
		this.addonsInfos = new HashMap<String, AddonInfo>();
		this.addonsClasses = new ArrayList<Class<?>>();
		this.loadingTasks = new ArrayList<Pair>();
		this.currentTaskIndex = 0;
		this.currentTaskStep = 0;
		
		// TODO: Check later if this is needed.
		if(this.addonsIds.length <= 0)
			throw new AddonException("Addons list is empty!");
		
		// Loading addons info files
		for (int i = 0; i < this.addonsIds.length; i++) {
			logger.debug("Loading {}...", this.addonsIds[i]);
			
			File addonInfoFile = new File(this.addonsFolder + this.addonsIds[i] + "/addon.json");
			if(!addonInfoFile.exists())
				throw new IOException("Unable to find: " + addonInfoFile.getPath());
			
			try {
				AddonInfo addonInfo = gson.fromJson(AddonUtils.fileToString(addonInfoFile.getPath()), AddonInfo.class);
				addonInfo.resetTransientFields(); // Useless ?
				this.addonsInfos.put(addonInfo.id, addonInfo);
			} catch (IOException e) {
				// logger.error(e.getMessage());
				throw e;
			} catch (Exception e) {
				// This part is used to catch the JsonSyntaxException, for some
				// mystic reason, java can't catch it with a specific "catch",
				// but just a generic Exception
				throw e;
			} /*
				 * catch(JsonSyntaxException e) {
				 * logger.error("An error occured");
				 * logger.error(e.getMessage()); throw e; }/
				 **/
			// My eyes, they start bleeding again.
		}
		
		logger.debug("Indexing addons classes...");
		this.addonsClasses.addAll(new Reflections("").getTypesAnnotatedWith(Addon.class));
		
		// Only warns about issues. - TODO: make it more efficient
		logger.debug("Checking the addons classes...");
		for (int i = 0; i < this.addonsClasses.size(); i++) {
			if(Strings.isNullOrEmpty(this.addonsClasses.get(i).getAnnotation(Addon.class).id())) {
				logger.error("An addon class is declared without an id or with an empty one.");
				continue;
			}
			for (int j = 0; j < this.addonsIds.length; j++) {
				if(this.addonsClasses.get(i).getAnnotation(Addon.class).id().equals(this.addonsIds[j])) {
					continue;
				}
			}
			logger.warn("Unable to find an addon with the same id as a class: {}", this.addonsClasses.get(i).getAnnotation(Addon.class).id());
		}
		
		logger.info("AddonLoader successfully initialized.");
	}
	
	/*
	 * Notes: Update the currentTaskIndex at the end of the update function
	 */
	/**
	 * @return true if every task is finished
	 */
	public boolean update() {
		if(this.loadingTasks.isEmpty()) {
			logger.warn("The AddonLoader tasks list is empty.");
			return true;
		}
		
		Pair taskPair = this.loadingTasks.get(this.currentTaskIndex);
		
		if(taskPair.getFirst() instanceof String) {
			logger.debug("Calling functions ...");
			
		} else if(taskPair.getFirst() instanceof LoopingCallback) {
			logger.debug("Executing LoopingCallback...");
			
		} else if(taskPair.getFirst() instanceof Callback) {
			logger.debug("Executing Callback...");
			((Callback) taskPair.getFirst()).execute(taskPair.getSecond());
			
			logger.debug("Finalizing Callback execution...");
			((Callback) taskPair.getFirst()).finalize(taskPair.getSecond());
			
			this.currentTaskIndex++;
			if(currentTaskIndex >= this.loadingTasks.size()) {
				return true;
			}
		} else {
			logger.error("Unable to process task, the Pair's first Object is not supported.");
		}
		
		return false;
	}
	
	/**
	 * Might be useful later... finalize was already used by the Object class
	 */
	public boolean end() {
		return true;
	}
	
	public boolean addReflectionTask(String functionName, AddonEvent event) {
		if(this.loadingTasks == null) {
			logger.error("The AddonLoader isn't initialized.");
			return false;
		}
		this.loadingTasks.add(new Pair(functionName, event));
		
		return true;
	}
	
	public boolean addCallbackTask(Callback callback, AddonEvent event) {
		return this.addCallbackTask(callback, event, true);
	}
	
	public boolean addCallbackTask(Callback callback, AddonEvent event, boolean executeInit) {
		if(this.loadingTasks == null) {
			logger.error("The AddonLoader isn't initialized.");
			return false;
		}
		
		try {
			if(executeInit)
				callback.init(event);
		} catch (Exception e) {
			logger.error("An error occured while excuting the init function of a Callback.");
			return false;
		}
		
		this.loadingTasks.add(new Pair(callback, event));
		
		return true;
	}
	
	public float getMainProgress() {
		return 0.0F;
	}
	
	public float getTaskProgress() {
		return 0.0F;
	}
	
	// http://stackoverflow.com/questions/443708/callback-functions-in-java
	// Using protected, might restrict access outside of the package...
	// TODO: Trouver d'autres parametres intéressants/utiles
	/*
	 * public interface Callback { boolean init(HashMap<String, AddonInfo>
	 * addonsInfos, Object... others); boolean execute(HashMap<String,
	 * AddonInfo> addonsInfos, Object... others); boolean
	 * finalize(HashMap<String, AddonInfo> addonsInfos, Object... others); }
	 * public interface LoopingCallback extends Callback { float getProgress();
	 * boolean update(); //==tick(); }/
	 **/
	
	// How the hell was i supposed to use this ?
	/**
	 * @param task
	 * @param others
	 */
	@Deprecated
	public boolean addReflectionTask(String taskName, String functionName, Object... args) {
		if(this.loadingTasks == null)
			return false;
		
		return true;
	}
	
	/**
	 * @param task
	 * @param others
	 * @throws AddonException
	 */
	@Deprecated
	public void addCallbackTask(String task, Object... args) throws AddonException {
		if(this.loadingTasks == null) {
			logger.error("The AddonLoader isn't initialized.");
			throw new AddonException("The AddonLoader isn't initialized.");
		}
		
	}
}
