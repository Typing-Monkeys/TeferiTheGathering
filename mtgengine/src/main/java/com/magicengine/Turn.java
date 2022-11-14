package com.magicengine;

import java.util.LinkedList;

public class Turn extends LinkedList<Phase>{
	
	public int phasesNumber; // init by Drools
	private boolean additional;	
	private boolean toSkip;

	public Turn(boolean isAdditional) {	
		this.additional = isAdditional;		
		this.toSkip = false;
	}

	public int getPhaseNumber() {
		return phasesNumber;
	}

	public void setPhaseNumber(int phaseNumber) {
		this.phasesNumber = phaseNumber;
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
