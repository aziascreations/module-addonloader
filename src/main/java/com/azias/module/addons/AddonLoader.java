package com.azias.module.addons;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azias.module.common.Pair;
import com.azias.module.version.Version;
import com.azias.module.version.VersionDeserialiser;
import com.azias.module.version.VersionSerialiser;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Herwin Bozet
 */
public class AddonLoader {
	private final static Logger logger = LoggerFactory.getLogger(AddonLoader.class);
	
	// Might be used later in a DumpInfo() function, not sure.
	//public static final String version = "1.0.1";
	
	/** Use this as the load order */
	protected String[] addonsIds;
	protected String addonsFolder;
	
	protected int currentTaskIndex = 0, currentTaskStep = 0;
	
	protected HashMap<String, AddonInfo> addonsInfos;
	protected List<Class<?>> addonsClasses;
	protected List<Pair> loadingTasks;
	
	protected final GsonBuilder gsonBuilder = new GsonBuilder();
	protected Gson gson;

	protected boolean searchArchives = false;
	protected boolean requireAddons = true;
	protected CleaningRule cleaningRule = CleaningRule.NONE;

	protected boolean hasLoadingErrors = false;
	
	//TODO: Allow a sort of re-init. - See SetSmthFlag()... - Being worked on... init[X], update[?], misc[]
	//TODO: Check addons' dependencies.
	public AddonLoader(String[] addonsIds) {
		this(addonsIds, "./addons/");
	}
	
	public AddonLoader(String[] addonsIds, String addonsFolder) {
		logger.debug("Calling AddonLoader constructor with {} and \"{}\" as parameters", Arrays.toString(addonsIds), addonsFolder);
		// Findbugs was getting pissy so i did what it asked and used clone().
		this.addonsIds = addonsIds.clone();
		this.addonsFolder = addonsFolder;
		
		// Registering (De)Serializer for version field in addon.json files.
		gsonBuilder.registerTypeAdapter(Version.class, new VersionSerialiser());
		gsonBuilder.registerTypeAdapter(Version.class, new VersionDeserialiser());
	}
	
	/**
	 * Initialize the AddonLoader and load the addons info files and checks if everything is working correctly and ready to be loaded.
	 * 
	 * @param requireAddons
	 *            A boolean indicating whether an addon is required to
	 *            initialize the {@link AddonLoader}.
	 * @throws AddonException
	 *             Thrown when the addons list is empty, might get removed later
	 * @throws IOException
	 *             Thrown if an error occurs when reading a "addon.json" file.
	 * @throws Exception
	 *             Thrown if an error occurs when a {@link com.google.gson.JsonSyntaxException}
	 *             is thrown when parsing a "addon.json" file.
	 */
	@Deprecated
	public void initialize(boolean requireAddons) throws AddonException, IOException {
		this.initialize();
	}
	
	/**
	 * Initialize the AddonLoader and load the addons info files and checks if everything is working correctly and ready to be loaded.
	 * 
	 * @throws AddonException
	 *             Thrown when the addons list is empty, might get removed later
	 * @throws IOException
	 *             Thrown if an error occurs when reading a "addon.json" file.
	 * @throws Exception
	 *             Thrown if an error occurs when a {@link com.google.gson.JsonSyntaxException}
	 *             is thrown when parsing a "addon.json" file.
	 */
	public void initialize() throws AddonException, IOException {
		logger.debug("Initializing the AddonLoader with {} addon(s)", addonsIds.length);
		
		// Initializing/Reseting variables
		gson = gsonBuilder.create();
		addonsInfos = new HashMap<>();
		addonsClasses = new ArrayList<>();
		loadingTasks = new ArrayList<>();
		currentTaskIndex = 0;
		currentTaskStep = 0;
		hasLoadingErrors = false;
		
		if(addonsIds.length <= 0 && requireAddons)
			throw new AddonException("Addons are required to continue !");

		// Loading addon.json info files
		for(String addonId : addonsIds) {
			logger.debug("Loading {}...", addonId);

			File addonInfoFile = new File(addonsFolder + addonId + "/addon.json");
			if(!addonInfoFile.exists())
				throw new IOException("Unable to find: " + addonInfoFile.getPath());

			try {
				AddonInfo addonInfo = gson.fromJson(AddonLoader.fileToString(addonInfoFile.getPath()), AddonInfo.class);
				addonInfo.resetTransientFields();
				addonsInfos.put(addonInfo.id, addonInfo);
			} catch(Exception e) {
				throw e;
			}
		}
		
		logger.debug("Indexing addons classes...");
		addonsClasses.addAll(new Reflections("").getTypesAnnotatedWith(Addon.class));
		
		// TODO: Use breaks instead of continues.
		// Looping trough addons classes (i)
		logger.debug("Checking the addons classes...");

		for(Class<?> addonClass : addonsClasses) {
			String classId = addonClass.getAnnotation(Addon.class).id();

			// Checks if classes have an id. - Couldn't get it to trigger in tests, it's not really useful anyway.
			if(Strings.isNullOrEmpty(classId)) {
				String className = ((addonClass.getPackage()==null)?"NoPackage: ":addonClass.getPackage().getName())+"."+addonClass.getName();
				throw new AddonException("An addon class is declared without an id or with an empty one. ("+className+")");
			}

			// Checks if an addon(j) has the same id as the current class(i).
			// And sets the "hasCode" boolean to check if an addon has code that needs to be executed.
			for(String addonId : addonsIds) {
				if(classId.equals(addonId)) {
					logger.debug("Found class for addon: {}", classId);
					//TODO: Check for potential NullPointerException when getting a null map entry.
					addonsInfos.get(addonId).setHasCode();
					continue; //Is this even useful to avoid the logger.warn 3 lines down ?
				}
			}
			logger.warn("Unable to find an addon with the same id as a class: {}", classId);
		}
		
		logger.debug("Checking dependencies... (Not Implemented yet, waiting for an update for module-version)");
		
		logger.info("AddonLoader successfully initialized.");
	}
	
	// I got tired of the old cat
	//  so I put this one instead
	//
	//    /\_/\
	//  =( °w° )=
	//    )   (  //
	//   (__ __)//
	//
	
	/**
	 * @return true if every loading tasks has been completed.<br>
	 *         false if there is still things to do.
	 */
	// TODO: Optimize this whole function later
	public boolean update() {
		if(loadingTasks.isEmpty()) {
			logger.debug("The AddonLoader tasks list is empty, skipping update().");
			return true;
		}
		
		Pair taskPair = loadingTasks.get(currentTaskIndex);
		
		if(taskPair.getFirst() instanceof String) {
			logger.debug("Calling functions...");
			
			if(this.currentTaskStep >= this.addonsIds.length) {
				// TODO: Fix this, might block full completion of LoopingCallbacks. - Maybe not, was I that dumb back then ?
				this.currentTaskStep = 0;
				this.currentTaskIndex++;
			} else {
				// Checks for addons without code and skips them
				//TODO: Check for potential NullPointerException when getting a null map entry.
				if(!this.addonsInfos.get(this.addonsIds[this.currentTaskStep]).hasCode) {
					this.currentTaskStep++;
					return false;
				}
				
				//TODO: "Unnest" this.
				for(int i = 0; i < this.addonsClasses.size(); i++) {
					if(this.addonsClasses.get(i).getAnnotation(Addon.class).id().equals(this.addonsIds[this.currentTaskStep])) {
						//this.addonsClasses.get(i).getMethod(name, parameterTypes)
						for(Method m : this.addonsClasses.get(i).getMethods()) {
							if(m.getName().equals((String) taskPair.getFirst())) {
								try {
									m.invoke(this.addonsClasses.get(i).newInstance(), taskPair.getSecond());
								} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
									logger.error("An error occured while executing the \"{}\" function for \"{}\"", taskPair.getFirst(),
											this.addonsIds[this.currentTaskStep]);
									e.printStackTrace();
									hasLoadingErrors = true;
								}
								break;
							}
						}
						break;
					}
				}
				
				this.currentTaskStep++;
				
				//TODO: Clean/remove the one in the beginning.
				if(this.currentTaskStep >= this.addonsIds.length) {
					this.currentTaskStep = 0;
					this.currentTaskIndex++;
				}
			}
		} else if(taskPair.getFirst() instanceof LoopingCallback) {
			logger.debug("Executing LoopingCallback...");
			if(this.currentTaskStep == 0) {
				if(((LoopingCallback) taskPair.getFirst()).update())
					this.currentTaskStep++;
				return false;
			} else {
				((LoopingCallback) taskPair.getFirst()).finalize((AddonEvent)taskPair.getSecond());
				this.currentTaskStep = 0;
				this.currentTaskIndex++;
			}
		} else if(taskPair.getFirst() instanceof Callback) {
			logger.debug("Executing Callback...");
			((Callback) taskPair.getFirst()).execute((AddonEvent)taskPair.getSecond());
			
			logger.debug("Finalizing Callback execution...");
			((Callback) taskPair.getFirst()).finalize((AddonEvent)taskPair.getSecond());
			
			this.currentTaskStep = 0;
			this.currentTaskIndex++;
		} else {
			logger.error("Unable to process task, the Pair's first Object Type is not supported.");
			this.currentTaskStep = 0;
			this.currentTaskIndex++;
		}
		
		return (currentTaskIndex >= this.loadingTasks.size()) ? true : false;
	}
	
	/**
	 * Blocks until all addons and callbacks are executed/loaded.
	 * @return true if no error occured. - NOT IMPLEMENTED YET.
	 */
	public boolean finishLoading() {
		while(!update()) {}
		return false;
	}
	
	/**
	 * Adds a reflection task.<br>
	 * This task will execute a function with the given name in every addons
	 * classes.
	 * 
	 * @param functionName
	 *            The function's name that will be executed.
	 * @param event
	 *            An interface implementing {@link AddonEvent}.
	 * @return true if the task has been added.<br>
	 *         false if an error occured while trying to add it.
	 */
	public boolean addReflectionTask(String functionName, AddonEvent event) {
		if(this.loadingTasks == null) {
			logger.error("The AddonLoader isn't initialized.");
			return false;
		}
		this.loadingTasks.add(new Pair(functionName, event));
		
		return true;
	}
	
	/**
	 * Adds a Callback task to the task list and execute the callback's init
	 * function.
	 * 
	 * @param callback
	 *            An interface implementing {@link Callback}.
	 * @param event
	 *            An interface implementing {@link AddonEvent}.
	 * @return true if the task has been added.<br>
	 *         false if an error occured while trying to add it.
	 */
	public boolean addCallbackTask(Callback callback, AddonEvent event) {
		return this.addCallbackTask(callback, event, true);
	}
	
	/**
	 * Adds a Callback task to the task list.<br>
	 * And can execute the callback's init function depending on the given
	 * parameters.
	 * 
	 * @param callback
	 *            An interface implementing {@link Callback}.
	 * @param event
	 *            An interface implementing {@link AddonEvent}.
	 * @param executeInit
	 *            [A boolean indicating] Read the param name.
	 * @return true if the task has been added.<br>
	 *         false if an error occured while trying to add it.
	 */
	public boolean addCallbackTask(Callback callback, AddonEvent event, boolean executeInit) {
		if(this.loadingTasks == null) {
			logger.error("The AddonLoader isn't initialized.");
			return false;
		}
		
		try {
			if(executeInit)
				callback.init(event);
		} catch(Exception e) {
			logger.error("An error occured while excuting the init function of a Callback.");
			return false;
		}
		
		this.loadingTasks.add(new Pair(callback, event));
		
		return true;
	}
	
	/**
	 * @return
	 */
	public float getMainProgress() {
		return (float) currentTaskIndex / (float) loadingTasks.size();
	}
	
	public AddonLoader setArchivesFlag(boolean par1) {
		if(this.gson != null)
			logger.warn("You will have to re-initialize the AddonLoader for the \"searchArchives\" flag to take effect.");
		this.searchArchives = par1;
		return this;
	}
	
	public AddonLoader setReqAdnsFlag(boolean par1) {
		if(this.gson != null)
			logger.warn("You will have to re-initialize the AddonLoader for the \"requireAddons\" flag to take effect.");
		this.requireAddons = par1;
		return this;
	}
	
	public AddonLoader setCleaningRule(CleaningRule cr) {
		if(this.gson != null)
			logger.warn("Ignore this please.");
		this.cleaningRule = cr;
		return this;
	}
	
	public void dispose() {
		//Call dispose in addons
		
		
		//Clean if flag is set
		
	}
	
	/**
	 * Load a file and returns the text encoded in UTF-8.
	 * 
	 * @param path
	 *            The desired file's path
	 * @return The file's content
	 * @throws IOException
	 */
	private static String fileToString(String path) throws IOException {
		return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
	}
}
