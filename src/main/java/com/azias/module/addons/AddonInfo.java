package com.azias.module.addons;

import com.azias.module.version.Version;

/**
 * @author Herwin Bozet
 * @version 1.0.0-alpha
 */
public class AddonInfo {
	protected String id, name, description;
	protected String[] authors, credits;
	protected Version version;
	protected String versionUrl, projectUrl, updateUrl;
	
	// TOTO: Find how to use it. - Might be usefull to use the "hasCode" var -
	// It will be usefull for callbacks or AddonsEvents
	// isLoaded still has no use...
	protected transient boolean isLoaded = false, hasCode = false;
	
	public void resetTransientFields() {
		this.isLoaded = false;
		this.hasCode = false;
	}

	public void setHasCode() {
		this.setHasCode(true);
	}
	
	public void setHasCode(boolean hasCode) {
		this.hasCode = hasCode;
	}
}
