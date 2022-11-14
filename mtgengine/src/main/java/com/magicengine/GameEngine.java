package com.magicengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.kie.api.runtime.rule.FactHandle;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class GameEngine extends Thread {
	// CLASS VARIABLES
	private Socket clientSocket;
	private static PrintWriter output = null;
	private BufferedReader input;
	private char buffer[];
	// JSON HANDLER
	private static Gson gson;
	private JsonReader jsonReader;
	private JsonParser jsonParser;
	private JsonElement jsonElement;
	private JsonObject jsonObject;
	// MAGIC GAME OBJECT
	private Game game;
	private DroolsEngine droolsEngine;
	

	// private FactHandle fh;

	public GameEngine(Socket s) {
		this.clientSocket = s;
		this.buffer = new char[1024];
		GameEngine.gson = new Gson();
		this.jsonParser = new JsonParser();
		this.droolsEngine = new DroolsEngine();
		this.game = new Game(); 
	}

	/**
	 * Get entire json from buffer
	 * 
	 * @param buffer
	 * @return json from buffer
	 */
	String getJson(String json) {
		return getJson(json, null);
	}

	/**
	 * Get json specified by token from buffer
	 * 
	 * @param buffer
	 * @param token
	 *            special string in json message
	 * @return associated json
	 */
	String getJson(String json, String token) {
		if (token != null) {
			jsonReader = new JsonReader(new StringReader(json));
			jsonElement = jsonParser.parse(jsonReader);
			jsonObject = jsonElement.getAsJsonObject();
			try {
				json = jsonObject.get(token).toString();
			} catch (NullPointerException e) {
				json = null;
			}
		}
		return json;
	}
	
	public static ArrayList<String> splitEqually(String text, int size) {
		ArrayList<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

	    for (int start = 0; start < text.length(); start += size) {
	        ret.add(text.substring(start, Math.min(text.length(), start + size)));
	    }
	    return ret;
	}
	
	public LinkedList<ManaSymbol> makeManaCost(String manaCost) {
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
				}else if(c == '0') {
					
				} else {
					temp += c;
				}
			}
			
			for(String m : splitted) {
				manaCostSymbols.add(new ManaSymbol(m));
			}
			
			return manaCostSymbols;
		}else{
			return null;
		}
	}

	public static void sendToNode(Object toSend) {
		String message = "";
		if(toSend instanceof String)
		{
			message = gson.toJson(new JSONMessageType(Game.TEXT_MESSAGE, toSend));
		}
		else
		{
			message = gson.toJson(new JSONMessageType(Game.TEXT_MESSAGE, gson.toJson(toSend)));
		}
		
		if(message.length() > 1000)
		{
			ArrayList<String> newMessages = splitEqually(message, 1000);
			for(int i = 0; i < newMessages.size(); i++)
			{
				String newMessage = newMessages.get(i);
				String msg = gson.toJson(new JSONMessage(0, newMessage, newMessages.size()-i-1));
				output.println(msg);
				//System.out.println(msg);
			}
		}
		else
		{
			String msg = gson.toJson(new JSONMessage(0, message, 0));
			output.println(msg);
			//System.out.println(msg);
		}
			
	}
	
	public static void sendToNode(Object toSend, int messageType, int messageSubtype) {
		String message = "";
		if(toSend instanceof String)
		{
			message = gson.toJson(new JSONMessageType(messageType, messageSubtype, toSend));
		}
		else
		{
			message = gson.toJson(new JSONMessageType(messageType, messageSubtype, gson.toJson(toSend)));
		}
		
		if(message.length() > 1000)
		{
			ArrayList<String> newMessages = splitEqually(message, 1000);
			for(int i = 0; i < newMessages.size(); i++)
			{
				String newMessage = newMessages.get(i);
				String msg = gson.toJson(new JSONMessage(0, newMessage, newMessages.size()-i-1));
				output.println(msg);
				//System.out.println(msg);
			}
		}
		else
		{
			String msg = gson.toJson(new JSONMessage(0, message, 0));
			output.println(msg);
			//System.out.println(msg);
		}
	}
	
	public static void sendToNode(Object toSend, int messageType, int messageSubtype, int idPlayer) {
		String message = "";
		if(toSend instanceof String)
		{
			message = gson.toJson(new JSONMessageType(messageType, messageSubtype, toSend));
		}
		else
		{
			message = gson.toJson(new JSONMessageType(messageType, messageSubtype, gson.toJson(toSend)));
		}

		if(message.length() > 1000)
		{
			ArrayList<String> newMessages = splitEqually(message, 1000);
			for(int i = 0; i < newMessages.size(); i++)
			{
				String newMessage = newMessages.get(i);
				String msg = gson.toJson(new JSONMessage(idPlayer, newMessage, newMessages.size()-i-1));
				output.println(msg);
				//System.out.println(msg);
			}
		}
		else
		{
			String msg = gson.toJson(new JSONMessage(idPlayer, message, 0));
			output.println(msg);
			//System.out.println(msg);
		}
	}

	void exit(String json) {
		int idPlayer = gson.fromJson(json, int.class);
		for (Player p : game.getPriorityOrder()) {
			if (p.getId() == idPlayer) {
				sendToNode("player " + p.getNickname() + " has disconnected, VICTORY!");
				break;
			}
		}
	}

	void initPlayer(String json) {
		boolean isNewPlayer = false;
		int position = 0;
		// get player info
		String playerInfo = getJson(json, "playerInfo");
		//System.out.println(playerInfo);
		Player receivedPlayer = new Player();
		receivedPlayer = gson.fromJson(playerInfo, Player.class);
		receivedPlayer.setMagicTargetId(game.giveMeMyId());
		
		// get deck
		
		String playerDeck = getJson(json, "playerDeck");
		int[] deck = gson.fromJson(playerDeck, int[].class);
		// save deck
		for (int i : deck) {
			receivedPlayer.getDeck().add(new Card(i, game.giveMeMyId()));
		}
		

		//get sideboard
		String playerSideboard = getJson(json, "playerSideboard");
		int[] sideboard = gson.fromJson(playerSideboard, int[].class);
		//save sideboard
		for (int i : sideboard) {
			receivedPlayer.getSideboard().add(new Card(i, game.giveMeMyId()));
		}

		// check if it's present
		for (Player py : game.getPriorityOrder()) {
			if (py.getId() == receivedPlayer.getId()) {
				game.getPriorityOrder().set(position, receivedPlayer);
				isNewPlayer = true;
				break;
			}
			position++;
		}
		if (!isNewPlayer) {
			game.getPriorityOrder().add(receivedPlayer);
		}
		System.out.println("size: " + game.getPriorityOrder().size());
		if(game.getPriorityOrder().size() == game.getPlayersNumber())
		{
			sendToNode("readyStart", 1, 1);
		}
	}

	private String cleanBuffer(char buffer[]) {
		String toString = new String(buffer);
		String splitted[] = toString.split("#");
		return toString = splitted[1].trim();
	}

	@Override
	public void run() {
		System.out.println("Partita creata");
		String received;
		String cleanJson;
		int n;
		try {
			// open streams
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			output = new PrintWriter(clientSocket.getOutputStream(), true);

			// FASE 1 RECEIVE INFO
			while (true) {
				Arrays.fill(buffer, ' ');
				n = input.read(buffer);
				System.out.println(buffer);

				if (n != -1) {
					cleanJson = cleanBuffer(buffer);
					// JSON "NUMBER PLAYERS"
					received = getJson(cleanJson, "playersNumber");
					if (received != null) {
						game.setPlayersNumber(gson.fromJson(received, int.class));
						System.out.println("Numero Giocatori: " + game.getPlayersNumber());
						sendToNode("readySPD", 1, 1);
						
					}
					// JSON "PLAYERS SETTINGS"
					received = getJson(cleanJson, "playerSettings");
					System.out.println(received);
					if (received != null) {
						String twoPlayerJ = getJson(received, "twoplayer");
						int twoPlayer = gson.fromJson(twoPlayerJ, int.class);
						if(twoPlayer == 1)
							game.setTwoPlayerGame(true);
						else
							game.setTwoPlayerGame(false);
						
						String variantJ = getJson(received, "variant");
						int variant = gson.fromJson(variantJ, int.class);
						game.setVariant(variant);
						
						String casualVariantJ = getJson(received, "c_variant");
						int casualVariant = gson.fromJson(casualVariantJ, int.class);
						game.setCasualVariant(casualVariant);
						
						String limitedRangeOfInfluenceJ = getJson(received, "lroi");
						int limitedRangeOfInfluence = gson.fromJson(limitedRangeOfInfluenceJ, int.class);
						if(limitedRangeOfInfluence == 1)							
							game.setLimitedRangeOfInfluenceOption(true);
						else
							game.setLimitedRangeOfInfluenceOption(false);
						
						String rangeOfInfluenceJ = getJson(received, "roi");
						int rangeOfInfluence = gson.fromJson(rangeOfInfluenceJ, int.class);
						game.setRangeOfInfluence(rangeOfInfluence);
						
						String attackMultiplePlayersOprionJ = getJson(received, "amp");
						int attackMultiplePlayersOption = gson.fromJson(attackMultiplePlayersOprionJ, int.class);
						if(attackMultiplePlayersOption == 1)
							game.setAttackMultiplePlayersOption(true);
						else
							game.setAttackMultiplePlayersOption(false);
						
						String attackLeftOptionJ = getJson(received, "al");
						int attackLeftOption = gson.fromJson(attackLeftOptionJ, int.class);
						if(attackLeftOption == 1)
							game.setAttackLeftOption(true);
						else
							game.setAttackLeftOption(false);
						
						String attackRightOptionJ = getJson(received, "ar");
						int attackRightOption = gson.fromJson(attackRightOptionJ, int.class);
						if(attackRightOption == 1)
							game.setAttackRightOption(true);
						else
							game.setAttackRightOption(false);
						
						String deployCreatureOptionJ = getJson(received, "dc");
						int deployCreatureOption = gson.fromJson(deployCreatureOptionJ, int.class);
						if(deployCreatureOption == 1)
							game.setDeployCreaturesOption(true);
						else
							game.setDeployCreaturesOption(false);
						
						String sharedTeamTurnOptionJ = getJson(received, "stt");
						int sharedTeamTurnOption = gson.fromJson(sharedTeamTurnOptionJ, int.class);
						if(sharedTeamTurnOption == 1)
							game.setSharedTeamTurnsOption(true);
						else
							game.setSharedTeamTurnsOption(false);
						
						initPlayer(received);
					}
					// JSON "START GAME"
					// TODO: Riguardare come viene settato turn e priority
					received = getJson(cleanJson, "startGame");
					if ((game.getPlayersNumber()==game.getPriorityOrder().size()) && received != null) {
						sendToNode("gameStarted", 1, 1);
						for(Player p : game.getPriorityOrder()){
							//init opponents
							//TODO: tutti i player sono avversari fra loro, da modificare quando saranno implementate le squadre
							for (Player opp : game.getPriorityOrder()){
								if (p != opp){
									p.getOpponentsList().add(opp);
								}
							}
							
							// init action
							Action act = new Action(p.getId(), 0, 0, 0);
							p.setAction(act);
							// add drools
							droolsEngine.addToDroolsEngine(p);
							droolsEngine.addToDroolsEngine(act);
						}
						
						game.getTurnOrder().addAll(game.getPriorityOrder());
						game.getPlayers().addAll(game.getPriorityOrder());
												
						droolsEngine.addToDroolsEngine(game);
						droolsEngine.setFocusAgenda("initialization");
						droolsEngine.fireAllRules();
						break;
					}
				}
			}
			
			//cambio agenda per il temporaneo passaggio automatico da untap a upkeep
			droolsEngine.setFocusAgenda("general");
			droolsEngine.fireAllRules();

			while (true) {
				Arrays.fill(buffer, ' '); // clean buffer
				n = input.read(buffer);
				System.out.println(buffer);
				if (n != -1) {
					cleanJson = cleanBuffer(buffer);
					// JSON "EXIT"
					received = getJson(cleanJson, "exit");
					if (received != null) {
						System.out.println("ricevuto exit");
						exit(received);
						break;
					}
					// JSON "ATTEMPT"
					received = getJson(cleanJson, "attempt");
					if (received != null) {
						game.setAttempt(gson.fromJson(received, Attempt.class));
						droolsEngine.setFocusAgenda("general");
						droolsEngine.addToDroolsEngine(game.getAttempt());
						droolsEngine.fireAllRules();
					}
					
					// JSON "DebugON"
					received = getJson(cleanJson, "debugOn");
					if (received != null) {
						FactHandle fh = droolsEngine.addToDroolsEngine(game);
						System.out.println("ricevuto debugOn");
						game.setPrestage(game.getStage());
						System.out.println("stage"+game.getStage());
						game.setStage(999);
						
						droolsEngine.updateGame(fh,game);
						droolsEngine.fireAllRules();
					}
					
					// JSON "DebugOFF"
					received = getJson(cleanJson, "debugOff");
					if (received != null) {
						System.out.println("ricevuto debugOff");
						FactHandle fh = droolsEngine.addToDroolsEngine(game);
						game.setStage(game.getPrestage());
						System.out.println("prestage"+game.getPrestage());
						System.out.println("stage"+game.getStage());
						droolsEngine.updateGame(fh,game);
						droolsEngine.fireAllRules();
						
					}
					
					
					// JSON "setDataPlayer"
					received = getJson(cleanJson, "setDataPlayer");
					if (received != null) {
						System.out.println("ricevuto setDataPlayer");
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int life = Integer.parseInt(getJson(cleanJson, "LTPlayer"));
						int poison = Integer.parseInt(getJson(cleanJson, "poisonCounter"));
						String mana = getJson(cleanJson, "LMPlayer");
						LinkedList<Mana> list = new LinkedList<Mana>();
						
						// Converts ManaSymbol objects to Mana objects
						LinkedList<ManaSymbol> manp=makeManaCost(mana);
						if (manp.size() > 0) {
							for (ManaSymbol mSymb : manp) {
								list.add(new Mana(mSymb));
							}
						}
						
						FactHandle fh = droolsEngine.addToDroolsEngine(game);
						
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						game.getPriorityOrder().get(idappoggio).setLifeTotal(life);
						game.getPriorityOrder().get(idappoggio).setPoisonCounter(poison);
						game.getPriorityOrder().get(idappoggio).setManaPool(list);
						
						droolsEngine.updateGame(fh,game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
						
					}
					
					// JSON "CHOICE"
					received = getJson(cleanJson, "choice");
					if (received != null) {
						game.setChoice(gson.fromJson(received, ChoiceAnswer.class));
						droolsEngine.setFocusAgenda("general");
						droolsEngine.addToDroolsEngine(game.getChoice());
						droolsEngine.fireAllRules();
						
					}
					
					//JSON MOVE HAND TO LIBRARY
					received = getJson(cleanJson, "H_L");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getPriorityOrder().get(idappoggio).getHand().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Card card = (Card) game.getPriorityOrder().get(idappoggio).getHand().get(i);
					        if (card.getId() == idCard){
						        	//variabile d'appoggio per swappare la carta 
						        	MagicObject theCard = game.getPriorityOrder().get(idappoggio).getHand().get(i);
						        	game.getPriorityOrder().get(idappoggio).getHand().remove(i);
						        	game.getPriorityOrder().get(idappoggio).getLibrary().add(numCard, theCard);
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE HAND TO GRAVEYARD
					received = getJson(cleanJson, "H_G");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getPriorityOrder().get(idappoggio).getHand().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Card card = (Card) game.getPriorityOrder().get(idappoggio).getHand().get(i);
					        if (card.getId() == idCard){
						        	//variabile d'appoggio per swappare la carta 
						        	MagicObject theCard = game.getPriorityOrder().get(idappoggio).getHand().get(i);
						        	game.getPriorityOrder().get(idappoggio).getHand().remove(i);
						        	if(numCard<game.getPriorityOrder().get(idappoggio).getGraveyard().size()){
						        		game.getPriorityOrder().get(idappoggio).getGraveyard().add(numCard, theCard);
						        	}else{
						        		game.getPriorityOrder().get(idappoggio).getGraveyard().add(theCard);
						        	}
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE HAND TO BATTLEFIELD
					received = getJson(cleanJson, "H_B");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
					        	//variabile d'appoggio per swappare la carta 
					        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getPriorityOrder().get(idappoggio).getHand().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Card c = (Card) game.getPriorityOrder().get(idappoggio).getHand().get(i);
					        if (c.getId() == idCard) {
						        	//variabile d'appoggio per swappare la carta 
						        	game.getPriorityOrder().get(idappoggio).getHand().remove(i);
						        	game.getBattleField().add(new Permanent(c, c.getMagicTargetId(), idPlayr, game.getIdBattlefieldCounter()));
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
						
					}
					
					//JSON MOVE HAND TO EXILE
					received = getJson(cleanJson, "H_E");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
					        	//variabile d'appoggio per swappare la carta 
					        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getPriorityOrder().get(idappoggio).getHand().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Card card = (Card) game.getPriorityOrder().get(idappoggio).getHand().get(i);
					        if (card.getId() == idCard){
					        	//variabile d'appoggio per swappare la carta 
					        	MagicObject theCard = game.getPriorityOrder().get(idappoggio).getHand().get(i);
					        	game.getPriorityOrder().get(idappoggio).getHand().remove(i);
					        	game.getExile().add(theCard);
					        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE HAND TO STACK
					received = getJson(cleanJson, "H_S");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getPriorityOrder().get(idappoggio).getHand().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Card card = (Card) game.getPriorityOrder().get(idappoggio).getHand().get(i);
					        if (card.getId() == idCard){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getPriorityOrder().get(idappoggio).getHand().remove(i);
						        	Spell s=new Spell(card,card.getMagicTargetId(),idPlayr,game.getIdStackCounter());
						        	game.getStack().add(s);
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE LIBRARY TO HAND
					received = getJson(cleanJson, "L_H");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						Card c=(Card) game.getPriorityOrder().get(idappoggio).getLibrary().get(numCard);
						game.getPriorityOrder().get(idappoggio).getLibrary().remove(numCard);
						game.getPriorityOrder().get(idappoggio).getHand().add(c);
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE LIBRARY TO GRAVEYARD
					received = getJson(cleanJson, "L_G");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int numPlace = Integer.parseInt(getJson(cleanJson, "numPlace"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						Card c=(Card) game.getPriorityOrder().get(idappoggio).getLibrary().get(numCard);
						game.getPriorityOrder().get(idappoggio).getLibrary().remove(numCard);
						if(numPlace<game.getPriorityOrder().get(idappoggio).getGraveyard().size()){
				        		game.getPriorityOrder().get(idappoggio).getGraveyard().add(numPlace, c);
				        	}else{
				        		game.getPriorityOrder().get(idappoggio).getGraveyard().add(c);
				        	}
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE LIBRARY TO BATTLEFIELD
					received = getJson(cleanJson, "L_B");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						Card c=(Card) game.getPriorityOrder().get(idappoggio).getLibrary().get(numCard);
						game.getPriorityOrder().get(idappoggio).getLibrary().remove(numCard);
						game.getBattleField().add(new Permanent(c, c.getMagicTargetId(), idPlayr, game.getIdBattlefieldCounter()));
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE LIBRARY TO EXILE
					received = getJson(cleanJson, "L_E");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						Card c=(Card) game.getPriorityOrder().get(idappoggio).getLibrary().get(numCard);
						game.getPriorityOrder().get(idappoggio).getLibrary().remove(numCard);
						game.getExile().add(c);
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}

					//JSON MOVE LIBRARY TO STACK
					received = getJson(cleanJson, "L_S");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						Card c=(Card) game.getPriorityOrder().get(idappoggio).getLibrary().get(numCard);
						game.getPriorityOrder().get(idappoggio).getLibrary().remove(numCard);
			        		game.getStack().add(new Spell(c,c.getMagicTargetId(),idPlayr,game.getIdStackCounter()));
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE GRAVEYARD TO HAND
					received = getJson(cleanJson, "G_H");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						Card c=(Card) game.getPriorityOrder().get(idappoggio).getGraveyard().get(numCard);
						game.getPriorityOrder().get(idappoggio).getGraveyard().remove(numCard);
						game.getPriorityOrder().get(idappoggio).getHand().add(c);
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE GRAVEYARD TO LIBRARY
					received = getJson(cleanJson, "G_L");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int numPlace = Integer.parseInt(getJson(cleanJson, "numPlace"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						Card c=(Card) game.getPriorityOrder().get(idappoggio).getGraveyard().get(numCard);
						game.getPriorityOrder().get(idappoggio).getGraveyard().remove(numCard);
						if(numPlace<game.getPriorityOrder().get(idappoggio).getLibrary().size()){
				        		game.getPriorityOrder().get(idappoggio).getLibrary().add(numPlace, c);
				        	}else{
				        		game.getPriorityOrder().get(idappoggio).getLibrary().add(c);
				        	}
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE GRAVEYARD TO BATTLEFIELD
					received = getJson(cleanJson, "G_B");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
					        	//variabile d'appoggio per swappare la carta 
					        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						Card c=(Card) game.getPriorityOrder().get(idappoggio).getGraveyard().get(numCard);
						game.getPriorityOrder().get(idappoggio).getGraveyard().remove(numCard);
						game.getBattleField().add(new Permanent(c, c.getMagicTargetId(), idPlayr, game.getIdBattlefieldCounter()));
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE GRAVEYARD TO EXILE
					received = getJson(cleanJson, "G_E");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
					        	//variabile d'appoggio per swappare la carta 
					        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						Card c=(Card) game.getPriorityOrder().get(idappoggio).getGraveyard().get(numCard);
						game.getPriorityOrder().get(idappoggio).getGraveyard().remove(numCard);
						game.getExile().add(c);
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE GRAVEYARD TO STACK
					received = getJson(cleanJson, "G_S");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						Card c=(Card) game.getPriorityOrder().get(idappoggio).getGraveyard().get(numCard);
						game.getPriorityOrder().get(idappoggio).getGraveyard().remove(numCard);
						game.getStack().add(new Spell(c,c.getMagicTargetId(),idPlayr,game.getIdStackCounter()));
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE EXILE TO HAND
					received = getJson(cleanJson, "E_H");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getExile().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Card card = (Card) game.getExile().get(i);
					        if (card.getId() == idCard && card.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getExile().remove(i);
						        	game.getPriorityOrder().get(idappoggio).getHand().add(card);
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE EXILE TO LIBRARY
					received = getJson(cleanJson, "E_L");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getExile().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Card card = (Card) game.getExile().get(i);
					        if (card.getId() == idCard && card.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getExile().remove(i);
						        	game.getPriorityOrder().get(idappoggio).getLibrary().add(numCard, card);
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE EXILE TO GRAVEYARD
					received = getJson(cleanJson, "E_G");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getExile().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Card card = (Card) game.getExile().get(i);
					        if (card.getId() == idCard && card.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getExile().remove(i);
						        	if(numCard<game.getPriorityOrder().get(idappoggio).getGraveyard().size()){
						        		game.getPriorityOrder().get(idappoggio).getGraveyard().add(numCard, card);
						        	}else{
						        		game.getPriorityOrder().get(idappoggio).getGraveyard().add(card);
						        	}					        
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE EXILE TO BATTLEFIELD
					received = getJson(cleanJson, "E_B");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getExile().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Card c = (Card) game.getExile().get(i);
					        if (c.getId() == idCard && c.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getExile().remove(i);
								game.getBattleField().add(new Permanent(c, c.getMagicTargetId(), idPlayr, game.getIdBattlefieldCounter()));
					        		break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE EXILE TO STACK
					received = getJson(cleanJson, "E_S");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getExile().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Card c = (Card) game.getExile().get(i);
					        if (c.getId() == idCard && c.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getExile().remove(i);
						        	game.getStack().add(new Spell(c,c.getMagicTargetId(),idPlayr,game.getIdStackCounter()));
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}

					//JSON MOVE BATTLEFIELD TO HAND
					received = getJson(cleanJson, "B_H");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getBattleField().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Permanent p = (Permanent) game.getBattleField().get(i);
							Card c=p.getOriginCard();
					        if (c.getId() == idCard && c.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getBattleField().remove(i);
						        	game.getPriorityOrder().get(idappoggio).getHand().add(c);
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE BATTLEFIELD TO LIBRARY
					received = getJson(cleanJson, "B_L");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getBattleField().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Permanent p = (Permanent) game.getBattleField().get(i);
							Card c=p.getOriginCard();
							if (c.getId() == idCard && c.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getBattleField().remove(i);
						        	game.getPriorityOrder().get(idappoggio).getLibrary().add(numCard, c);;
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE BATTLEFIELD TO GRAVEYARD
					received = getJson(cleanJson, "B_G");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getBattleField().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Permanent p = (Permanent) game.getBattleField().get(i);
							Card c=p.getOriginCard();
							if (c.getId() == idCard && c.getIdOwner()==idPlayr){
					        	//variabile d'appoggio per swappare la carta 
						        	game.getBattleField().remove(i);
						        	if(numCard<game.getPriorityOrder().get(idappoggio).getGraveyard().size()){
						        		game.getPriorityOrder().get(idappoggio).getGraveyard().add(numCard, c);
						        	}else{
						        		game.getPriorityOrder().get(idappoggio).getGraveyard().add(c);
						        	}
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE BATTLEFIELD TO EXILE
					received = getJson(cleanJson, "B_E");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getBattleField().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Permanent p = (Permanent) game.getBattleField().get(i);
							Card c=p.getOriginCard();
							if (c.getId() == idCard && c.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getBattleField().remove(i);
						        	game.getExile().add(c);
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE BATTLEFIELD TO STACK
					received = getJson(cleanJson, "B_S");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getBattleField().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Permanent p = (Permanent) game.getBattleField().get(i);
							Card c=p.getOriginCard();
							if (c.getId() == idCard && c.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getBattleField().remove(i);
						        	game.getStack().add(new Spell(c,c.getMagicTargetId(),idPlayr,game.getIdStackCounter()));
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE STACK TO HAND
					received = getJson(cleanJson, "S_H");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getStack().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Spell p = (Spell) game.getStack().get(i);
							Card c=p.getOriginCard();
							if (c.getId() == idCard && c.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getStack().remove(i);
						        	game.getPriorityOrder().get(idappoggio).getHand().add(c);
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE STACK TO LIBRARY
					received = getJson(cleanJson, "S_L");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getStack().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Spell p = (Spell) game.getStack().get(i);
							Card c=p.getOriginCard();
							if (c.getId() == idCard && c.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getStack().remove(i);
						        	game.getPriorityOrder().get(idappoggio).getLibrary().add(numCard,c);
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE STACK TO BATTLEFIELD
					received = getJson(cleanJson, "S_B");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getStack().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Object o = game.getStack().get(i);
							if(o instanceof Spell) {
								Spell p = (Spell) o;
								Card c=p.getOriginCard();
								if (c.getId() == idCard && c.getIdOwner()==idPlayr){
							        	//variabile d'appoggio per swappare la carta 
							        	game.getStack().remove(i);
							        	game.getBattleField().add(new Permanent(c, c.getMagicTargetId(), idPlayr, game.getIdBattlefieldCounter()));
							        	break;
						    }
							}else if(o instanceof AbilityOnTheStack) {
								AbilityOnTheStack a = (AbilityOnTheStack) o;
								Object o2 = a.getOriginCard();
								if(o2 instanceof Card) {
									Card c = (Card) o2;
									if (c.getId() == idCard && c.getIdOwner()==idPlayr){
					        	//variabile d'appoggio per swappare la carta 
					        	game.getStack().remove(i);
					        	game.getBattleField().add(new Permanent(c, c.getMagicTargetId(), idPlayr, game.getIdBattlefieldCounter()));
					        	break;
									}
								}else {
									// gestire il caso in cui non sia una carta (Pietro e Nicola)
									game.getStack().remove(i);
								}
							}else {// gestire il caso in cui non sia una spell o una ability
								game.getStack().remove(i);								
							}
					  } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE STACK TO GRAVEYARD
					received = getJson(cleanJson, "S_G");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int numCard = Integer.parseInt(getJson(cleanJson, "numCard"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getStack().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Spell p = (Spell) game.getStack().get(i);
							Card c=p.getOriginCard();
							if (c.getId() == idCard && c.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getStack().remove(i);
						        	if(numCard<game.getPriorityOrder().get(idappoggio).getGraveyard().size()){
						        		game.getPriorityOrder().get(idappoggio).getGraveyard().add(numCard, c);
						        	}else{
						        		game.getPriorityOrder().get(idappoggio).getGraveyard().add(c);
						        	}
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					//JSON MOVE STACK TO EXILE
					received = getJson(cleanJson, "S_E");
					if (received != null) {
						int idPlayr = Integer.parseInt(getJson(cleanJson, "idplayer"));
						int idCard = Integer.parseInt(getJson(cleanJson, "idCard"));
						LinkedList<Player> x = game.getPriorityOrder();
						int idappoggio = 0;
						for(int e=0; e < x.size(); e++){
					        if (x.get(e).getId() == idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	idappoggio = e;
					        }
					    } 
						FactHandle fm = droolsEngine.addToDroolsEngine(game);
						
						for(int i=0; i < game.getStack().size(); i++){
							//System.out.println(game.getPriorityOrder().get(idappoggio).getHand().get(i));
							Spell p = (Spell) game.getStack().get(i);
							Card c=p.getOriginCard();
							if (c.getId() == idCard && c.getIdOwner()==idPlayr){
						        	//variabile d'appoggio per swappare la carta 
						        	game.getStack().remove(i);
						        	game.getExile().add(c);
						        	break;
					        }
					    } 
						
						droolsEngine.updateGame(fm, game);
						droolsEngine.setFocusAgenda("general");
						droolsEngine.fireAllRules();
					}
					
					// JSON "SEND ZONES"
					received = getJson(cleanJson, "receiveZone");
					if (received != null) {
						int id = gson.fromJson(received, int.class);
						System.out.println("ricevuto richiesta zones da " + id);
						PrivateGame privateGame = new PrivateGame(game, id);
						sendToNode(privateGame, 3, 0, id);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				clientSocket.close();
				System.out.println("Game closed");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
}
