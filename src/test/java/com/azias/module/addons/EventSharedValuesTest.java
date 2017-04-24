package com.azias.module.addons;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Herwin Bozet
 */
public class EventSharedValuesTest {
	private final Logger logger = LoggerFactory.getLogger(EventSharedValuesTest.class);
	
	@Rule
	public TestRule watcher = new TestWatcher() {
		protected void starting(Description description) {
			logger.info("Starting test: {}", description.getMethodName());
		}
	};
	
	@Test
	public void mainTest() {
		AddonLoader al = new AddonLoader(new String[] {});
		try {
			// Replaced the old constructor with flags.
			//al.initialize(false);
			al.setReqAdnsFlag(false);
			al.initialize();
		} catch(Exception e) {
			e.printStackTrace();
			fail("An exception occured while initializing the AddonLoader.");
		}
		
		Callback cb = new ESVCallback();
		AddonEvent ae = new ESVEvent();
		
		int defaultValue = 42;
		((ESVEvent) ae).setDefaultValue(defaultValue);
		
		//They have to be added separately, otherwise the init function is only executed once, I have no idea why.
		if(!al.addCallbackTask(cb, ae, true)) {
			fail("Unable to add the first Callback Tasks to the AddonLoader.");
		}
		
		if(!al.addCallbackTask(cb, ae, true)) {
			fail("Unable to add the second Callback Tasks to the AddonLoader.");
		}
		
		al.finishLoading();
		
		if(((ESVEvent) ae).getDefaultValue() == defaultValue + 6) {
			assertTrue(true);
		} else {
			fail("Got the wrong number from the ESVEvent: " + ((ESVEvent) ae).getDefaultValue());
		}
	}
}

class ESVCallback implements Callback {
	
	@Override
	public boolean init(AddonEvent event) {
		((ESVEvent) event).incrementDefaultValue();
		return false;
	}
	
	@Override
	public boolean execute(AddonEvent event) {
		((ESVEvent) event).incrementDefaultValue();
		return false;
	}
	
	@Override
	public boolean finalize(AddonEvent event) {
		((ESVEvent) event).incrementDefaultValue();
		return false;
	}
}

class ESVEvent implements AddonEvent {
	protected int defaultValue = -42;
	
	public void setDefaultValue(int par1) {
		this.defaultValue = par1;
	}
	
	public int getDefaultValue() {
		return this.defaultValue;
	}
	
	public void incrementDefaultValue() {
		this.defaultValue++;
	}
}
