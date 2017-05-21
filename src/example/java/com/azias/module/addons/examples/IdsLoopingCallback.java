package com.azias.module.addons.examples;

import com.azias.module.addons.AddonLoader;
import com.azias.module.addons.Container;
import com.azias.module.addons.LoopingCallback;

public class IdsLoopingCallback implements LoopingCallback {
	
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
	
	@Override
	public float getProgress() {
		return 0;
	}
	
	@Override
	public boolean update(AddonLoader al) {
		return false;
	}
}
