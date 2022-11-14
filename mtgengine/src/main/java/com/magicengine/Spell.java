package com.magicengine;

import java.util.LinkedList;
import java.util.List;

public class Spell extends MagicObject{
	
	private Card originCard;
	private TotalCost totalCost;
	private Boolean sufficientMana;
	private Boolean isPermanent;
	private Boolean faceDown;
	private int idStack;
	private int idPlayer=-1;
	
	//private Target targetted_obj = null; tolto!
	private int targetted_id=-1;
	private Target target;
	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	private ListPointer<String> stepCastingSpell;
	private Boolean instantSpell;
	
	

	public Spell(Card card, int idObject, int idController, int idStack)
	{
		super(card, idObject);		
		this.originCard = card;
		this.setIdController(idController);
		this.totalCost = null;
		this.sufficientMana = null;
		this.isPermanent = null;
		this.idStack = idStack;		
		this.stepCastingSpell = new ListPointer<String>(new LinkedList<String>());
	}
	
	// Costruttore per il PrivateGame (in caso di faceDown = true dovrà essere messa una creatura 2/2)
	public Spell(Spell spell, boolean faceDown)
	{
		super(spell, faceDown);
		if(faceDown)
			this.originCard 	= null;
		else
			this.originCard 	= spell.getOriginCard();
		this.sufficientMana = null;
		this.isPermanent 		= null;
		this.faceDown 			= spell.isFaceDown();
		this.idStack 				= spell.getIdStack();
		this.targetted_id		= spell.getTargetted_id();
		this.target					= spell.target;
	}

	public Card getOriginCard() {
		return originCard;
	}

	public void setOriginCard(Card originCard) {
		this.originCard = originCard;
	}
	
	public int getTargetted_id() {
		return targetted_id;
	}

	public void setTargetted_id(int targetted_id) {
		this.targetted_id = targetted_id;
	}

	

	@Override
	public String toString() {
		return "Spell [originCard=" + originCard + ", totalCost=" + totalCost + ", sufficientMana=" + sufficientMana
				+ ", isPermanent=" + isPermanent + ", faceDown=" + faceDown + ", idStack=" + idStack + ", idPlayer=" + idPlayer
				+ ", targetted_id=" + targetted_id + ", stepCastingSpell=" + stepCastingSpell + ", instantSpell=" + instantSpell
				+ "]";
	}

	public Boolean getInstantSpell() {
		return instantSpell;
	}
	
	public void setInstantSpell(Boolean instantSpell) {
		this.instantSpell = instantSpell;
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
	}
	
	public Boolean getSufficientMana() {
		return sufficientMana;
	}

	public void setSufficientMana(Boolean sufficientMana) {
		this.sufficientMana = sufficientMana;
	}
	
	public int getIdPlayer() {
		return idPlayer;
	}

	public void setIdPlayer(int idPlayer) {
		this.idPlayer = idPlayer;
	}
	
	public ListPointer<String> getStepCastingSpell() {
		return stepCastingSpell;
	}

	public void setStepCastingSpell(ListPointer<String> stepCastingSpell) {
		this.stepCastingSpell = stepCastingSpell;
	}

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
		for(Mana m : manaCost)
		{
			manaPool.remove(m);
		}
		return manaPool;
	}
	
	public void payMana(Player p)
	{
		for(Mana m : totalCost.getManaCost())
		{
			p.getManaPool().remove(m);
		}
	}

	public Boolean getIsPermanent() {
		return isPermanent;
	}

	public void setIsPermanent(Boolean isPermanent) {
		this.isPermanent = isPermanent;
	}

	public Boolean getFaceDown() {
		return faceDown;
	}

	public void setFaceDown(Boolean faceDown) {
		this.faceDown = faceDown;
	}

	public int getIdStack() {
		return idStack;
	}

	public void setIdStack(int idStack) {
		this.idStack = idStack;
	}

	public class TotalCost {
		private LinkedList<Mana> manaCost;
		// TODO: add other costs

		public TotalCost(LinkedList<Mana> manaCost) {
			super();
			this.manaCost = manaCost;
		}
		
		public TotalCost() {
			super();
			this.manaCost = new LinkedList<Mana>();
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
	
	}
}
