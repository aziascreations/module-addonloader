package com.azias.module.addons.testing;

import com.azias.module.addons.AddonLoader;

public class AddonCleaning {
	
	public static void main(String[] args) {
		AddonLoader a = new AddonLoader(new String[]{"test"}, "/addonsTest/");
		System.out.println("Cleaning...");
		a.dispose();
		
	}
	
}
