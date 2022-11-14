package com.magicengine;

public class Mana {
	
	private String color; // white, blue, black, red, and green.
	private String type; // white, blue, black, red, green, and colorless.
	private ManaSymbol manaSymbol;
	
	//TODO: gestire mana di più colori

	public Mana(String color, String type, ManaSymbol manaSymbol) {
		super();
		this.color = color;
		this.type = type;
		this.manaSymbol = manaSymbol;
	}
	
	public Mana(ManaSymbol manaSymbol) {
		super();
		
		// Implementare in futuro il parsing del ManaSymbol
		// in modo da generare automaticamente il mana corrispondente
		this.manaSymbol = manaSymbol;
		if (manaSymbol.getSymbol().equals("{G}")) {
			this.color = this.type = "green";
		} else if (manaSymbol.getSymbol().equals("{U}")) {
			this.color = this.type = "blue";
		} else if (manaSymbol.getSymbol().equals("{W}")) {
			this.color = this.type = "white";
		} else if (manaSymbol.getSymbol().equals("{B}")) {
			this.color = this.type = "black";
		} else if (manaSymbol.getSymbol().equals("{R}")) {
			this.color = this.type = "red";
		} else {
			this.color = null;
			this.type = "colorless";
		} 
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public ManaSymbol getManaSymbol() {
		return manaSymbol;
	}

	public void setManaSymbol(ManaSymbol manaSymbol) {
		this.manaSymbol = manaSymbol;
	}

	@Override
    public boolean equals(Object obj) {
		if (!(obj instanceof Mana))
            return false;
        if (obj == this)
            return true;
        
        Mana m = (Mana) obj;
        if(m.getType().equals(this.getType()) && m.getColor().equals(this.getColor()))
        	return true;
    	else
    		return false;
	}
}
