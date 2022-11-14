package com.magicengine;

import java.util.LinkedList;

public class Phase extends LinkedList<Step>{
	
	public int stepsNumber; // init by Drools
	private String name;	//
	private boolean additional;	
	private boolean toSkip;

	public Phase(String namePhase, boolean isAdditional) {		
		this.name = namePhase;
		this.additional = isAdditional;		
		this.toSkip = false;
	}

	public int getStepsNumber() {
		return stepsNumber;
	}

	public void setStepsNumber(int stepsNumber) {
		this.stepsNumber = stepsNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getAdditional() {
		return additional;
	}

	public void setAdditional(boolean additional) {
		this.additional = additional;
	}	

	public boolean getToSkip() {
		return toSkip;
	}

	public void setToSkip(boolean toSkip) {
		this.toSkip = toSkip;
	}
}