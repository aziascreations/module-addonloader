package com.azias.module.addons;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddonUtilsTest {
	private final Logger logger = LoggerFactory.getLogger(AddonUtilsTest.class);

	@Rule
	public TestRule watcher = new TestWatcher() {
		protected void starting(Description description) {
			logger.info("Starting test: {}", description.getMethodName());
		}
	};

	@Test
	public void test1() {
		AddonUtils.listAddonsInFolders("./addons");
		assertTrue(true);
	}
	
	@Test
	public void test2() {
		AddonUtils.listAddonsInFolder("./addons");
		assertTrue(true);
	}
}
