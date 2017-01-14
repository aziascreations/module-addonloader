package com.azias.module.addons;

public interface LoopingCallback extends Callback {
	float getProgress();
	boolean update(); // ==tick(); - See libgdx AssetsManager
}
