package com.magicengine;

import java.util.LinkedList;

public class DebugGame {
	GameP debugGame;
	
	public DebugGame(Game game)
	{
		// Inizializzo il private game
		debugGame = new GameP();
		
		// Zone Shared
		// Battlefield
		Zone battlefield = new Zone("battlefield", true);
		for(MagicObject obj : game.getBattleField())
		{
			Permanent perm = (Permanent) obj;
			/*if(true) 
			{*/
				battlefield.add(new Permanent(perm, false));
			/*}
			else
			{
				battlefield.add(new Permanent(perm, perm.isFaceDown()));		
			}*/
		}
		debugGame.setBattlefield(battlefield);
		
		// Stack
		Zone stack = new Zone("stack", true);
		for(MagicObject obj : game.getStack())
		{
			if(obj instanceof Spell) {
				Spell sp = (Spell) obj;
				stack.add(new Spell(sp, sp.isFaceDown()));
			}
			else if(obj instanceof AbilityOnTheStack) {
				AbilityOnTheStack ab = (AbilityOnTheStack)obj;
				stack.add(new AbilityOnTheStack(ab));				
			}else {
				System.out.println("ERRORE: Non capisco di che tipo sia l'oggetto "+ obj +" RIGA 32 debug game ! <<<<<<<<<<");
			}
		}
		debugGame.setStack(stack);
		
		// Ante
		Zone ante = new Zone("ante", true);
		for(MagicObject obj : game.getAnte())
		{
			Card card = (Card) obj;
			ante.add(new Card(card, card.getId()));
		}
		debugGame.setAnte(ante);
		
		// Command
		Zone command = new Zone("command", true);
		for(MagicObject obj : game.getCommand())
		{
			command.add(new MagicObject(obj, obj.getMagicTargetId()));
		}
		debugGame.setCommand(command);
		
		// Exile
		Zone exile = new Zone("exile",true);
		for(MagicObject obj : game.getExile())
		{
			if(obj instanceof Card)
			{
				Card card = (Card) obj;
				exile.add(new Card(card, false));
			}
			else
			{
				exile.add(new MagicObject(obj, true));
			}
		}
		debugGame.setExile(exile);
		
		// Players
		/*for(Player pl : game.getPriorityOrder())
		{
			if(true)
			{
				PlayerP player = debugGame.getPlayer();
				player.setId(pl.getId());
				player.setNickname(pl.getNickname());
				player.setLifeTotal(pl.getLifeTotal());
				player.setManaPool(pl.printManaPool());
				player.setPoisonCounter(pl.getPoisonCounter());
				for(MagicObject obj : pl.getGraveyard())
				{
					Card c = (Card) obj;
					player.getGraveyard().add(new Card(c, false));
				}
				for(MagicObject obj : pl.getHand())
				{
					Card c = (Card) obj;
					player.getHand().add(new Card(c, false));
				}
				for(MagicObject obj : pl.getLibrary())
				{
					Card c = (Card) obj;
					player.getLibrary().add(new Card(c, false));
				}
				for(Player p : pl.getTeamMates())
				{
					PlayerP_O teamMate = new PlayerP_O();
					teamMate.setId(p.getId());
					teamMate.setNickname(p.getNickname());
					teamMate.setLifeTotal(p.getLifeTotal());
					teamMate.setManaPool(p.printManaPool());
					teamMate.setPoisonCounter(p.getPoisonCounter());
					for(MagicObject obj : p.getGraveyard())
					{
						Card c = (Card) obj;
						teamMate.getGraveyard().add(new Card(c, false));
					}
					for(MagicObject obj : p.getHand())
					{
						Card c = (Card) obj;
						teamMate.getHand().add(new Card(c, false));
					}
					for(MagicObject obj : p.getLibrary())
					{
						Card c = (Card) obj;
						teamMate.getLibrary().add(new Card(c, false));
					}
					player.getTeamMates().add(teamMate);
				}
				for(Player p : pl.getOpponentsList())
				{
					PlayerP_O opponent = new PlayerP_O();
					opponent.setId(p.getId());
					opponent.setNickname(p.getNickname());
					opponent.setLifeTotal(p.getLifeTotal());
					opponent.setManaPool(p.printManaPool());
					opponent.setPoisonCounter(p.getPoisonCounter());
					for(MagicObject obj : p.getGraveyard())
					{
						Card c = (Card) obj;
						opponent.getGraveyard().add(new Card(c, false));
					}
					for(MagicObject obj : p.getHand())
					{
						Card c = (Card) obj;
						opponent.getHand().add(new Card(c, false));
					}
					for(MagicObject obj : p.getLibrary())
					{
						Card c = (Card) obj;
						opponent.getLibrary().add(new Card(c, false));
					}
					player.getOpponentsList().add(opponent);
					
				}
			}
		}*/
		PlayerP player = debugGame.getPlayer();

		for(Player p : game.getPlayers())
		{
			PlayerP_O opponent = new PlayerP_O();
			opponent.setId(p.getId());
			opponent.setNickname(p.getNickname());
			opponent.setLifeTotal(p.getLifeTotal());
			opponent.setManaPool(p.printManaPool());
			opponent.setPoisonCounter(p.getPoisonCounter());
			opponent.setHasWin(p.getHasWin());
			
			for(MagicObject obj : p.getGraveyard())
			{
				Card c = (Card) obj;
				opponent.getGraveyard().add(new Card(c, false));
			}
			for(MagicObject obj : p.getHand())
			{
				Card c = (Card) obj;
				opponent.getHand().add(new Card(c, false));
			}
			for(MagicObject obj : p.getLibrary())
			{
				Card c = (Card) obj;
				opponent.getLibrary().add(new Card(c, false));
			}
			player.getOpponentsList().add(opponent);
			
		}
		
		
		// Timing Variables
		if(game.getPriorityMarker().getIndex() != -1)
			debugGame.setPriorityPlayer(game.getPriorityMarker().getObject().getId());
		else
			debugGame.setPriorityPlayer(-1);
		if(game.getActivePlayer().getIndex() != -1)
			debugGame.setActivePlayer(game.getActivePlayer().getObject().getId());
		else
			debugGame.setActivePlayer(-1);
		if(game.getCurrentPhase().getIndex() != -1)
			debugGame.setCurrentPhase(game.getCurrentPhase().getObject().getName());
		else
			debugGame.setCurrentPhase("");
		if(game.getCurrentStep().getIndex() != -1)
			debugGame.setCurrentStep(game.getCurrentStep().getObject().getName());
		else
			debugGame.setCurrentStep("");
			
	}
	
	private class GameP
	{
		private Zone battlefield;
		private Zone stack;
		private Zone ante;
		private Zone command;
		private Zone exile;
		private PlayerP player;
		private int priorityPlayer;
		private int activePlayer;
		private String currentPhase;
		private String currentStep;
		
		public GameP() {
			super();
			this.player = new PlayerP();
		}

		public Zone getBattlefield() {
			return battlefield;
		}

		public void setBattlefield(Zone battlefield) {
			this.battlefield = battlefield;
		}

		public Zone getStack() {
			return stack;
		}

		public void setStack(Zone stack) {
			this.stack = stack;
		}

		public Zone getAnte() {
			return ante;
		}

		public void setAnte(Zone ante) {
			this.ante = ante;
		}

		public Zone getCommand() {
			return command;
		}

		public void setCommand(Zone command) {
			this.command = command;
		}

		public Zone getExile() {
			return exile;
		}

		public void setExile(Zone exile) {
			this.exile = exile;
		}

		public PlayerP getPlayer() {
			return player;
		}

		public void setPlayer(PlayerP player) {
			this.player = player;
		}

		public int getPriorityPlayer() {
			return priorityPlayer;
		}

		public void setPriorityPlayer(int priorityPlayer) {
			this.priorityPlayer = priorityPlayer;
		}

		public int getActivePlayer() {
			return activePlayer;
		}

		public void setActivePlayer(int activePlayer) {
			this.activePlayer = activePlayer;
		}

		public String getCurrentPhase() {
			return currentPhase;
		}

		public void setCurrentPhase(String currentPhase) {
			this.currentPhase = currentPhase;
		}

		public String getCurrentStep() {
			return currentStep;
		}

		public void setCurrentStep(String currentStep) {
			this.currentStep = currentStep;
		}
	}
	
	private class PlayerP
	{
		private String nickname;
		private int id;
		private Zone hand;
		private Zone graveyard;
		private Zone library;
		private LinkedList<PlayerP_O> opponentsList;
		private LinkedList<PlayerP_O> teamMates;
		private int lifeTotal;
		private String manaPool;
		private int poisonCounter;
		private Integer hasWin;

		
		public PlayerP() {
			super();
			this.hand = new Zone("hand", true);
			this.graveyard = new Zone("graveyard", true);
			this.library = new Zone("library",true);
			this.opponentsList = new LinkedList<PlayerP_O>();
			this.teamMates = new LinkedList<PlayerP_O>();
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
		public Zone getLibrary() {
			return library;
		}
		public void setLibrary(Zone library) {
			this.library = library;
		}
		public LinkedList<PlayerP_O> getOpponentsList() {
			return opponentsList;
		}
		public void setOpponentsList(LinkedList<PlayerP_O> opponentsList) {
			this.opponentsList = opponentsList;
		}
		public LinkedList<PlayerP_O> getTeamMates() {
			return teamMates;
		}
		public void setTeamMates(LinkedList<PlayerP_O> teamMates) {
			this.teamMates = teamMates;
		}
		public int getLifeTotal() {
			return lifeTotal;
		}
		public void setLifeTotal(int lifeTotal) {
			this.lifeTotal = lifeTotal;
		}
		public String getManaPool() {
			return manaPool;
		}

		public void setManaPool(String manaPool) {
			this.manaPool = manaPool;
		}

		public int getPoisonCounter() {
			return poisonCounter;
		}

		public void setPoisonCounter(int poisonCounter) {
			this.poisonCounter = poisonCounter;
		}	
		public Integer getHasWin(){
			return this.hasWin;
		}
		
		public void setHasWin(Integer hasWin){
			this.hasWin=hasWin;
		}
	}
	
	private class PlayerP_O {
		private String nickname;
		private int id;
		private Zone hand;
		private Zone graveyard;
		private Zone library;
		private int lifeTotal;
		private String manaPool;
		private int poisonCounter;
		private Integer hasWin;
		
		public int getPoisonCounter() {
			return poisonCounter;
		}

		public void setPoisonCounter(int poisonCounter) {
			this.poisonCounter = poisonCounter;
		}

		public PlayerP_O() {
			super();
			this.hand = new Zone("hand", true);
			this.graveyard = new Zone("graveyard", true);
			this.library = new Zone("library",true);
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
		public Zone getLibrary() {
			return library;
		}
		public void setLibrary(Zone library) {
			this.library = library;
		}
		public int getLifeTotal() {
			return lifeTotal;
		}
		public void setLifeTotal(int lifeTotal) {
			this.lifeTotal = lifeTotal;
		}
		public String getManaPool() {
			return manaPool;
		}

		public void setManaPool(String manaPool) {
			this.manaPool = manaPool;
		}	
		
		public Integer getHasWin(){
			return this.hasWin;
		}
		
		public void setHasWin(Integer hasWin){
			this.hasWin=hasWin;
		}
	}
}