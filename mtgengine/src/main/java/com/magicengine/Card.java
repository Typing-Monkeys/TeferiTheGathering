package com.magicengine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.*;

import com.google.gson.Gson;

public class Card extends MagicObject{
	
	private int id; 
	private DBCard dbCard;	
	private boolean faceDown;
	private boolean castable;
	private int idExile; // Quando viene spostata una carta nell'exile, settarlo richiamando setIdExile(Game.getIdExileCounter())
	private int idGraveyard; // Quando viene spostata una carta nell'exile, settarlo richiamando setIdExile(Game.getIdGraveyardCounter())
	private boolean contieneAbilita = false;
	public Card(Card card, int idObject)
	{
		super(card, idObject);
		this.faceDown = card.isFaceDown();
		this.castable = card.isCastable();
		this.id = card.getId();
		this.dbCard = card.getDbCard();
		this.idExile = -1;
		this.idGraveyard = -1;
	
	}	
	
	public Card(int id, int idObject)
	{
		super(idObject);
		this.faceDown = true;
		this.castable = false;
		this.id = id;
		this.idExile = -1;
		this.idGraveyard = -1;
		try {

			//URL url = new URL("http://mtg.dmi.unipg.it/mancini/rest-server/api/imgFromCardid/" + id);
			//@GALT 2021
			URL url = new URL("https://www.afterlifegdr.com/test/mtg/testjson.php?cardid=" + id);

			Scanner scan = new Scanner(url.openStream());
			String jsonCard = scan.nextLine();
			//System.out.println(jsonCard); // stampa tutte le carte
			//System.out.println("leggo carta");
			Gson gson = new Gson();
			this.dbCard = gson.fromJson(jsonCard, DBCard.class);
			scan.close();
			if(dbCard.isSplitCard())
			{
				super.setName(dbCard.getFace1().getFaceName());
				super.setName(dbCard.getFace2().getFaceName());
				super.setManaCost(dbCard.getFace1().getManaCost());
				super.setManaCost(dbCard.getFace2().getManaCost());
				super.setColor(dbCard.getFace1().getColorIndicator());
				super.setColor(dbCard.getFace2().getColorIndicator());
				super.setColorIndicator(dbCard.getFace1().getColorIndicator());
				super.setColorIndicator(dbCard.getFace2().getColorIndicator());
				super.setCardType(dbCard.getFace1().getTypes());
				super.setCardType(dbCard.getFace2().getTypes());
				super.setSubtype(dbCard.getFace1().getSubtypes());
				super.setSubtype(dbCard.getFace2().getSubtypes());
				super.setSupertype(dbCard.getFace1().getSupertypes());
				super.setSupertype(dbCard.getFace2().getSupertypes());
				super.setAbilities(this.parseStringAbilities(dbCard.getFace1().getAbilities()));
				super.setAbilities(this.parseStringAbilities(dbCard.getFace2().getAbilities()));
				super.setKeywordAbilities(this.parseStringKeywordAbilities(dbCard.getFace1().getKeywordAbilities()));
				super.setKeywordAbilities(this.parseStringKeywordAbilities(dbCard.getFace2().getKeywordAbilities()));
				//super.setAbilities(2);
				// NON SETTA NIENTE; ABILITA' IMPOSTATE DA DROOLS
				// NON SETTA NIENTE; ABILITA' IMPOSTATE DA DROOLS
				// NON SETTA NIENTE; ABILITA' IMPOSTATE DA DROOLS
				//super.setAbilities(this.parseStringAbilities(dbCard.getFace1().getAbilities(), idObject)); //modificato
				//super.setAbilities(this.parseStringAbilities(dbCard.getFace2().getAbilities(), idObject)); //modificato
				
/*
 * bisogna ricreare le abilità da Java e poi le abilità come flying vanno settate da Drools
 * 1) da json 	-> DB card 	
 * 2) DBcard 	-> Card (con anche le abilità)
 * poi per ogni carta controllo se la carta contiene una keyword ability e gli assegno il mattoncino prefabbricato 			
 */
				
				super.setPower(dbCard.getFace1().getPower());
				super.setPower(dbCard.getFace2().getPower());
				super.setToughness(dbCard.getFace1().getToughness());
				super.setToughness(dbCard.getFace2().getToughness());
				super.setLoyalty(dbCard.getFace1().getLoyalty());
				super.setLoyalty(dbCard.getFace2().getLoyalty());
			}
			else
			{
				super.setName(dbCard.getFace1().getFaceName());
				super.setManaCost(dbCard.getFace1().getManaCost());
				super.setColor(dbCard.getFace1().getColorIndicator());
				super.setColorIndicator(dbCard.getFace1().getColorIndicator());
				super.setCardType(dbCard.getFace1().getTypes());
				super.setSubtype(dbCard.getFace1().getSubtypes());
				super.setSupertype(dbCard.getFace1().getSupertypes());

				super.setAbilities(this.parseStringAbilities(dbCard.getFace1().getAbilities())); //parseString abilities
				super.setKeywordAbilities(this.parseStringKeywordAbilities(dbCard.getFace1().getKeywordAbilities()));
				//super.setAbilities(1);
				super.setPower(dbCard.getFace1().getPower());
				super.setToughness(dbCard.getFace1().getToughness());
				super.setLoyalty(dbCard.getFace1().getLoyalty());
				super.setImage(String.format("%d", dbCard.getMultiverseid()));
			}
			
			//System.out.println("multiverse Id" + dbCard.getMultiverseid());
			System.out.println("Type: "+super.getCardType().toString() +"  Subtype:"+ super.getSubtype().toString());
			//System.out.println("carte["+id+"]='"+jsonCard+"';");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Costruttore per il PrivateGame
	public Card(Card card, boolean faceDown)
	{
		super(card, faceDown);
		this.faceDown = card.isFaceDown();
		if(faceDown) 
		{
			this.id = -1;
			this.castable = false;			
		}
		else
		{
			this.id = card.getId();
			this.castable = card.isCastable();
		}
		this.dbCard = null;
		this.idExile = card.getIdExile();
	}

	@Override
	public boolean isFaceDown() {
		return faceDown;
	}

	public void setFaceDown(boolean faceDown) {
		this.faceDown = faceDown;
	}
	
	public DBCard getDbCard() {
		return dbCard;
	}

	public void setDbCard(DBCard dbCard) {
		this.dbCard = dbCard;
	}
	
	@Override
	public int getIdController()
	{
		return getIdOwner();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isCastable() {
		return castable;
	}

	public void setCastable(boolean castable) {
		this.castable = castable;
	}

	@Override
	public String toString() {
		return "Card [id=" + id + ", dbCard=" + dbCard + ", faceDown=" + faceDown + ", castable=" + castable
				+ ", idExile=" + idExile + ", idGraveyard=" + idGraveyard + ", contieneAbilita=" + contieneAbilita
				+ "]";
	}
	public boolean isContieneAbilita() {
		return contieneAbilita;
	}

	public void setContieneAbilita(boolean contieneAbilita) {
		this.contieneAbilita = contieneAbilita;
	}

	public int getIdExile() {
		return idExile;
	}

	public void setIdExile(int idExile) {
		this.idExile = idExile;
	}

	public int getIdGraveyard() {
		return idGraveyard;
	}

	public void setIdGraveyard(int idGraveyard) {
		this.idGraveyard = idGraveyard;
	}
	

	
	private LinkedList<Ability> parseStringAbilities(LinkedList<String> abilities) {
		LinkedList<Ability> parsedAbilities = new LinkedList<Ability>();
			  for(String ab : abilities){				  
				  Ability nuova = new Ability(ab, false);
			      parsedAbilities.add(nuova);		      
			  }
		return parsedAbilities;
	}
	
	private LinkedList<Ability> parseStringKeywordAbilities(LinkedList<String> keywordAbilities) {
		LinkedList<Ability> parsedAbilities = new LinkedList<Ability>();
			  for(String ab : keywordAbilities){				  
				  Ability nuova = new Ability(ab, true);
			      parsedAbilities.add(nuova);		      
			  }
		return parsedAbilities;
	}
	
}
