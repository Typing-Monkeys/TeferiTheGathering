package com.magicengine;

import java.util.LinkedList;

public class MakeChoice {
	private int idChoice;
	private String choiceText; 
	private LinkedList<ChoiceOption> choiceOptions;
	
	public MakeChoice()
	{
		super();
		this.idChoice = -1;
		this.choiceText = "";
		this.choiceOptions = new LinkedList<ChoiceOption>();
	}

	public MakeChoice(int idChoice, String choiceText,
			LinkedList<ChoiceOption> choiceOptions) {
		super();
		this.idChoice = idChoice;
		this.choiceText = choiceText;
		this.choiceOptions = choiceOptions;
	}

	public int getIdChoice() {
		return idChoice;
	}

	public void setIdChoice(int idChoice) {
		this.idChoice = idChoice;
	}

	public String getChoiceText() {
		return choiceText;
	}

	public void setChoiceText(String choiceText) {
		this.choiceText = choiceText;
	}

	public LinkedList<ChoiceOption> getChoiceOptions() {
		return choiceOptions;
	}

	public void setChoiceOptions(LinkedList<ChoiceOption> choiceOptions) {
		this.choiceOptions = choiceOptions;
	}
	
	public void addOption(int idOtion, String optionText)
	{
		this.choiceOptions.add(new ChoiceOption(idOtion, optionText));
	}
	
	public void addOption(String idOtion, String optionText)
	{
		this.choiceOptions.add(new ChoiceOption(idOtion, optionText));
	}
	public void addOptionExtra(int idOtion, String optionText, String extra)
	{
		this.choiceOptions.add(new ChoiceOption(idOtion, optionText, extra));
	}
	
	public void addOptionExtra(String idOtion, String optionText, String extra)
	{
		this.choiceOptions.add(new ChoiceOption(idOtion, optionText, extra));
	}
	
}
