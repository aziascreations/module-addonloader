package com.azias.module.addons;

/**
 * @author Unknown - Not searched
 * @source http://www.factsandpeople.com/facts-mainmenu-5/8-java/10-java-pair-class
 */
public class Pair {
	public Object o1;
	public AddonEvent o2;
	
	public Pair(Object o1, AddonEvent o2) {
		this.o1 = o1;
		this.o2 = o2;
	}
	
	Object getFirst() {
		return o1;
	}
	
	AddonEvent getSecond() {
		return o2;
	}
	
	void setFirst(Object o) {
		o1 = o;
	}
	
	void setSecond(AddonEvent o) {
		o2 = o;
	}
	
	public String toString() {
		return "Pair{" + o1 + ", " + o2 + "}";
	}
}