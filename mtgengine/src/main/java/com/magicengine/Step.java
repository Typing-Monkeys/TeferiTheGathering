package com.magicengine;

public class Step {
	private String name;
	private boolean additional;
	private boolean toSkip;

	public Step(String nameStep, boolean isAdditional) {
		this.name = nameStep;
		this.additional = isAdditional;		
		this.toSkip = false;
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
