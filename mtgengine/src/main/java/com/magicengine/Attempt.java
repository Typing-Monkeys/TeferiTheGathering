package com.magicengine;

public class Attempt {
	
	private int id;		//playerID settato da Node.js
	private int command; //TAKE_ACTION ecc ecc
	private int option; //PLAY_LAND ecc ecc
	private int option2;
	
	//verificare distinzione command-option
		
	public Attempt(int id, int command, int option, int option2) {
		this.id = id;
		this.command = command; 
		this.option = option;
		this.option2 = option2;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCommand() {
		return command;
	}
	public void setCommand(int command) {
		this.command = command;
	}
	public int getOption() {
		return option;
	}
	public void setOption(int option) {
		this.option = option;
	}

	public int getOption2() {
		return option2;
	}

	public void setOption2(int option2) {
		this.option2 = option2;
	}	
	
	
	
}
