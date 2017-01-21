package com.azias.module.addons.examples;

import com.azias.module.addons.AddonLoader;

/*
 * Note: Check the logs or the usage.md file to see what arguments [were/are] passed in the constructors.
 */
public class ConstructorExample {
	private static AddonLoader al;
	
	public static void main(String[] args) {
		al = new AddonLoader(new String[] { "test" });
		
		al = new AddonLoader(new String[] { "test" }, "./addons/");
		
		// Not implemented yet.
		// al = new AddonLoader(new String[] { "test" }, true);
		// al = new AddonLoader(new String[] { "test" }, "./addons/", true);
		
		//Used to prevent FindBugs from saying there is a problem here.
		if(al != null) {
			al = null;
		}
	}
}
