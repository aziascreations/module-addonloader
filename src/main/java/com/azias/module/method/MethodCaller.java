package com.azias.module.method;

import com.azias.module.common.Pair;

import java.util.List;

/**
 * Used to call methods using reflections without having to use the addons system.
 * @author Herwin
 */
public class MethodCaller {
	protected List<Pair> tasks;
	
	public MethodCaller() {
		
	}
	
	public boolean init() {
		// Can be called by update if not called before.
		// Will shutdown if an error occurs. -> recommended to do it before.
		return false;
	}
	
	public boolean update() {
		// Same thing as the AddonLoader.
		
		return false;
	}
	
	public boolean addTask(String method, MethodEvent event) {
		
		return false;
	}
}
