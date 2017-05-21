package com.azias.module.addons;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddonClassCallingTest {
	private final Logger logger = LoggerFactory.getLogger(EventSharedValuesTest.class);
	
	private static AddonLoader addonLoader;
	
	public static int testingValue = 0;
	
	@Rule
	public TestRule watcher = new TestWatcher() {
		protected void starting(Description description) {
			logger.info("Starting test: {}", description.getMethodName());
		}
	};
	
	@BeforeClass
	public static void setUpBeforeClass() throws AddonException, IOException, Exception {
		addonLoader = new AddonLoader(new String[] { "test" });
		addonLoader.initialize();
	}
	
	@Test
	public void simpleClassCalling() {
		Container ae = new ACCEvent();
		addonLoader.addReflectionTask("testMethod", ae);
		
		while(!addonLoader.update()) {
			
		}
		
		if(AddonClassCallingTest.testingValue == 42) {
			assertTrue(true);
		} else {
			fail("Got the wrong testingValue: " + AddonClassCallingTest.testingValue);
		}
	}
}

class ACCEvent implements Container {
	private int newTestingValue = 41;
	
	public int getNewTestingValue() {
		//Incrementing the value to make sure it's not called multiple times.
		this.newTestingValue++;
		return this.newTestingValue;
	}
}

@Addon(id = "test")
class ACCAddon {
	public static void testMethod(ACCEvent ae) {
		AddonClassCallingTest.testingValue = ae.getNewTestingValue();
	}
	
	//The AddonEvent doesn't need to be casted in an addon's class like in a custom AddonEvent.
	/*public static void testMethod(AddonEvent ae) {
		AddonClassCallingTest.testingValue = ((ACCEvent) ae).getNewTestingValue();
	}*/
}
