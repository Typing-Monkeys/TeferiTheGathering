package com.magicengine;

import java.util.ArrayList;
import java.util.LinkedList;

public class MagicObject implements Target{
	
	//Characteristics
	private ArrayList<String> name;
	private ArrayList<LinkedList<ManaSymbol>> manaCost;
	private ArrayList<LinkedList<String>> color;
	private ArrayList<LinkedList<String>> colorIndicator;
    private ArrayList<LinkedList<String>> cardType;
	private ArrayList<LinkedList<String>> subtype;
	private ArrayList<LinkedList<String>> supertype;
	private ArrayList<String> rulesText;
	protected ArrayList<LinkedList<Ability>> abilities=new ArrayList<LinkedList<Ability>>(2);
	protected ArrayList<LinkedList<Ability>> keywordAbilities=new ArrayList<LinkedList<Ability>>(2);
	private ArrayList<String> power;
	private ArrayList<String> toughness;
	private ArrayList<String> loyalty;
	private ArrayList<String> handModifier;
	private ArrayList<String> lifeModifier;
	private ArrayList<String> image;


	//Not Characteristics
	private int magicTargetId; 	// rinomina magicObjectID, in modo da uniformare con il resto degli oggetti
	private int idOwner; 		// Non su tutti gli oggetti
	private int idController; 	// Non su tutti gli oggetti



	public MagicObject(ArrayList<String> name, ArrayList<LinkedList<ManaSymbol>> manaCost, ArrayList<LinkedList<String>> color,
			ArrayList<LinkedList<String>> colorIndicator, ArrayList<LinkedList<String>> cardType, ArrayList<LinkedList<String>> subtype,
			ArrayList<LinkedList<String>> supertype, ArrayList<String> rulesText, ArrayList<LinkedList<Ability>> abilities, ArrayList<LinkedList<Ability>> keywordAbilities, ArrayList<String> power,
			ArrayList<String> toughness, ArrayList<String> loyalty, ArrayList<String> handModifier, ArrayList<String> lifeModifier,
			int idOwner, int idController, int magicTargetId) {
		super();
		this.name = name;
		this.manaCost = manaCost;
		this.color = color;
		this.colorIndicator = colorIndicator;
		this.cardType = cardType;
		this.subtype = subtype;
		this.supertype = supertype;
		this.rulesText = rulesText;
		this.abilities = abilities;
		this.keywordAbilities = keywordAbilities;
		this.power = power;
		this.toughness = toughness;
		this.loyalty = loyalty;
		this.handModifier = handModifier;
		this.lifeModifier = lifeModifier;
		this.idOwner = idOwner;
		this.idController = idController;
		this.magicTargetId = magicTargetId;
	}
	
	public MagicObject(MagicObject obj, int magicTargetId)
	{
		super();
		this.name = obj.getName();
		this.manaCost = obj.getManaCost();
		this.color = obj.getColor();
		this.colorIndicator = obj.getColorIndicator();
		this.cardType = obj.getCardType();
		this.subtype = obj.getSubtype();
		this.supertype = obj.getSupertype();
		this.rulesText = obj.getRulesText();
		this.abilities = obj.getAbilities();
		this.keywordAbilities = obj.getKeywordAbilities();
		this.power = obj.getPower();
		this.toughness = obj.getToughness();
		this.loyalty = obj.getLoyalty();
		this.handModifier = obj.getHandModifier();
		this.lifeModifier = obj.getLifeModifier();
		this.idOwner = obj.getIdOwner();
		this.idController = obj.getIdController();
		this.magicTargetId = magicTargetId;
	}
	// Creato costruttore con id = idObj
	public MagicObject(MagicObject obj, boolean faceDown, int idObject)
	{
		super();
		if(faceDown){
			this.name = new ArrayList<String>(2);
			this.manaCost = new ArrayList<LinkedList<ManaSymbol>>(2);
			this.color = new ArrayList<LinkedList<String>>(2);
			this.colorIndicator = new ArrayList<LinkedList<String>>(2);
			this.cardType = new ArrayList<LinkedList<String>>(2);
			this.subtype = new ArrayList<LinkedList<String>>(2);
			this.supertype = new ArrayList<LinkedList<String>>(2);
			this.rulesText = new ArrayList<String>(2);
			this.abilities = new ArrayList<LinkedList<Ability>>(2);
			this.keywordAbilities = new ArrayList<LinkedList<Ability>>(2);
			this.power = new ArrayList<String>(2);
			this.toughness = new ArrayList<String>(2);
			this.loyalty = new ArrayList<String>(2);
			this.handModifier = new ArrayList<String>(2);
			this.lifeModifier = new ArrayList<String>(2);
			this.image = new ArrayList<String>(2);
		}
		else
		{
			this.name = obj.getName();
			this.manaCost = obj.getManaCost();
			this.color = obj.getColor();
			this.colorIndicator = obj.getColorIndicator();
			this.cardType = obj.getCardType();
			this.subtype = obj.getSubtype();
			this.supertype = obj.getSupertype();
			this.rulesText = obj.getRulesText();
			this.abilities = obj.getAbilities();
			this.keywordAbilities = obj.getKeywordAbilities();
			this.power = obj.getPower();
			this.toughness = obj.getToughness();
			this.loyalty = obj.getLoyalty();
			this.handModifier = obj.getHandModifier();
			this.lifeModifier = obj.getLifeModifier();
		}
		this.idOwner = obj.getIdOwner();
		this.idController = obj.getIdController();
		//this.magicTargetId = magicTargetId; //La differenza è qui
		this.magicTargetId = obj.getMagicTargetId(); //La differenza è qui
	}
	
	// Costruttore per il PrivateGame
	public MagicObject(MagicObject obj, boolean faceDown) // bisogna settare i timestamp quando si creano gli oggetti
	{
		super();
		if(faceDown){
			this.name = new ArrayList<String>(2);
			this.manaCost = new ArrayList<LinkedList<ManaSymbol>>(2);
			this.color = new ArrayList<LinkedList<String>>(2);
			this.colorIndicator = new ArrayList<LinkedList<String>>(2);
			this.cardType = new ArrayList<LinkedList<String>>(2);
			this.subtype = new ArrayList<LinkedList<String>>(2);
			this.supertype = new ArrayList<LinkedList<String>>(2);
			this.rulesText = new ArrayList<String>(2);
			this.abilities = new ArrayList<LinkedList<Ability>>(2);
			this.keywordAbilities = new ArrayList<LinkedList<Ability>>(2);
			this.power = new ArrayList<String>(2);
			this.toughness = new ArrayList<String>(2);
			this.loyalty = new ArrayList<String>(2);
			this.handModifier = new ArrayList<String>(2);
			this.lifeModifier = new ArrayList<String>(2);
			this.image = new ArrayList<String>(2);
		}
		else
		{
			this.name = obj.getName();
			this.manaCost = obj.getManaCost();
			this.color = obj.getColor();
			this.colorIndicator = obj.getColorIndicator();
			this.cardType = obj.getCardType();
			this.subtype = obj.getSubtype();
			this.supertype = obj.getSupertype();
			this.rulesText = obj.getRulesText();
			this.abilities = obj.getAbilities();
			this.keywordAbilities = obj.getKeywordAbilities();
			this.power = obj.getPower();
			this.toughness = obj.getToughness();
			this.loyalty = obj.getLoyalty();
			this.handModifier = obj.getHandModifier();
			this.lifeModifier = obj.getLifeModifier();
		}
		this.idOwner = obj.getIdOwner();
		this.idController = obj.getIdController();
		this.magicTargetId = -1;
	}
	
	public MagicObject(int idObject)
	{
		this.name = new ArrayList<String>(2);
		this.manaCost = new ArrayList<LinkedList<ManaSymbol>>(2);
		this.color = new ArrayList<LinkedList<String>>(2);
		this.colorIndicator = new ArrayList<LinkedList<String>>(2);
		this.cardType = new ArrayList<LinkedList<String>>(2);
		this.subtype = new ArrayList<LinkedList<String>>(2);
		this.supertype = new ArrayList<LinkedList<String>>(2);
		this.rulesText = new ArrayList<String>(2);
		this.abilities = new ArrayList<LinkedList<Ability>>(2);
		this.keywordAbilities = new ArrayList<LinkedList<Ability>>(2);
		this.power = new ArrayList<String>(2);
		this.toughness = new ArrayList<String>(2);
		this.loyalty = new ArrayList<String>(2);
		this.handModifier = new ArrayList<String>(2);
		this.lifeModifier = new ArrayList<String>(2);
		this.image = new ArrayList<String>(2);
		this.idOwner = -1;
		this.idController = -1;
		this.magicTargetId = idObject; 
	}


	public ArrayList<String> getName() {
		return name;
	}
	
	
	public String getNameAsString() {
		String output = name.get(0);
		if(name.size() > 1)
		{
			output = output + " / " + name.get(1);
		}
		return output;
	}


	public void setName(ArrayList<String> name) {
		this.name = name;
	}
	
	public void setName(String name)  {
		this.name.add(name);
	}


	public ArrayList<LinkedList<ManaSymbol>> getManaCost() {
		return manaCost;
	}


	public void setManaCost(ArrayList<LinkedList<ManaSymbol>> manaCost) {
		this.manaCost = manaCost;
	}
	
	public void setManaCost(String manaCost) {
		LinkedList<ManaSymbol> manaCostSymbols = new LinkedList<ManaSymbol>();
		
		if(!manaCost.isEmpty()) {
			LinkedList<String> splitted = new LinkedList<String>();
			
			String temp = "";
			for(int i = 0; i < manaCost.length(); i++) {
				char c = manaCost.charAt(i);
				if(c == '{') {
					temp = "";
					temp += c;
				} else if(c == '}') {
					temp += c;
					splitted.add(temp);
				} else {
					temp += c;
				}
			}
			
			for(String m : splitted) {
				manaCostSymbols.add(new ManaSymbol(m));
			}
			
			this.manaCost.add(manaCostSymbols);
		}
	}


	public ArrayList<LinkedList<String>> getColor() {
		return color;
	}


	public void setColor(ArrayList<LinkedList<String>> color) {
		this.color = color;
	}
	
	
	public void setColor(LinkedList<String> color) {
		this.color.add(color);
	}


	public ArrayList<LinkedList<String>> getColorIndicator() {
		return colorIndicator;
	}


	public void setColorIndicator(LinkedList<String> colorIndicator) {
		this.colorIndicator.add(colorIndicator);
	}
	
	
	public void setColorIndicator(ArrayList<LinkedList<String>> colorIndicator) {
		this.colorIndicator = colorIndicator;
	}


	public ArrayList<LinkedList<String>> getCardType() {
		ArrayList<LinkedList<String>> lowerCard = (ArrayList<LinkedList<String>>) cardType.clone();
		//for(int i=0 ; i<lowerCard.size(); i++)
		lowerCard.get(0).set(0, lowerCard.get(0).get(0).toLowerCase());
		return lowerCard;
	}


	public void setCardType(LinkedList<String> cardType) {
		this.cardType.add(cardType);
	}
	
	
	public void setCardType(ArrayList<LinkedList<String>> cardType) {
		this.cardType = cardType;
	}


	public ArrayList<LinkedList<String>> getSubtype() {
		return subtype;
	}


	public void setSubtype(ArrayList<LinkedList<String>> subtype) {
		this.subtype = subtype;
	}
	
	
	public void setSubtype(LinkedList<String> subtype) {
		this.subtype.add(subtype);
	}


	public ArrayList<LinkedList<String>> getSupertype() {
		return supertype;
	}


	public void setSupertype(ArrayList<LinkedList<String>> supertype) {
		this.supertype = supertype;
	}
	
	
	public void setSupertype(LinkedList<String> supertype) {
		this.supertype.add(supertype);
	}


	public ArrayList<String> getRulesText() {
		return rulesText;
	}


	public void setRulesText(ArrayList<String> rulesText) {
		this.rulesText = rulesText;
	}
	

	public void setRulesText(String rulesText) {
		this.rulesText.add(rulesText);
	}

	public ArrayList<LinkedList<Ability>> getAbilities() {
		
		return abilities;
	}


	public void setAbilities(ArrayList<LinkedList<Ability>> abilities) {
		this.abilities = abilities;
	}
	
	public void setAbilities(LinkedList<Ability> abilities) {
		this.abilities.add(abilities);
	}	
	
	public void setAbilities(Ability abilities,int faccia) {
		this.abilities.get(faccia).add(abilities);
	}

	public ArrayList<LinkedList<Ability>> getKeywordAbilities() {
		return keywordAbilities;
	}

	public void setKeywordAbilities(ArrayList<LinkedList<Ability>> keywordAbilities) {
		this.keywordAbilities = keywordAbilities;
	}
	
	public void setKeywordAbilities(LinkedList<Ability> keywordAbilities) {
		this.keywordAbilities.add(keywordAbilities);
	}	

	public ArrayList<String> getPower() {
		return power;
	}


	public void setPower(ArrayList<String> power) {
		this.power = power;
	}
	
	
	public void setPower(String power) {
		this.power.add(power);
	}


	public ArrayList<String> getToughness() {
		return toughness;
	}


	public void setToughness(ArrayList<String> toughness) {
		this.toughness = toughness;
	}
	
	
	public void setToughness(String toughness) {
		this.toughness.add(toughness);
	}


	public ArrayList<String> getLoyalty() {
		return loyalty;
	}


	public void setLoyalty(ArrayList<String> loyalty) {
		this.loyalty = loyalty;
	}
	
	
	public void setLoyalty(String loyalty) {
		this.loyalty.add(loyalty);
	}


	public ArrayList<String> getHandModifier() {
		return handModifier;
	}


	public void setHandModifier(ArrayList<String> handModifier) {
		this.handModifier = handModifier;
	}
	
	
	public void setHandModifier(String handModifier) {
		this.handModifier.add(handModifier);
	}


	public ArrayList<String> getLifeModifier() {
		return lifeModifier;
	}


	public void setLifeModifier(ArrayList<String> lifeModifier) {
		this.lifeModifier = lifeModifier;
	}
	
	
	public void setLifeModifier(String lifeModifier) {
		this.lifeModifier.add(lifeModifier);
	}
	
	public ArrayList<String> getImage() {
		return image;
	}

	public void setImage(ArrayList<String> image) {
		this.image = image;
	}
	
	public void setImage(String image) {
		this.image.add(image);
	}
	
	public int getIdOwner() {
		return idOwner;
	}
	

	public void setIdOwner(int idOwner) {
		this.idOwner = idOwner;
	}
	

	public int getIdController() {
		return idController;
	}
	

	public void setIdController(int idController) {
		this.idController = idController;
	}
	

	public int getMagicTargetId() {
		return magicTargetId;
	}
	

	public void setMagicTargetId(int magicTargetId) {
		this.magicTargetId = magicTargetId;
	}
	

	public boolean isFaceDown()
	{
		return false;
	}

	@Override
	public String toString() {
		return "MagicObject [name=" + name + ", manaCost=" + manaCost + ", color=" + color + ", colorIndicator="
				+ colorIndicator + ", cardType=" + cardType + ", subtype=" + subtype + ", supertype=" + supertype
				+ ", rulesText=" + rulesText + ", abilities=" + abilities + ", keywordAbilities=" + keywordAbilities + ", power=" + power + ", toughness=" + toughness
				+ ", loyalty=" + loyalty + ", handModifier=" + handModifier + ", lifeModifier=" + lifeModifier + ", image="
				+ image + ", magicTargetId=" + magicTargetId + ", idOwner=" + idOwner + ", idController=" + idController + "]";
	}


	
	
	
}
