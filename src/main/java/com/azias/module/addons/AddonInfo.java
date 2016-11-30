package com.azias.module.addons;

import com.azias.module.version.Version;

public class AddonInfo {
	protected String id, name, description;
	protected String[] authors, credits;
	protected Version version;
	protected String versionUrl, projectUrl, updateUrl;
	
	//Simply search for a class with the Addon annotation with the same id as the mod.
	///** Might not be required later */
	//protected boolean hasCode;
}
