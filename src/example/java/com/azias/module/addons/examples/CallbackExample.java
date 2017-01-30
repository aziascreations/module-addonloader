package com.azias.module.addons.examples;

import com.azias.module.addons.AddonEvent;
import com.azias.module.addons.Callback;;

public class CallbackExample implements Callback {
	
	@Override
	public boolean init(AddonEvent event) {
		return false;
	}
	
	@Override
	public boolean execute(AddonEvent event) {
		return false;
	}
	
	@Override
	public boolean finalize(AddonEvent event) {
		return false;
	}
	
}
