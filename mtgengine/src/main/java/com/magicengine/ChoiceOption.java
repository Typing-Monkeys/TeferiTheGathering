package com.magicengine;

public class ChoiceOption {
	private String idOption;
	private String optionText;
	private String extra;
	
	public ChoiceOption(String idOption, String optionValue) {
		super();
		this.idOption = idOption;
		this.optionText = optionValue;
		this.extra="";
	}
	
	public ChoiceOption(int idOption, String optionValue) {
		super();
		this.idOption =  String.valueOf(idOption);
		this.optionText = optionValue;
		this.extra="";
	}	
	public ChoiceOption(String idOption, String optionValue,String extra) {
		super();
		this.idOption = idOption;
		this.optionText = optionValue;
		this.extra=extra;
	}
	
	public ChoiceOption(int idOption, String optionValue,String extra) {
		super();
		this.idOption =  String.valueOf(idOption);
		this.optionText = optionValue;
		this.extra=extra;
	}

	public String getIdOption() {
		return idOption;
	}

	public void setIdOption(int idOption) {
		this.idOption = String.valueOf(idOption);
	}
	
	public void setIdOption(String idOption) {
		this.idOption = idOption;
	}

	public String getOptionValue() {
		return optionText;
	}

	public void setOptionValue(String optionValue) {
		this.optionText = optionValue;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getExtra() {
		return extra;
	}
}
