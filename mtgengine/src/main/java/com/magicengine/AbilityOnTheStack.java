package com.magicengine;

import java.util.*;

import com.magicengine.Spell.TotalCost;

public class AbilityOnTheStack extends MagicObject{

	
	private boolean mana_ability 							= false; // non deve essere una loyalty ability
	private boolean loyalty_ability						= false; // sono una l'inverso dell'altra false -> true; true 
	private boolean keyword_ability 					= false;
	private String 	keyword_text 							= "";	
	private String 	abilityText 							= "";
	// su ability text inserire il testo fornito dal json	
	private boolean spell_ability 						= false;
	private boolean triggered_ability					= false;	
	private boolean targetted_ability 				= false;
	//private String 	targetType			= "";
	//private MagicObject targetted_obj	= null;
	private LinkedList<String>targetType			= null;
	//private LinkedList<Target> targetted_obj	= null; // da levare
	private LinkedList<Integer> targetted_id 	= null;	
	private boolean activated_ability 				= false;
	private boolean evasion_ability 					= false;
	private boolean static_ability 						= false;	
	private boolean as_a_sorcery 						= false;
	private boolean attached_to 						= false;
	private boolean validtarget 						= false; //check if target is still valid
	private MagicObject attached_to_obj					= null;	
	private String 	timestamp 							= null;
	private int idStack;	
	private LinkedList<String> activated_cost 			= null;
	private String activated_effect 					= null;
	private String activated_instruction 				= null;	
	private MagicObject originCard 						= null; //source è l'object che ha generato l'ability
	private int controller; //il controller è il player che ha "messo nel gioco" l'ability
	private int owner; // controller e owner possono essere diversi
	private TotalCost totalCost;
	private LinkedList<ManaSymbol> manaCostSymbolsOriginalAbility;
	private Boolean sufficientMana;	
	private ListPointer<String> stepActivateAbility;
	
	

	public AbilityOnTheStack(Ability a, int idObject, MagicObject source, int controller, int owner, int idStack) {
		super(idObject);
		this.mana_ability 			= a.isMana_ability();
		this.loyalty_ability		= a.isLoyalty_ability();
		this.keyword_ability		= a.isKeyword_ability();
		this.keyword_text 			= a.getKeyword_text();
		this.abilityText 				= a.getAbilityText();
		this.triggered_ability	= a.isTriggered_ability();
		this.targetted_ability 	= a.isTargetted_ability();
		this.targetType				= a.getTargetType();
		this.activated_ability= a.isActivated_ability();
		this.evasion_ability 	= a.isEvasion_ability();
		this.static_ability 	= a.isStatic_ability();
		this.as_a_sorcery 		= a.isAs_a_sorcery();
		this.attached_to 			= a.isAttached_to();
		this.attached_to_obj	= a.getAttached_to_obj();
		this.timestamp 				= a.getTimestamp();
		this.activated_cost 	= a.getActivated_cost();
		this.activated_effect	= a.getActivated_effect();
		this.activated_instruction			= a.getActivated_instruction();
		this.totalCost 									= null;
		this.manaCostSymbolsOriginalAbility	= a.getManaCostSymbols();
		this.sufficientMana = null;
		this.originCard = source;
		this.controller = controller;
		this.owner = owner;
		this.idStack = idStack;	
		this.stepActivateAbility = new ListPointer<String>(new LinkedList<String>());
	}
	
	//Costruttore per il private Game
	public AbilityOnTheStack(AbilityOnTheStack a) {
		super(a.getMagicTargetId());
		this.mana_ability 					= a.isMana_ability();
		this.loyalty_ability				= a.isLoyalty_ability();
		this.keyword_ability				= a.isKeyword_ability();
		this.keyword_text 					= a.getKeyword_text();
		this.abilityText 					= a.getAbilityText();
		this.triggered_ability				= a.isTriggered_ability();
		this.targetted_ability 				= a.isTargetted_ability();
		this.targetType						= a.getTargetType();
		this.targetted_id					= a.getTargetted_id();
		this.activated_ability 				= a.isActivated_ability();
		this.evasion_ability 				= a.isEvasion_ability();
		this.static_ability 				= a.isStatic_ability();
		this.as_a_sorcery 					= a.isAs_a_sorcery();
		this.attached_to 					= a.isAttached_to();
		this.attached_to_obj				= a.getAttached_to_obj();
		this.timestamp 						= a.getTimestamp();
		this.activated_cost 				= a.getActivated_cost();
		this.activated_effect				= a.getActivated_effect();
		this.activated_instruction 			= a.getActivated_instruction();
		this.totalCost 						= a.getTotalCost();
		this.sufficientMana 				= a.getSufficientMana();
		this.originCard 					= a.getOriginCard();
		this.controller 					= a.getController();
		this.owner 							= a.getOwner();
		this.idStack 						= a.getIdStack();	
		this.stepActivateAbility 			= a.getStepActivateAbility();				
		this.spell_ability 					= a.isSpell_ability();
		this.validtarget					= a.getValidTarget();
	}
	
	
	//////////
	
	

	public boolean isValidtarget() {
		return validtarget;
	}
	
	public boolean getValidTarget() {
		return validtarget;
	}
	
	public void setValidTarget(boolean validtarget) {
		this.validtarget = validtarget;
	}

	public LinkedList<ManaSymbol> getManaCostSymbolsOriginalAbility() {
		return manaCostSymbolsOriginalAbility;
	}

	public void setManaCostSymbolsOriginalAbility(LinkedList<ManaSymbol> manaCostSymbolsOriginalAbility) {
		this.manaCostSymbolsOriginalAbility = manaCostSymbolsOriginalAbility;
	}

	public Boolean getSufficientMana() {
		return sufficientMana;
	}

	public void setSufficientMana(Boolean sufficientMana) {
		this.sufficientMana = sufficientMana;
	}

	public TotalCost getTotalCost() {
		return totalCost;
	}


	public void setTotalCost(TotalCost totalCost) {
		this.totalCost = totalCost;
	}
	
	public void initializeTotalCost()
	{
		this.totalCost = new TotalCost();
		this.totalCost.manaCostSymbols=this.manaCostSymbolsOriginalAbility;
	}
	
	///////
	
	public boolean isMana_ability() {
		return mana_ability;
	}

	public LinkedList<String> getActivated_cost() {
		return activated_cost;
	}


	public void setActivated_cost(LinkedList<String> activated_cost) {
		this.activated_cost = activated_cost;
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

	public ListPointer<String> getStepActivateAbility() {
		return stepActivateAbility;
	}


	public void setStepActivateAbility(ListPointer<String> stepActivateAbility) {
		this.stepActivateAbility = stepActivateAbility;
	}
	
	public int getIdStack() {
		return idStack;
	}

	public void setIdStack(int idStack) {
		this.idStack = idStack;
	}
	



	public MagicObject getOriginCard() {
		return originCard;
	}

	public void setOriginCard(MagicObject originCard) {
		this.originCard = originCard;
	}

	public int getController() {
		return controller;
	}


	public void setController(int controller) {
		this.controller = controller;
	}


	public int getOwner() {
		return owner;
	}


	public void setOwner(int owner) {
		this.owner = owner;
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


	public void setSpell_ability(boolean spell_ability) { // true se lancio un instant 
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

	public LinkedList<Integer> getTargetted_id() {
		return targetted_id;
	}


	public void setTargetted_id(LinkedList<Integer> targetted_id) {
		this.targetted_id = targetted_id;
	}	
	
	public void addOneTargetted_id(int targetted_id) {
		if(this.targetted_id==null)
				this.targetted_id=new LinkedList<Integer>();
		this.targetted_id.add(targetted_id);
	}

	
	public void id(Integer targetted_id) {
		if(this.targetted_id==null)
				this.targetted_id=new LinkedList<Integer>();
		this.targetted_id.add(targetted_id);
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
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + (triggered_ability ? 1231 : 1237);
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
		AbilityOnTheStack other = (AbilityOnTheStack) obj;
		if (abilityText == null) {
			if (other.abilityText != null)
				return false;
		} else if (!abilityText.equals(other.abilityText))
			return false;
		if (activated_ability != other.activated_ability)
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
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (triggered_ability != other.triggered_ability)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "AbilityOnTheStack [mana_ability=" + mana_ability + ", loyalty_ability=" + loyalty_ability
				+ ", keyword_ability=" + keyword_ability + ", keyword_text=" + keyword_text + ", abilityText="
				+ abilityText + ", spell_ability=" + spell_ability + ", triggered_ability=" + triggered_ability
				+ ", targetted_ability=" + targetted_ability + ", targetType=" + targetType + ", activated_ability=" + activated_ability + ", evasion_ability=" + evasion_ability
				+ ", static_ability=" + static_ability + ", as_a_sorcery=" + as_a_sorcery + ", attached_to="
				+ attached_to + ", attached_to_obj=" + attached_to_obj + ", timestamp=" + timestamp + ", originCard="
				+ originCard + ", controller=" + controller + ", owner=" + owner + "]";
	}
	
	
	///////////////////////pagamento del mana
	
	
	public String printTotalCost()
	{
		String mp = "";
		
		int cont_White = 0;
		int cont_Blue = 0;
		int cont_Black = 0;
		int cont_Red = 0;
		int cont_Green = 0;
		int count_Colorless = 0;
			
		for(Mana m : totalCost.getManaCost())
		{
			if(m.getType().equals("white"))
			{
				cont_White++;
			}
			else if(m.getType().equals("blue"))
			{
				cont_Blue++;
			}
			else if(m.getType().equals("black"))
			{
				cont_Black++;
			}
			else if(m.getType().equals("red"))
			{
				cont_Red++;
			}
			else if(m.getType().equals("green"))
			{
				cont_Green++;
			}
			else if(m.getType().equals("colorless"))
			{
				count_Colorless++;
			}
		}
		
		for(int i = 1; i <= cont_White; i++)
		{
			mp += "{W}";
		}
		for(int i = 1; i <= cont_Blue; i++)
		{
			mp += "{U}";
		}
		for(int i = 1; i <= cont_Black; i++)
		{
			mp += "{B}";
		}
		for(int i = 1; i <= cont_Red; i++)
		{
			mp += "{R}";
		}
		for(int i = 1; i <= cont_Green; i++)
		{
			mp += "{G}";
		}
		if(count_Colorless > 0)
		{
			mp += "{" + count_Colorless + "}";
		}
		
		if(mp.length() > 0)
		{
			return mp;
		}
		else
		{
			return "0";
		}		
	}
	


	public void checkMana(Player p) {
		LinkedList<Mana> manaPool = new LinkedList<Mana>(p.getManaPool());
		LinkedList<Mana> manaCost = new LinkedList<Mana>(totalCost.getManaCost());
		int colorlessMana = 0;
		for(Mana m : manaCost)
		{
			if(m.getType().equals("colorless"))
			{
				colorlessMana++;
			}
			else
			{
				if(!manaPool.remove(m))
				{
					this.sufficientMana = false;
					return;
				}
			}
		}
		for(int i = 0; i < colorlessMana; i++)
		{
			if(manaPool.size() > 0)
			{
				manaPool.remove();
			}
			else
			{
				this.sufficientMana = false;
				return;
			}
		}
		this.sufficientMana = true;
	}
	
	public int getCountSelectableMana() {
		LinkedList<Mana> manaCost = new LinkedList<Mana>(totalCost.getManaCost());
		int count = 0;
		for(Mana m : manaCost)
		{
			if(m.getType().equals("colorless"))
			{
				count++;
			}
		}
		return count;
	}
	
	public LinkedList<Mana> getSelectableMana(Player p) {
		LinkedList<Mana> manaPool = new LinkedList<Mana>(p.getManaPool());
		LinkedList<Mana> manaCost = new LinkedList<Mana>(totalCost.getManaCost());
		System.out.println(manaCost);
		for(Mana m : manaCost)
		{
			manaPool.remove(m);
		}
		return manaPool;
	}
	
	public void payMana(Player p)
	{
		System.out.println("Pago il mana; Stampo il costo totale:");
		for(Mana m : totalCost.getManaCost()) {
			System.out.println("Colore: " + m.getColor() + " Tipo: "+ m.getType() +" Simbolo:"+ m.getManaSymbol().getSymbol());
		}		
		System.out.println("Stampo quello che ha il giocatore:");
		for(Mana m : p.getManaPool()) {
			System.out.println("Colore: " + m.getColor() + " Tipo: "+ m.getType() +" Simbolo:"+ m.getManaSymbol().getSymbol());
		}		
		
		for(Mana m : totalCost.getManaCost())
		{		
			p.getManaPool().remove(m);
		}
	}
	
	
	
	public class TotalCost {
		private LinkedList<Mana> manaCost;
		private LinkedList<ManaSymbol> manaCostSymbols;
		// TODO: add other costs

		public TotalCost(LinkedList<Mana> manaCost) {
			super();
			this.manaCost = manaCost;
		}		
		public TotalCost() {
			super();
		}
		


		public LinkedList<Mana> getManaCost() {
			return manaCost;
		}



		public void setManaCost(LinkedList<ManaSymbol> manaCost) {
			this.manaCost = new LinkedList<Mana>();
			
			for (ManaSymbol mSymbol : manaCost) {
				if(mSymbol.getSymbol().equals("{W}"))
				{
					this.manaCost.add(new Mana("white", "white", mSymbol));
				}
				else if(mSymbol.getSymbol().equals("{U}"))
				{
					this.manaCost.add(new Mana("blue", "blue", mSymbol));
				}
				else if(mSymbol.getSymbol().equals("{B}"))
				{
					this.manaCost.add(new Mana("black", "black", mSymbol));
				}
				else if(mSymbol.getSymbol().equals("{R}"))
				{
					this.manaCost.add(new Mana("red", "red", mSymbol));
				}
				else if(mSymbol.getSymbol().equals("{G}"))
				{
					this.manaCost.add(new Mana("green", "green", mSymbol));
				}
				else
				{
					int colorless = Integer.valueOf(mSymbol.getSymbol().substring(1, mSymbol.getSymbol().length()-1));
					for(int i = 0; i < colorless; i++)
					{
						this.manaCost.add(new Mana(null, "colorless", mSymbol));
					}
				}
			}
		}
	
	
	
		public LinkedList<ManaSymbol> getManaCostSymbols() {
			return manaCostSymbols;
		}

		public void setManaCostSymbols(LinkedList<ManaSymbol> manaCostSymbols) {
			this.manaCostSymbols = manaCostSymbols;
		}

		public void setManaCost() {
			/*
			String manaCost=ab.getAbilityText().split(":")[0];
			
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
			*/
			this.manaCost = new LinkedList<Mana>();
			
			for (ManaSymbol mSymbol : manaCostSymbols) {
				if(mSymbol.getSymbol().equals("{W}"))
				{
					this.manaCost.add(new Mana("white", "white", mSymbol));
				}
				else if(mSymbol.getSymbol().equals("{U}"))
				{
					this.manaCost.add(new Mana("blue", "blue", mSymbol));
				}
				else if(mSymbol.getSymbol().equals("{B}"))
				{
					this.manaCost.add(new Mana("black", "black", mSymbol));
				}
				else if(mSymbol.getSymbol().equals("{R}"))
				{
					this.manaCost.add(new Mana("red", "red", mSymbol));
				}
				else if(mSymbol.getSymbol().equals("{G}"))
				{
					this.manaCost.add(new Mana("green", "green", mSymbol));
				}
				else
				{
					int colorless = Integer.valueOf(mSymbol.getSymbol().substring(1, mSymbol.getSymbol().length()-1));
					for(int i = 0; i < colorless; i++)
					{
						this.manaCost.add(new Mana(null, "colorless", mSymbol));
					}
				}
			}
		}
	/////////////////////////

	}
}
