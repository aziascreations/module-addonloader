package com.azias.module.addons;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.azias.module.version.Version;
import com.azias.module.version.VersionDeserialiser;
import com.azias.module.version.VersionSerialiser;
import com.google.common.annotations.Beta;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A collection of functions that help you use and manipulate addons
 * 
 * @author Herwin
 */
@Beta
public class AddonUtils {
	private final static GsonBuilder gsonBuilder = new GsonBuilder();
	private static Gson gson;
	
	@Beta
	public static HashMap<String, AddonInfo> listAddonsInFolder(String folderPath) {
		
		return null;
	}
	
	@Beta
	public static HashMap<String, AddonInfo> listAddonsInFolders(String... foldersPaths) {
		ArrayList<String> directories = new ArrayList<String>();
		
		//Listing subdirectories
		for(String directory : foldersPaths)
			directories.addAll(listFoldersInFolder(directory));
		
		//Check if no folders were found
		if(directories.size() <= 0)
			return new HashMap<String, AddonInfo>();
		
		//Registering Version(De)Serializers
		if(gson == null) {
			gsonBuilder.registerTypeAdapter(Version.class, new VersionSerialiser());
			gsonBuilder.registerTypeAdapter(Version.class, new VersionDeserialiser());
			gson = gsonBuilder.create();
		}
		
		//Loading Addons
		HashMap<String, AddonInfo> addons = new HashMap<String, AddonInfo>();
		addons.putAll(loadAddonsFromFolders(directories));
		
		return addons;
	}
	
	private static HashMap<String, AddonInfo> loadAddonFromFolder(String directory) {
		return null;
	}
	
	private static HashMap<String, AddonInfo> loadAddonsFromFolders(ArrayList<String> directories) {
		HashMap<String, AddonInfo> addons = new HashMap<String, AddonInfo>();
		
		for(String directoryPath : directories) {
			File addonDirectory = new File(directoryPath + "/addon.json");
			
			if(!addonDirectory.exists() || !addonDirectory.isFile())
				continue;
			
			try {
				AddonInfo addonInfo = gson.fromJson(fileToString(addonDirectory), AddonInfo.class);
				addonInfo.resetTransientFields();
				addons.put(addonInfo.id, addonInfo);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return addons;
	}
	
	private static ArrayList<String> listFoldersInFolder(String folderPath) {
		File file = new File(folderPath);
		String[] directories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		return (directories != null) ? new ArrayList<String>(Arrays.asList(directories)) : new ArrayList<String>();
	}
	
	private static String fileToString(File file) throws IOException {
		return fileToString(file.getAbsolutePath());
	}
	
	private static String fileToString(String path) throws IOException {
		return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
	}
}
