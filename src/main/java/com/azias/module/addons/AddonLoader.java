package com.azias.module.addons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azias.module.version.Version;
import com.azias.module.version.VersionDeserialiser;
import com.azias.module.version.VersionSerialiser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * @author Herwin Bozet
 */
public class AddonLoader {
	private final static Logger logger = LoggerFactory.getLogger(AddonLoader.class);
	protected String[] addonsIds;
	protected String addonsFolder;
	
	protected HashMap<String, AddonInfo> addonsInfos;
	protected List<Pair> loadingTasks;
	protected int currentTasksIndex = 0;
	
	protected final GsonBuilder gsonBuilder = new GsonBuilder();
	protected Gson gson;
	
	public AddonLoader(String[] addonsIds) {
		this(addonsIds, "./addons/");
	}
	
	public AddonLoader(String[] addonsIds, String addonsFolder) {
		logger.debug("Calling AddonLoader constructor with {} and \"{}\" as arguments", Arrays.toString(addonsIds), addonsFolder);
		this.addonsIds = addonsIds;
		this.addonsFolder = addonsFolder;
	}
	
	/**
	 * Initialize the AddonLoader and load the addons info files and check if everything is ok.
	 * @throws AddonException Thrown when the addons list is empty, might get removed later
	 * @throws FileNotFoundException Thrown when a "addon.json" file isn't found when it should.
	 * @throws IOException Thrown if an error occurs when reading a "addon.json" file.
	 */
	public void initialize() throws AddonException, IOException {
		logger.debug("Initializing the AddonLoader with {} addon(s)", this.addonsIds.length);
		
		//Adding Serialiser and Deserialiser for the Version object.
		this.gsonBuilder.registerTypeAdapter(Version.class, new VersionSerialiser());
		this.gsonBuilder.registerTypeAdapter(Version.class, new VersionDeserialiser());
		this.gson = this.gsonBuilder.create();
		
		this.addonsInfos = new HashMap<String, AddonInfo>();
		
		//TODO: Check later if this is needed.
		if(this.addonsIds.length<=0)
			throw new AddonException("Addons list is empty!");
		
		//Loading addons info files
		for(int i=0; i<this.addonsIds.length; i++) {
			logger.debug("Loading {}...", this.addonsIds[i]);
			
			File addonInfoFile = new File(this.addonsFolder + this.addonsIds[i] + "/addon.json");
			if(!addonInfoFile.exists())
				throw new IOException("Unable to find: "+addonInfoFile.getPath());
			
			try {
				AddonInfo addonInfo = gson.fromJson(AddonUtils.fileToString(addonInfoFile.getPath()), AddonInfo.class);
				addonInfo.resetTransientFields(); //Useless ?
				this.addonsInfos.put(addonInfo.id, addonInfo);
			} catch(IOException e) {
				//logger.error(e.getMessage());
				throw e;
			} catch(Exception e) {
				//This part is used to catch the JsonSyntaxException, for some mystic reason, java can't catch it with a specific "catch", but just a generic Exception
				throw e;
			}/* catch(JsonSyntaxException e) {
				logger.error("An error occured");
				logger.error(e.getMessage());
				throw e;
			}/**/
			//My eyes, they start bleeding again.
		}
		this.currentTasksIndex = 0;
		logger.info("AddonLoader successfully initialized.");
	}
	
	public boolean addReflectionTask(String functionName, AddonEvent event) {
		if(this.loadingTasks==null) {
			logger.error("The AddonLoader isn't initialized.");
			return false;
		}
		this.loadingTasks.add(new Pair(functionName, event));
		
		return true;
	}
	
	public boolean addCallbackTask(Callback callback, AddonEvent event) {
		if(this.loadingTasks==null) {
			logger.error("The AddonLoader isn't initialized.");
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
	
	//http://stackoverflow.com/questions/443708/callback-functions-in-java
	//Using protected, might restrict access outside of the package...
	//TODO: Trouver d'autres parametres intéressants/utiles
	/*public interface Callback {
		boolean init(HashMap<String, AddonInfo> addonsInfos, Object... others);
		boolean execute(HashMap<String, AddonInfo> addonsInfos, Object... others);
		boolean finalize(HashMap<String, AddonInfo> addonsInfos, Object... others);
	}
	
	public interface LoopingCallback extends Callback {
		float getProgress();
		boolean update(); //==tick();
	}/**/
	
	//How the hell was i supposed to use this ?
	/**
	 * @param task
	 * @param others
	 */
	@Deprecated
	public boolean addReflectionTask(String taskName, String functionName, Object... args) {
		if(this.loadingTasks==null)
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
		if(this.loadingTasks==null) {
			logger.error("The AddonLoader isn't initialized.");
			throw new AddonException("The AddonLoader isn't initialized.");
		}
		
		
	}
}
