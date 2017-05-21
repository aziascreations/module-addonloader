package com.azias.module.addons;

public interface Callback {
	/**
	 * Used to setup/prepare stuff for the Callback to use later.
	 */
	boolean init(AddonLoader al, Container container);
	
	boolean execute(AddonLoader al, Container container);
	
	boolean finalize(AddonLoader al, Container container);
}
