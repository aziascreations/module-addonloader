package com.azias.module.addons;

import com.azias.module.version.Version;

public class AddonInfo {
	protected String id, name, description;
	protected String[] authors, credits;
	protected Version version;
	protected String versionUrl, projectUrl, updateUrl;
	
	// isLoaded still has no use...
	// Might be used to indicate if code has been executed for the addon.
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
