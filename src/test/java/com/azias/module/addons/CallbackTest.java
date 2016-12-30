package com.azias.module.addons;

import java.util.HashMap;

public class CallbackTest {
	
	public static void main(String[] args) {
		Callback cb = new CustomCallback();
		
		argTest(cb);
		callingTest(cb);
	}
	
	private static void argTest(Callback arg1) {
		System.out.println("received: "+arg1);
	}
	
	private static void callingTest(Callback arg1) {
		arg1.init(null);
	}
}

class CustomCallback implements Callback {

	@Override
	public boolean init(HashMap<String, AddonInfo> addonsInfos, Object... others) {
		System.out.println("Init called !");
		return false;
	}

	@Override
	public boolean execute(HashMap<String, AddonInfo> addonsInfos, Object... others) {
		return false;
	}

	@Override
	public boolean finalize(HashMap<String, AddonInfo> addonsInfos, Object... others) {
		return false;
	}
}
