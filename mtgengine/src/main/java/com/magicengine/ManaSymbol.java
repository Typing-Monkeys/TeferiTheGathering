package com.magicengine;

/**
 * Represents the a mana symbol. The symbols are:
 * 	- {W}, {U}, {B}, {R}, {G}, and {C}; 
 * 	- the numerical symbols {0}, {1}, {2}, {3}, {4}, and so on; the variable symbol {X}; 
 * 	- the hybrid symbols {W/U}, {W/B}, {U/B}, {U/R}, {B/R}, {B/G}, {R/G}, {R/W}, {G/W}, and {G/U}; 
 * 	- the monocolored hybrid symbols {2/W}, {2/U}, {2/B}, {2/R}, and {2/G}; 
 * 	- the Phyrexian mana symbols {W/P}, {U/P}, {B/P}, {R/P}, and {G/P}; and the snow symbol {S}.
 */
public class ManaSymbol {
	
	private String symbol;
	
	public ManaSymbol(String symbol) {
		
		this.setSymbol(symbol);
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
}
