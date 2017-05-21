package com.azias.module.addons.tests.integration;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.azias.module.addons.AddonLoader;
import com.azias.module.addons.Container;
import com.azias.module.addons.LoopingCallback;

public class AddonsLoopingTest {
	private static AddonLoader al;
	private static String a;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}
	
	@Test
	public void test() {
		//fail("Not yet implemented");
		assert(true);
	}
	
}

class EventTempA implements Container {
	
}

// Passer à travers la liste d'addons dans l'addonloader
class LoopAddonCallback implements LoopingCallback {

	@Override
	public boolean init(AddonLoader al, Container event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean execute(AddonLoader al, Container event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean finalize(AddonLoader al, Container event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getProgress() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean update(AddonLoader al) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
