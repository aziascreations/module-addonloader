package com.azias.module.addons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
	private String[] addonsIds;
	private String addonsFolder;
	
	//Use private modifier ?
	//Making them protected might let people extend the class.
	private HashMap<String, AddonInfo> addonsInfos;
	private ArrayList<Object> loadingTasks;
	//private int currentTasksStage = 0;
	
	private final GsonBuilder gsonBuilder = new GsonBuilder();
	private Gson gson;
	
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
	public void initialize() throws AddonException, IOException, JsonSyntaxException {
		logger.debug("Initializing the AddonLoader with {} addon(s)", this.addonsIds.length);
		
		//Adding Serialiser and Deserialiser for Version object.
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
				this.addonsInfos.put(addonInfo.id, addonInfo);
			} catch(IOException e) {
				//logger.error(e.getMessage());
				throw e;
			} catch(Exception e) {
				//This part is used to catch the JsonSyntaxException, for some mystic reason, java can't catch it with a specific "catch"
				throw e;
			}/* catch(JsonSyntaxException e) {
				logger.error("An error occured");
				logger.error(e.getMessage());
				throw e;
			}/**/
			//My eyes, they start bleeding again.
			
			
		}
		logger.info("AddonLoader successfully initialized.");
	}
	
	//How the hell was i supposed to use this ?
	/**
	 * @param task
	 * @param others
	 */
	public boolean addReflectionTask(String taskName, String functionName, Object... others) {
		if(this.loadingTasks==null)
			return false;
		
		return true;
	}
	
	/**
	 * @param task
	 * @param others
	 * @throws AddonException
	 */
	public void addCallbackTask(String task, Object... others) throws AddonException {
		if(this.loadingTasks==null) {
			throw new AddonException("The AddonLoader isn't initialized.");
		}
	}
	
	//http://stackoverflow.com/questions/443708/callback-functions-in-java
	//Use protected, might restrict access outside of the package...
	//TODO: Trouver d'autres parametres intéressant/utiles
	public interface Callback {
		boolean init(HashMap<String, AddonInfo> addonsInfos, Object... others);
		boolean execute(HashMap<String, AddonInfo> addonsInfos, Object... others);
	}
	
	public interface LoopingCallback extends Callback {
		float getProgress();
	}
}
