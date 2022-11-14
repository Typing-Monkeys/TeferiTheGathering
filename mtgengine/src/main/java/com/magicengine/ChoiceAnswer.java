package com.magicengine;

import java.util.LinkedList;

public class ChoiceAnswer {
	private int idPlayer;
	private int idChoice;
	private LinkedList<String> idOptions;
	
	public ChoiceAnswer(int idPlayer, int idChoice, LinkedList<String> idOptions) {
		super();
		this.idPlayer = idPlayer;
		this.idChoice = idChoice;
		this.idOptions = idOptions;
	}

	public int getIdPlayer() {
		return idPlayer;
	}

	public void setIdPlayer(int idPlayer) {
		this.idPlayer = idPlayer;
	}

	public int getIdChoice() {
		return idChoice;
	}

	public void setIdChoice(int idChoice) {
		this.idChoice = idChoice;
	}

	public LinkedList<String> getIdOptions() {
		return idOptions;
	}

	public void setIdOptions(LinkedList<String> idOptions) {
		this.idOptions = idOptions;
	}
}
