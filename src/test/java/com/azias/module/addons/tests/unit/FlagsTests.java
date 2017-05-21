package com.azias.module.addons.tests.unit;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azias.module.addons.AddonException;
import com.azias.module.addons.AddonLoader;

public class FlagsTests {
	private final Logger logger = LoggerFactory.getLogger(FlagsTests.class);
	
	private static AddonLoader al1, al2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		al1 = new AddonLoader(new String[]{"test"});
		al2 = new AddonLoader(new String[]{});
	}

	@Test
	public void defaultOptions() {
		try {
			al1.initialize();
			logger.info("No Exception thrown/catched.");
			assertTrue(true);
		} catch(AddonException | IOException e) {
			logger.error("Catched an Exception");
			logger.debug(e.getMessage());
			fail("Unwanted Exception catched.");
		}
	}

	@Test
	public void defaultOptionsEmpty() {
		try {
			al2.initialize();
			fail("No Exception catched");
		} catch(AddonException e) {
			logger.info("Catched the expected AddonException.");
			assertTrue(true);
		} catch(IOException e) {
			logger.error("Catched an IOException");
			logger.debug(e.getMessage());
			fail("Unwanted Exception catched.");
		}
	}
	
	/*@Test
	public void AddonsRequirementOptions() {
		
	}/**/
	
	//TODO: Add archive and cleaning options.
}
