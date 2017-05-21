package com.azias.module.addons;

public interface LoopingCallback extends Callback {
	float getProgress();
	
	/**
	 * Called over and over by the AddonLoader until it returns true.
	 */
	//boolean update();
	boolean update(AddonLoader al);
}
