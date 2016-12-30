package com.azias.module.addons;

public interface Callback {
	boolean init(AddonEvent event);
	
	boolean execute(AddonEvent event);
	
	boolean finalize(AddonEvent event);
}
