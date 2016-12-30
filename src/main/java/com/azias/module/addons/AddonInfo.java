package com.azias.module.addons;

import com.azias.module.version.Version;

public class AddonInfo {
	protected String id, name, description;
	protected String[] authors, credits;
	protected Version version;
	protected String versionUrl, projectUrl, updateUrl;
	
	//TOTO: Find how to use it. - Might be usefull to use the "hasCode" var - It will be usefull for callbacks or AddonsEvents
	protected transient boolean isLoaded = false, hasCode = false;
	
	public void resetTransientFields() {
		this.isLoaded = false;
		this.hasCode = false;
	}
	//Simply search for a class with the @Addon annotation with the same id as the mod.
	///** Might not be required later */
	//protected boolean hasCode;
}
