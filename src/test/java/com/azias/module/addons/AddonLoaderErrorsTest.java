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
public class AddonLoaderErrorsTest {
	private final Logger logger = LoggerFactory.getLogger(AddonLoaderErrorsTest.class);
	
	private AddonLoader addonLoader;
	
	@Rule
	public TestRule watcher = new TestWatcher() {
		protected void starting(Description description) {
			logger.info("Starting test: {}", description.getMethodName());
		}
	};
	
	@Test
	public void emptyAddonsIdsTest() {
		addonLoader = new AddonLoader(new String[] {});
		
		try {
			addonLoader.initialize();
		} catch(AddonException e) {
			logger.info("Catched the expected AddonException.");
		} catch(Exception e) {
			fail("An unknown Exception has been catched.");
		}
		
		try {
			addonLoader.initialize(false);
		} catch(AddonException e) {
			fail("The AddonLoader has cheked if the addonList was empty.");
		} catch(Exception e) {
			logger.info("Catched a generic Exception while initializing the AddonLoader with \"false\"");
		}
		
		assertTrue(true);
	}
}
