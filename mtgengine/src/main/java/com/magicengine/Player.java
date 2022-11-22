package com.magicengine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Player implements Target {
	
	// Usate per indicare se un giocatore ha
	// vinto/perso/pareggiato un gioco. (Regole 104.1 - 104.6)
	public static final Integer PLAYER_WIN 	 = 1;
	public static final Integer PLAYER_DRAW  = 0;
	public static final Integer PLAYER_LOOSE = -1;
	
	private String nickname; // received from Node.js
	private int id; // received from Node.js
	private Zone deck; // received from Node.js //RULE 400.1
	private Zone sideboard; // received from Node.js
	private Zone hand; // RULE 400.1
	private Zone graveyard; // RULE 400.1
	private Zone library; // deck shuffled //RULE 400.1
	private Action action; // attempt accepted
	private Turn turn;
	private ArrayList<Integer> sideBoard; // Da implementare lato Node
	private LinkedList<Player> opponentsList;
	private LinkedList<Player> teamMates;
	private LinkedList<Card> scryedCards;
	//private Player opponent;		//Obsoleto
	private int lifeTotal;
	private int combatDamage;
	private int damageDealt;
	private int poisonCounter;
	private int startingHandSize;
	private int maxHandSize=7; 
	private int initialCardHand;
	private LinkedList<Mana> manaPool;
	private LinkedList<MagicObject> triggeredAbilities; //rule 116.2a, 503.1
	//rule 305.2a ToDo: implementare modificatori
	private boolean wantsPlayLand;
	private boolean emptyLibrary;
	private Integer hasWin;
	
	private int maxPlayableLands; //see rule 505.5b
	private int playedLands; //see rule 505.5b
	//il giocatore decide quale carta eliminare
	
	//regola per i target
	private boolean wantSelectTarget;
	private int magicTargetId=-1;
	
	//numero dei mulligan usati dal giocatore
	private int mulliganCounter=0; 
	
	private boolean discarding = false; // true se il giocatore è in fase di scarto mulligan
	
	//Variabili per la gestione della regola 514.1
	private boolean discardToMaxHandSize = false; // true se il giocatore deve scartare fino ad avere 7 carte in mano
	private boolean beginningDiscardingToMaxHandSize = true; // serve per impedire di creare infinite choiceAnswer
	
	private LinkedList<Integer> attached_by;
	private LinkedList<Permanent> attby_perm;
	
	
	// Class TEAM

	public Player() {
		// create player zone, TODO: in drools -> rule 400.1
		this.deck = new Zone("deck",false);
		this.library=new Zone("library",false);
		this.sideboard = new Zone("sideboard",false);
		this.graveyard = new Zone("graveyard",true);
		this.hand=new Zone("hand",false);
		// create turn
		this.turn=new Turn(false);
		//init triggeredAbilities
		this.triggeredAbilities = new LinkedList<MagicObject>();
		
		this.lifeTotal = -1;
		this.combatDamage = 0;
		this.damageDealt = 0;
		this.startingHandSize = -1;
		this.poisonCounter = 0;
		
		this.manaPool = new LinkedList<Mana>();
		
		this.teamMates = new LinkedList<Player>();
		this.scryedCards = new LinkedList<Card>();
		this.opponentsList = new LinkedList<Player>();
		
		this.maxPlayableLands = 1;
		this.playedLands = 0;
		this.wantsPlayLand = false;
		this.wantSelectTarget = false;
		this.emptyLibrary = false;
	}
	
	public void draw()
	{
		//String name = this.library.pop().getName();
		try {
			this.hand.push(this.library.pop());
		} catch(NoSuchElementException ne) {
			this.setEmptyLibrary(true);
		}
	}
	
	public void draw(int n)
	{
		try {
			for(int i = 1; i <= n; i++)
			{
				draw();
			}
		} catch(NoSuchElementException ne) {
			this.setEmptyLibrary(true);
		}
	}
	
	public void takeMulligan(int mulliganType)
	{
		setMulliganCounter(getMulliganCounter() + 1);
		//CONTROLLARE I TIPI DI MULLIGAN E METTERE IN MANO IL NUMERO DI MANI CORRETTE 
		if(mulliganType== Game.VANCOUVER_MULLIGAN || mulliganType== Game.PARIS_MULLIGAN) {
			//
			int actualHandSize = this.hand.size();
			for(int i = 0; i < actualHandSize; i++)
			{
				this.library.push(this.hand.pop());
			}
			this.library.shuffle();
			this.draw(actualHandSize-1); //si pescano actualHandSize-1 carte
		}
		else {
			//MULLIGAN LONDON
			int actualHandSize = this.hand.size();
			for(int i = 0; i < actualHandSize; i++)
			{
				this.library.push(this.hand.pop());
			}
			this.library.shuffle();
			//Nel London vengono date maxHandSize carte e poi se ne scartano mulliganTaken
			this.draw(actualHandSize); //si pescano actualHandSize carte
		}	
	}
	
	/**
	 * @author Nicolò Posta, Tommaso Romani, Nicolò Vescera
	 * 
	 * Rimuove dalla mano del giocatore la carta specificata (se presente)
	 * e la posiziona in fondo alla libreria
	 *  N.B.: questa regola viene utilizzata (per ora) solo per il mulligan !!
	 *
	 * @param card la carta da mettere in fondo la mazzo
	 */
	public void sendToBottomLibrary(MagicObject card) {
		boolean discarded = this.hand.remove(card);
		if (discarded) {
			this.library.add(card);
		}
	}
	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Zone getHand() {
		return hand;
	}

	public void setHand(Zone hand) {
		this.hand = hand;
	}

	public Zone getGraveyard() {
		return graveyard;
	}

	public void setGraveyard(Zone graveyard) {
		this.graveyard = graveyard;
	}

	public Zone getDeck() {
		return deck;
	}

	public void setDeck(Zone deck) {
		this.deck = deck;
	}
	
	public Zone getSideboard() {
		return sideboard;
	}

	public void setSideboard(Zone sideboard) {
		this.sideboard = sideboard;
	}

	public Zone getLibrary() {
		return library;
	}

	public void setLibrary(Zone library) {
		this.library = library;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Turn getTurn() {
		return turn;
	}

	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	public ArrayList<Integer> getSideBoard() {
		return sideBoard;
	}

	public void setSideBoard(ArrayList<Integer> sideBoard) {
		this.sideBoard = sideBoard;
	}

	public LinkedList<Player> getOpponentsList() {
		return opponentsList;
	}

	public void setOpponentsList(LinkedList<Player> opponentsList) {
		this.opponentsList = opponentsList;
	}

	public LinkedList<Player> getTeamMates() {
		return teamMates;
	}

	public void setTeamMates(LinkedList<Player> teamMates) {
		this.teamMates = teamMates;
	}
	
	public LinkedList<Card> getScryedCards() {
		return scryedCards;
	}

	public void setScryedCards(LinkedList<Card> scryedCards) {
		this.scryedCards = scryedCards;
	}

	public int getStartingHandSize() {
		return startingHandSize;
	}

	public void setStartingHandSize(int startingHandSize) {
		this.startingHandSize = startingHandSize;
	}

	public int getMaxHandSize() {
		return maxHandSize;
	}

	public void setMaxHandSize(int maxHandSize) {
		this.maxHandSize = maxHandSize;
	}

	public int getInitialCardHand() {
		return initialCardHand;
	}

	public void setInitialCardHand(int initialCardHand) {
		this.initialCardHand = initialCardHand;
	}

	public LinkedList<Mana> getManaPool() {
		return manaPool;
	}

	public void setManaPool(LinkedList<Mana> manaPool) {
		this.manaPool = manaPool;
	}
	
	public String printManaPool()
	{
		String mp = "";
		
		int cont_White = 0;
		int cont_Blue = 0;
		int cont_Black = 0;
		int cont_Red = 0;
		int cont_Green = 0;
		int count_Colorless = 0;
		
		for(Mana m : manaPool)
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

	public LinkedList<MagicObject> getTriggeredAbilities() {
		return triggeredAbilities;
	}

	public void setTriggeredAbilities(LinkedList<MagicObject> triggeredAbilities) {
		this.triggeredAbilities = triggeredAbilities;
	}

	public int getMaxPlayableLands() {
		return maxPlayableLands;
	}

	public void setMaxPlayableLands(int maxPlayableLands) {
		this.maxPlayableLands = maxPlayableLands;
	}

	public int getPlayedLands() {
		return playedLands;
	}

	public void setPlayedLands(int playedLands) {
		this.playedLands = playedLands;
	}

	public boolean isWantsPlayLand() {
		return wantsPlayLand;
	}

	public boolean isWantSelectTarget() {
		return wantSelectTarget;
	}

	public void setWantSelectTarget(boolean wantSelectTarget) {
		this.wantSelectTarget = wantSelectTarget;
	}

	public void setWantsPlayLand(boolean wantsPlayLand) {
		this.wantsPlayLand = wantsPlayLand;
	}

	public int getLifeTotal() {
		return lifeTotal;
	}

	public void setLifeTotal(int lifeTotal) {
		this.lifeTotal = lifeTotal;
	}

	public int getCombatDamage() {
		return combatDamage;
	}

	public void setCombatDamage(int combatDamage) {
		this.combatDamage = combatDamage;
	}

	public int getDamageDealt() {
		return damageDealt;
	}

	public void setDamageDealt(int damageDealt) {
		this.damageDealt = damageDealt;
	}

	public int getPoisonCounter() {
		return poisonCounter;
	}

	public void setPoisonCounter(int poisonCounter) {
		this.poisonCounter = poisonCounter;
	}

	public Integer getHasWin() {
		return hasWin;
	}

	public void setHasWin(Integer hasWin) {
		this.hasWin = hasWin;
	}

	public boolean isEmptyLibrary() {
		return emptyLibrary;
	}

	public void setEmptyLibrary(boolean emptyLibrary) {
		this.emptyLibrary = emptyLibrary;
	}

	public int getMagicTargetId() {
		return magicTargetId;
	}

	public void setMagicTargetId(int magicTargetId) {
		this.magicTargetId = magicTargetId;
	}

	public int getMulliganCounter() {
		return mulliganCounter;
	}

	public void setMulliganCounter(int mulliganCounter) {
		this.mulliganCounter = mulliganCounter;
	}

	public LinkedList<Permanent> getAttby_perm() {
		return attby_perm;
	}

	public void setAttby_perm(LinkedList<Permanent> attby_perm) {
		this.attby_perm = attby_perm;
	}

	public LinkedList<Integer> getAttached_by() {
		return attached_by;
	}

	public void setAttached_by(LinkedList<Integer> attached_by) {
		this.attached_by = attached_by;
	}

	public boolean isDiscarding() {
		return discarding;
	}

	public void setDiscarding(boolean discarding) {
		this.discarding = discarding;
	}

	public boolean isDiscardToMaxHandSize() {
		return discardToMaxHandSize;
	}

	public void setDiscardToMaxHandSize(boolean discardToMaxHandSize) {
		this.discardToMaxHandSize = discardToMaxHandSize;
	}

	public boolean isBeginningDiscardingToMaxHandSize() {
		return beginningDiscardingToMaxHandSize;
	}

	public void setBeginningDiscardingToMaxHandSize(boolean beginningDiscardingToMaxHandSize) {
		this.beginningDiscardingToMaxHandSize = beginningDiscardingToMaxHandSize;
	}
	
}