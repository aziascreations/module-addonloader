package com.azias.module.addons;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

class AddonUtils {
	/**
	 * 
	 * @param path
	 * @return The content of the file.
	 * @throws IOException
	 */
	protected static String fileToString(String path) throws IOException  {
		return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
	}
}
