package com.azias.module.addons;

import java.io.IOException;

public class AddonLoaderTest {
	public static void main(String[] args) {
		AddonLoader al = new AddonLoader(new String[] { "test" });
		try {
			al.initialize();
		} catch(AddonException | IOException e) {
			e.printStackTrace();
		}
		
		Callback cb = new CustomCallback();
		Container ae = new CustomEvent();
		
		al.addCallbackTask(cb, ae, true);
		al.addCallbackTask(cb, ae, true);
		
		while (!al.update()) {
			System.out.println("Loading isn't finished");
		}
		
		System.out.println("Finished loading");
	}
}

class CustomCallback implements Callback {
	@Override
	public boolean init(AddonLoader al, Container event) {
		System.out.println("Callback's init function called !");
		return false;
	}
	
	@Override
	public boolean execute(AddonLoader al, Container event) {
		CustomEvent ce = (CustomEvent) event;
		System.out.println("Callback's execute function called !");
		System.out.println("Meaning of life: " + ce.getMeaningOfLife());
		return false;
	}
	
	@Override
	public boolean finalize(AddonLoader al, Container event) {
		System.out.println("Callback's finalize function called !");
		return false;
	}
}

class CustomEvent implements Container {
	public int getMeaningOfLife() {
		return 42;
	}
}
