package com.azias.module.addons.examples;

import com.azias.module.addons.AddonLoader;
import com.azias.module.addons.Callback;
import com.azias.module.addons.Container;

public class CallbackExample implements Callback {
	
	@Override
	public boolean init(AddonLoader al, Container container) {
		return false;
	}
	
	@Override
	public boolean execute(AddonLoader al, Container container) {
		return false;
	}
	
	@Override
	public boolean finalize(AddonLoader al, Container container) {
		return false;
	}
	
}
