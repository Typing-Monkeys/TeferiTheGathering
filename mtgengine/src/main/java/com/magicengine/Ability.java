package com.magicengine;

import java.util.LinkedList;

public class Ability {

	private boolean mana_ability 		= false; // non deve essere una loyalty ability
	private boolean loyalty_ability		= false; // sono una l'inverso dell'altra false -> true; true 
	
	private boolean keyword_ability 	= false;
	private String 	keyword_text 		= "";

	private String 	abilityText 		= "";
	// su ability text inserire il testo fornito dal json
	
	
	private boolean trigg_haste 		= false;
	private boolean spell_ability 		= false; // solo della classe ability on the stack
	private boolean triggered_ability	= false;
	
	//private String 	targetType			= "";
	//private MagicObject targetted_obj	= null;
	private boolean targetted_ability 				= false;
	private LinkedList<String> 		targetType		= null; // analizzato parzialmente
	private LinkedList<Target> targetted_obj	= null;
	
	private boolean activated_ability 	= false;
	private boolean evasion_ability 	= false; // Progetto Magic 2021/2022
	private boolean static_ability 		= false;
	
	private boolean as_a_sorcery 		= false;

	private boolean attached_to 		= false;
	private MagicObject attached_to_obj	= null; // da spostare
	
	private String 	timestamp 			= null; // non implementato
	
	private String 	triggered_condition	= null;	
	private String 	triggered_effect	= null;
	private String 	triggered_instruction= null;
	
	private LinkedList<String> activated_cost = null;
	private String activated_effect = null;
	private String activated_instruction =null;
	LinkedList<ManaSymbol> manaCostSymbols=null;
			
	private boolean flash = false;
	
	//public boolean checkedByDrools = false;

	public Ability(String str, boolean keyword) {
		if(keyword) {
			this.keyword_text = str;
		} else {
			this.abilityText = str;	
		}
		//System.out.println("Riempo abilita "+str);
	}


	public LinkedList<ManaSymbol> getManaCostSymbols() {
		return manaCostSymbols;
	}


	public void setManaCostSymbols() {
		String manaCost=this.getAbilityText().split(":")[0];
		
		LinkedList<ManaSymbol> manaCostSymbols = new LinkedList<ManaSymbol>();
		
		if(!manaCost.isEmpty()) {
			LinkedList<String> splitted = new LinkedList<String>();
			
			String temp = "";
			char c1;char c2;char c3;
			for(int i = 0; i < manaCost.length(); i++) {
				c1 = manaCost.charAt(i);
				if(c1=='(') {
					break;
				}
				if(c1 == '{' && i+2<manaCost.length()) { // se { e i+2 esiste
					c3=manaCost.charAt(i+2);
					if(c3=='}') { // se }
						c2=manaCost.charAt(i+1);
						temp=""+c1+c2+c3;
						splitted.add(temp);
						i=i+3;
					}
				}					
			}				
			for(String m : splitted) {
				manaCostSymbols.add(new ManaSymbol(m));
			}
		}
		this.manaCostSymbols=manaCostSymbols;
	}
	
	public boolean isTrigg_haste() {
		return trigg_haste;
	}
	
	public boolean getTrigg_haste() {
		return trigg_haste;
	}

	public void setTrigg_haste(boolean trigg_haste) {
		this.trigg_haste = trigg_haste;
	}



	public LinkedList<String> getActivated_cost() {
		return activated_cost;
	}

	
	public void Activated_cost(LinkedList<String> activated_cost) {
		this.activated_cost = activated_cost;
	}	
	public void addActivated_cost(String activated_cost) {
		if(this.activated_cost==null)
				this.activated_cost=new LinkedList<String>();
		this.activated_cost.add(activated_cost);
	}

	public String getActivated_effect() {
		return activated_effect;
	}


	public void setActivated_effect(String activated_effect) {
		this.activated_effect = activated_effect;
	}


	public String getActivated_instruction() {
		return activated_instruction;
	}


	public void setActivated_instruction(String activated_instruction) {
		this.activated_instruction = activated_instruction;
	}

	
	public String getTriggered_condition() {
		return triggered_condition;
	}


	public void setTriggered_condition(String triggered_condition) {
		this.triggered_condition = triggered_condition;
	}


	public String getTriggered_effect() {
		return triggered_effect;
	}


	public void setTriggered_effect(String triggered_effect) {
		this.triggered_effect = triggered_effect;
	}


	public String getTriggered_instruction() {
		return triggered_instruction;
	}


	public void setTriggered_instruction(String triggered_instruction) {
		this.triggered_instruction = triggered_instruction;
	}




	public boolean isMana_ability() {
		return mana_ability;
	}


	public void setMana_ability(boolean mana_ability) {
		this.mana_ability = mana_ability;
	}


	public boolean isLoyalty_ability() {
		return loyalty_ability;
	}


	public void setLoyalty_ability(boolean loyalty_ability) {
		this.loyalty_ability = loyalty_ability;
	}


	public boolean isKeyword_ability() {
		return keyword_ability;
	}


	public void setKeyword_ability(boolean keyword_ability) {
		this.keyword_ability = keyword_ability;
	}


	public String getKeyword_text() {
		return keyword_text;
	}


	public void setKeyword_text(String keyword_text) {
		this.keyword_text = keyword_text;
	}


	public String getAbilityText() {
		return abilityText;
	}


	public void setAbilityText(String abilityText) {
		this.abilityText = abilityText;
	}


	public boolean isSpell_ability() {
		return spell_ability;
	}


	public void setSpell_ability(boolean spell_ability) {
		this.spell_ability = spell_ability;
	}


	public boolean isTriggered_ability() {
		return triggered_ability;
	}


	public void setTriggered_ability(boolean triggered_ability) {
		this.triggered_ability = triggered_ability;
	}


	public boolean isTargetted_ability() {
		return targetted_ability;
	}


	public void setTargetted_ability(boolean targetted_ability) {
		this.targetted_ability = targetted_ability;
	}


	public LinkedList<Target> getTargetted_obj() {
		return targetted_obj;
	}


	public void setTargetted_obj(LinkedList<Target> targetted_obj) {
		this.targetted_obj = targetted_obj;
	}	
	public void addOneTargetted_obj(MagicObject targetted_obj) {
		if(this.targetted_obj==null)
				this.targetted_obj=new LinkedList<Target>();
		this.targetted_obj.add(targetted_obj);
	}
	
	public void addOneTargetted_obj(Card targetted_obj) {
		if(this.targetted_obj==null)
				this.targetted_obj=new LinkedList<Target>();
		this.targetted_obj.add(targetted_obj);
	}


	public boolean isActivated_ability() {
		return activated_ability;
	}


	public void setActivated_ability(boolean activated_ability) {
		this.activated_ability = activated_ability;
	}


	public boolean isEvasion_ability() {
		return evasion_ability;
	}


	public void setEvasion_ability(boolean evasion_ability) {
		this.evasion_ability = evasion_ability;
	}


	public boolean isStatic_ability() {
		return static_ability;
	}


	public void setStatic_ability(boolean static_ability) {
		this.static_ability = static_ability;
	}


	public boolean isAs_a_sorcery() {
		return as_a_sorcery;
	}


	public void setAs_a_sorcery(boolean as_a_sorcery) {
		this.as_a_sorcery = as_a_sorcery;
	}


	public boolean isAttached_to() {
		return attached_to;
	}


	public void setAttached_to(boolean attached_to) {
		this.attached_to = attached_to;
	}
	
	public MagicObject getAttached_to_obj() {
		return attached_to_obj;
	}


	public void setAttached_to_obj(MagicObject attached_to_obj) {
		this.attached_to_obj = attached_to_obj;
	}


	public String getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	public void setTargetType(LinkedList<String> targetType) {
		this.targetType = targetType;
	}
	
	public void addTargetType(String targetType) {
		if(this.targetType==null)
			this.targetType=new LinkedList<String>();
		this.targetType.add(targetType);
	}

	public LinkedList<String> getTargetType() {
		return this.targetType;
	}

	
	public boolean cercaTestoNellaStringa(String stringa, String tipo) {
		String abilityTextUpper=abilityText.toUpperCase();
		String stringaUpper=stringa.toUpperCase();		
		if(abilityTextUpper.contains(stringaUpper)) 
			if(tipo.equals(""))
				return true;
			else 
				return true;
		else
			return false;
	}
	public boolean cercaTestoNellaStringa(String stringa) {
		String abilityTextUpper=abilityText.toUpperCase();
		String stringaUpper=stringa.toUpperCase();		
		if(abilityTextUpper.contains(stringaUpper)) 
				return true;
		else
			return false;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abilityText == null) ? 0 : abilityText.hashCode());
		result = prime * result + (activated_ability ? 1231 : 1237);
		result = prime * result + ((activated_cost == null) ? 0 : activated_cost.hashCode());
		result = prime * result + ((activated_effect == null) ? 0 : activated_effect.hashCode());
		result = prime * result + ((activated_instruction == null) ? 0 : activated_instruction.hashCode());
		result = prime * result + (as_a_sorcery ? 1231 : 1237);
		result = prime * result + (attached_to ? 1231 : 1237);
		result = prime * result + ((attached_to_obj == null) ? 0 : attached_to_obj.hashCode());
		result = prime * result + (evasion_ability ? 1231 : 1237);
		result = prime * result + (keyword_ability ? 1231 : 1237);
		result = prime * result + ((keyword_text == null) ? 0 : keyword_text.hashCode());
		result = prime * result + (loyalty_ability ? 1231 : 1237);
		result = prime * result + (mana_ability ? 1231 : 1237);
		result = prime * result + (spell_ability ? 1231 : 1237);
		result = prime * result + (static_ability ? 1231 : 1237);
		result = prime * result + ((targetType == null) ? 0 : targetType.hashCode());
		result = prime * result + (targetted_ability ? 1231 : 1237);
		result = prime * result + ((targetted_obj == null) ? 0 : targetted_obj.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + (triggered_ability ? 1231 : 1237);
		result = prime * result + ((triggered_condition == null) ? 0 : triggered_condition.hashCode());
		result = prime * result + ((triggered_effect == null) ? 0 : triggered_effect.hashCode());
		result = prime * result + ((triggered_instruction == null) ? 0 : triggered_instruction.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ability other = (Ability) obj;
		if (abilityText == null) {
			if (other.abilityText != null)
				return false;
		} else if (!abilityText.equals(other.abilityText))
			return false;
		if (activated_ability != other.activated_ability)
			return false;
		if (activated_cost == null) {
			if (other.activated_cost != null)
				return false;
		} else if (!activated_cost.equals(other.activated_cost))
			return false;
		if (activated_effect == null) {
			if (other.activated_effect != null)
				return false;
		} else if (!activated_effect.equals(other.activated_effect))
			return false;
		if (activated_instruction == null) {
			if (other.activated_instruction != null)
				return false;
		} else if (!activated_instruction.equals(other.activated_instruction))
			return false;
		if (as_a_sorcery != other.as_a_sorcery)
			return false;
		if (attached_to != other.attached_to)
			return false;
		if (attached_to_obj == null) {
			if (other.attached_to_obj != null)
				return false;
		} else if (!attached_to_obj.equals(other.attached_to_obj))
			return false;
		if (evasion_ability != other.evasion_ability)
			return false;
		if (keyword_ability != other.keyword_ability)
			return false;
		if (keyword_text == null) {
			if (other.keyword_text != null)
				return false;
		} else if (!keyword_text.equals(other.keyword_text))
			return false;
		if (loyalty_ability != other.loyalty_ability)
			return false;
		if (mana_ability != other.mana_ability)
			return false;
		if (spell_ability != other.spell_ability)
			return false;
		if (static_ability != other.static_ability)
			return false;
		if (targetType == null) {
			if (other.targetType != null)
				return false;
		} else if (!targetType.equals(other.targetType))
			return false;
		if (targetted_ability != other.targetted_ability)
			return false;
		if (targetted_obj == null) {
			if (other.targetted_obj != null)
				return false;
		} else if (!targetted_obj.equals(other.targetted_obj))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (triggered_ability != other.triggered_ability)
			return false;
		if (triggered_condition == null) {
			if (other.triggered_condition != null)
				return false;
		} else if (!triggered_condition.equals(other.triggered_condition))
			return false;
		if (triggered_effect == null) {
			if (other.triggered_effect != null)
				return false;
		} else if (!triggered_effect.equals(other.triggered_effect))
			return false;
		if (triggered_instruction == null) {
			if (other.triggered_instruction != null)
				return false;
		} else if (!triggered_instruction.equals(other.triggered_instruction))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Ability [mana_ability=" + mana_ability + ", loyalty_ability=" + loyalty_ability + ", keyword_ability="
				+ keyword_ability + ", keyword_text=" + keyword_text + ", abilityText=" + abilityText
				+ ", spell_ability=" + spell_ability + ", triggered_ability=" + triggered_ability
				+ ", targetted_ability=" + targetted_ability + ", targetType=" + targetType + ", targetted_obj="
				+ targetted_obj + ", activated_ability=" + activated_ability + ", evasion_ability=" + evasion_ability
				+ ", static_ability=" + static_ability + ", as_a_sorcery=" + as_a_sorcery + ", attached_to="
				+ attached_to + ", attached_to_obj=" + attached_to_obj + ", timestamp=" + timestamp
				+ ", triggered_condition=" + triggered_condition + ", triggered_effect=" + triggered_effect
				+ ", triggered_instruction=" + triggered_instruction + ", activated_cost=" + activated_cost
				+ ", activated_effect=" + activated_effect + ", activated_instruction=" + activated_instruction 
				+ ", trigg_haste" + trigg_haste + "]";
	}


	public boolean hasFlash() {
		return flash;
	}


	public void setFlash(boolean flash) {
		this.flash = flash;
	}

}
