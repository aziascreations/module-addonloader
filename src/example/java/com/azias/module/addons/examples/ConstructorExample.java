package com.azias.module.addons.examples;

import com.azias.module.addons.AddonLoader;

public class ConstructorExample {
	private static AddonLoader al;
	
	public static void main(String[] args) {
		al = new AddonLoader(new String[] { "test" });
		
		al = new AddonLoader(new String[] { "test" }, "./addons/");
		
		// Not implemented yet.
		// al = new AddonLoader(new String[] { "test" }, true);
		// al = new AddonLoader(new String[] { "test" }, "./addons/", true);
		
		//Prevents FindBugs from saying there is a problem here.
		if(al != null) {
			al = null;
		}
	}
}
