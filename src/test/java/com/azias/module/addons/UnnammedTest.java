package com.azias.module.addons;

import java.io.IOException;
import java.util.Random;

public class UnnammedTest {
	public static void main(String[] args) {
		AddonLoader al = new AddonLoader(new String[] { "test" });
		try {
			al.initialize();
		} catch (AddonException | IOException e) {
			e.printStackTrace();
		}
		int test = 42;
		Callback cb = new UnnammedCallback();
		AddonEvent ae = new UnnammedEvent();
		
		((UnnammedEvent) ae).setTestValue(test);
		System.out.println(((UnnammedEvent) ae).getTestValue());
		
		al.addCallbackTask(cb, ae, true);
		al.addCallbackTask(cb, ae, true);/**/
		
		while (!al.update()) {
			System.out.println("Loading isn't finished");
		}
		
		System.out.println("Finished loading");
		System.out.println(((UnnammedEvent) ae).getTestValue());
		System.out.println(test);
		
		/*
		 * Note for future me:
		 * The value of an event will be carried through every Callback, even
		 * when casted, and you have to get them back from the declared event
		 * when the addonloader has finished loading it's stuff.
		 */
	}
}

class UnnammedCallback implements Callback {
	
	@Override
	public boolean init(AddonEvent event) {
		System.out.println("INIT START");
		UnnammedEvent ue = (UnnammedEvent) event;
		System.out.println(ue.getTestValue());
		ue.tickTest();
		System.out.println(ue.getTestValue());
		System.out.println("INIT END");
		return false;
	}
	
	@Override
	public boolean execute(AddonEvent event) {
		System.out.println("EXECUTE START");
		UnnammedEvent ue = (UnnammedEvent) event;
		System.out.println(ue.getTestValue());
		ue.setTestValue((new Random()).nextInt());
		System.out.println(ue.getTestValue());
		System.out.println("EXECUTE END");
		return false;
	}
	
	@Override
	public boolean finalize(AddonEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
}

class UnnammedEvent implements AddonEvent {
	protected int test = -1;
	
	public void setTestValue(int par1) {
		this.test = par1;
	}
	
	public int getTestValue() {
		return this.test;
	}
	
	public void tickTest() {
		this.test++;
	}
}
