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

import com.azias.module.version.Version;
import com.azias.module.version.VersionDeserialiser;
import com.azias.module.version.VersionSerialiser;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Herwin Bozet
 * @version 1.0.0-alpha
 */
public class AddonLoader {
	private final static Logger logger = LoggerFactory.getLogger(AddonLoader.class);
	public static final String version = "1.0.0-rc1";
	
	/** Use this as the load order */
	protected String[] addonsIds;
	protected String addonsFolder;
	
	protected int currentTaskIndex = 0, currentTaskStep = 0;
	
	protected HashMap<String, AddonInfo> addonsInfos;
	protected List<Class<?>> addonsClasses;
	protected List<Pair> loadingTasks;
	
	protected final GsonBuilder gsonBuilder = new GsonBuilder();
	protected Gson gson;
	
	//TODO: Check addons' dependencies.
	public AddonLoader(String[] addonsIds) {
		this(addonsIds, "./addons/");
	}
	
	public AddonLoader(String[] addonsIds, String addonsFolder) {
		logger.debug("Calling AddonLoader constructor with {} and \"{}\" as parameters", Arrays.toString(addonsIds), addonsFolder);
		this.addonsIds = addonsIds.clone();
		this.addonsFolder = addonsFolder;
	}
	
	/**
	 * [Requires at least one addon to be loaded.]
	 * 
	 * @throws AddonException
	 *             Thrown when the addons list is empty, might get removed later
	 * @throws IOException
	 *             Thrown if an error occurs when reading a "addon.json" file.
	 * @throws Exception
	 *             Thrown if an error occurs when a {@link JsonSyntaxException}
	 *             is thrown when parsing a "addon.json" file.
	 */
	public void initialize() throws AddonException, IOException {
		this.initialize(true);
	}
	
	/**
	 * Initialize the AddonLoader and load the addons info files and [check if
	 * everything is ok].
	 * 
	 * @param requireAddons
	 *            A boolean indicating whether an addon is required to
	 *            initialize the {@link AddonLoader}.
	 * @throws AddonException
	 *             Thrown when the addons list is empty, might get removed later
	 * @throws IOException
	 *             Thrown if an error occurs when reading a "addon.json" file.
	 * @throws Exception
	 *             Thrown if an error occurs when a {@link JsonSyntaxException}
	 *             is thrown when parsing a "addon.json" file.
	 */
	public void initialize(boolean requireAddons) throws AddonException, IOException {
		logger.debug("Initializing the AddonLoader with {} addon(s)", addonsIds.length);
		
		gsonBuilder.registerTypeAdapter(Version.class, new VersionSerialiser());
		gsonBuilder.registerTypeAdapter(Version.class, new VersionDeserialiser());
		gson = gsonBuilder.create();
		
		addonsInfos = new HashMap<String, AddonInfo>();
		addonsClasses = new ArrayList<Class<?>>();
		loadingTasks = new ArrayList<Pair>();
		currentTaskIndex = 0;
		currentTaskStep = 0;
		
		if(addonsIds.length <= 0 && requireAddons)
			throw new AddonException("Addons list is empty!");
		
		// Loading addons info files
		for(int i = 0; i < addonsIds.length; i++) {
			logger.debug("Loading {}...", addonsIds[i]);
			
			File addonInfoFile = new File(addonsFolder + addonsIds[i] + "/addon.json");
			if(!addonInfoFile.exists())
				throw new IOException("Unable to find: " + addonInfoFile.getPath());
			
			try {
				AddonInfo addonInfo = gson.fromJson(AddonLoader.fileToString(addonInfoFile.getPath()), AddonInfo.class);
				addonInfo.resetTransientFields(); // Useless ?
				addonsInfos.put(addonInfo.id, addonInfo);
			} catch(IOException e) {
				throw e;
			} catch(Exception e) {
				// This part is used to catch the JsonSyntaxException.
				// For some mystic reason, java can't catch it with a
				// specific "catch", but just a generic Exception.
				throw e;
			}
		}
		
		logger.debug("Indexing addons classes...");
		addonsClasses.addAll(new Reflections("").getTypesAnnotatedWith(Addon.class));
		
		// TODO: should I use breaks instead of continues.
		// Only warns about issues. - TODO: make it more efficient
		// Looping trough addons classes (i)
		logger.debug("Checking the addons classes...");
		for(int i = 0; i < addonsClasses.size(); i++) {
			//Checks if classes have an id.
			if(Strings.isNullOrEmpty(addonsClasses.get(i).getAnnotation(Addon.class).id())) {
				logger.error("An addon class is declared without an id or with an empty one.");
				continue;
			}
			//Checks if an addon(j) has the same id as the current class(i).
			//Also sets a boolean to check if an addon has code that needs to be executed.
			for(int j = 0; j < addonsIds.length; j++) {
				if(addonsClasses.get(i).getAnnotation(Addon.class).id().equals(addonsIds[j])) {
					logger.debug("Found class for addon: {}", addonsClasses.get(i).getAnnotation(Addon.class).id());
					//TODO: Check for potential NullPointerException when getting a null map entry.
					addonsInfos.get(addonsIds[j]).setHasCode();
					continue;
				}
			}
			logger.warn("Unable to find an addon with the same id as a class: {}", addonsClasses.get(i).getAnnotation(Addon.class).id());
		}
		
		logger.info("AddonLoader successfully initialized.");
	}
	
	// I was tired of going up and down to see the initialize function so
	// I added a cute cat here to cheer me up and keep me company.
	//
	//           .-o=o-.
	//       ,  /=o=o=o=\ .--.
	//      _|\|=o=O=o=O=|    \
	// __.'  o`\=o=o=o=(`\   /
	// '.  o  3/`|.-""'`\ \ ;'`)   .---.
	//   \   .'  /   .--'  |_.'   / .-._)
	//    `)  _.'   /     /`-.__.' /
	//     `'-.____;     /'-.___.-'
	//              `"""`
	
	/**
	 * @return true if every loading tasks has been completed.<br>
	 *         false if there is still things to do.
	 */
	public boolean update() {
		if(this.loadingTasks.isEmpty()) {
			logger.warn("The AddonLoader tasks list is empty.");
			return true;
		}
		
		Pair taskPair = this.loadingTasks.get(this.currentTaskIndex);
		
		if(taskPair.getFirst() instanceof String) {
			logger.debug("Calling functions...");
			//UNTESTED
			
			if(this.currentTaskStep >= this.addonsIds.length) {
				this.currentTaskStep = 0;
				this.currentTaskIndex++;
			} else {
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
									//System.exit(212);
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
				((LoopingCallback) taskPair.getFirst()).finalize(taskPair.getSecond());
				this.currentTaskStep = 0;
				this.currentTaskIndex++;
			}
		} else if(taskPair.getFirst() instanceof Callback) {
			logger.debug("Executing Callback...");
			((Callback) taskPair.getFirst()).execute(taskPair.getSecond());
			
			logger.debug("Finalizing Callback execution...");
			((Callback) taskPair.getFirst()).finalize(taskPair.getSecond());
			
			this.currentTaskStep = 0;
			this.currentTaskIndex++;
		} else {
			logger.error("Unable to process task, the Pair's first Object is not supported.");
			this.currentTaskStep = 0;
			this.currentTaskIndex++;
		}
		
		return (currentTaskIndex >= this.loadingTasks.size()) ? true : false;
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
