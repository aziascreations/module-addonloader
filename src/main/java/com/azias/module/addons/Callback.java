package com.azias.module.addons;

import java.util.HashMap;

public interface Callback {
	boolean init(HashMap<String, AddonInfo> addonsInfos, Object... others);
	boolean execute(HashMap<String, AddonInfo> addonsInfos, Object... others);
	boolean finalize(HashMap<String, AddonInfo> addonsInfos, Object... others);
}
