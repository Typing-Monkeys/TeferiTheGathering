package com.magicengine;


import java.util.LinkedList;
import java.util.*;

public class Game {
	
	/**
	 * Designazioni: (esempio Giorno/Notte/Nessuna)
	 * decidere come gestire le designazioni,
	 * come variabili booleane o lista di oggetti di tipo Designation, ...
	 */
	
	
	// KEYWORD ABILITY
	/* 
	 * Array associativo di keyword ability
	 * da riempire con le regole
	 * altre regole inseriscono queste abilità nelle carte
	*/
	public Map<String, Ability> keyword_ability = new HashMap<String, Ability>(); 
	public Map<String, Ability> activated_ability = new HashMap<String, Ability>(); 
	public Map<String, Ability> triggered_ability = new HashMap<String, Ability>(); 
	public Map<String, Ability> static_ability = new HashMap<String, Ability>(); 
	//EVASIVE OK
	
	
	
	// CONSTANT---------------------------------------------------------------------------
	// COMMAND TeamDrools handles only these
	public static final int TAKE_ACTION = 1;
	public static final int MAKE_CHOICE = 2;
	
	// OPTION
	public static final int PASS_PRIORITY = 1;
	public static final int CAST_INSTANT_SPELL = 2;
	public static final int CAST_NON_INSTANT_SPELL = 3;
	public static final int ACTIVATE_ABILITY = 4;
	public static final int ACTIVATE_MANA_ABILITY = 5;
	public static final int TAKE_SPECIAL_ACTION	= 6;
	public static final int SELECT_TARGET = 7;
	
	//OPTION2
	public static final int PLAY_LAND = 1;
	public static final int TURN_CREATURE_FACE_UP = 2;
	//public static final int TAKE_ACTION_AT_LATER_TIME = 3;
	//public static final int IGNORE_ABILITY_FOR_A_DURATION = 4;
	//public static final int SUSPEND_CARD = 5;
	//public static final int ROLL_PLANAR_DIE = 6;
	//public static final int TURN_CONSPIRACY_CARD_FACE_DOWN = 7;
	
	//VARIANT
	public static final int FREE_FOR_ALL = 1;
	public static final int GRAND_MELEE = 2;
	public static final int TEAM_VS_TEAM = 3;
	public static final int EMPEROR = 4;
	public static final int TWO_HEADED_GIANT = 5;
	public static final int ALTERNATING_TEAMS = 6;
	
	//CASUAL VARIANT
	public static final int PLANECHASE = 1;
	public static final int VANGUARD = 2;
	public static final int COMMANDER = 3;
	public static final int ARCHENEMY = 4;
	public static final int CONSPIRACY_DRAFT = 5;
	
	
	// TIME FRAME	
	public static final int BEGIN_TIME_FRAME = 1;
	public static final int DURING_TIME_FRAME = 2;	
	public static final int END_TIME_FRAME = 0;
	public static final int ABSENT_TIME_FRAME = -1;
	public static final int CLEANUP_TIME_FRAME = 3;
	
	// STAGE
	public static final int STARTING_STAGE 	= 0;
	public static final int GAME_STAGE 		= 1;
	public static final int DEBUG_STAGE 	= 999;
	/*
	 * Questa costante viene utilizzata per terminare il gioco e
	 * disabilitare ogni altra regola tranne la `updateClients` e
	 * `updateDebug` 
	 */
	public static final int GAME_FINISHED 	= 2; 
	
	// TYPE OF MESSAGES
	public static final int TEXT_MESSAGE = 1;
	public static final int CHOICE = 2;
	public static final int GAME_STATUS = 3;
	
	// TYPE OF CHOICES
	public static final int UNKNOWN = 0;
	public static final int ONE_OF_CHOICE = 1;
	public static final int MULTIPLE_CHOICE = 2;
	
	// ID CHOICES
	public static final int CHOOSE_STARTING_PLAYER = 1;
	public static final int TAKE_MULLIGAN = 2;
	public static final int TAKE_SCRY = 3;
	public static final int DISCARD_TO_MAXHANDSIZE = 4;
	
	// MULLIGAN TYPES
	public static final int VANCOUVER_MULLIGAN = 1;
	public static final int PARIS_MULLIGAN = 2;
	public static final int LONDON_MULLIGAN = 3;
	
	// -------------------------------------------------------------------------------------
	
	// VARIANTS AND OPTIONS
	boolean twoPlayerGame;
	int variant;
	int casualVariant;
	boolean limitedRangeOfInfluenceOption;
	int rangeOfInfluence;
	boolean attackMultiplePlayersOption;
	boolean attackLeftOption;
	boolean attackRightOption;
	boolean deployCreaturesOption;
	boolean sharedTeamTurnsOption;

	// LinkedList<Team> listOfTeam;
	//LinkedList<Player> startingTeam;
	//LinkedList<Player> multiplayerVariant;
	private int playersNumber;
	// ------------------------------------------------------

	// GLOBAL
	// VARIABLES---------------------------------------------------------------------
	private int indexChooserPlayer; //settato dalla random
	private ListPointer<Player> chooserPlayer; // rule 103.2
	private ListPointer<Player> startingPlayer; // rule 103.2
	private LinkedList<Player> nonActivePlayers;
	private LinkedList<Player> players; // Maintains the complete list of players
	private Attempt attempt;
	private Spell castingSpell;
	private Spell resolvingSpell;
	private boolean resolvedSpell;
	private ChoiceAnswer choice;
	
	private AbilityOnTheStack activeAbility;	
	private AbilityOnTheStack resolvingAbility;
	
	// MULLIGAN PRIORITY HANDLER
	private LinkedList<Player> mulliganPriorityOrder; // no mutable, used for mulligan priority
	private ListPointer<Player> mulliganPriorityMarker; // pointer to mulligan priorityOrder
	private LinkedList<Player> scryPriorityOrder; // Used to maintain a list of player that can do scry
	private ListPointer<Player> scryPriorityMarker; // Used as a pointer to work over scryPriorityOrder 
	private boolean mulliganPhase;
	private boolean mulliganFlag;
	private int mulliganType;
	private boolean finishedEndPhaseOfMulligan; // Indicates that the mulligan phase is finished
	private boolean discardTurn; // Indica che la fase di scarto è in corso
	
	/*
	 * Indicates that the player's scry turn is started or, in other words,
	 * to "force" the turn based scrying phase
	 */
	private boolean scryTurn;
	
	// PRIORITY HANDLER
	private LinkedList<Player> priorityOrder; // no mutable, used for priority
	private ListPointer<Player> priorityMarker; // pointer to priorityOrder
	private boolean controlStateBasedActions;
	private LinkedList<Player> sevenzerofourPlayerOrder; // 704.5j
	private ListPointer<Player> sevenzerofourPlayerMarker; // 704.5j
	private boolean playerHasToMakeChoiceszf; // 704.5j
	
	// STAGE HANDLER
	private int stage; // stage of the game, can take values STARTING(=0) or GAME(=1) or DEBUG(999)
	private int prestage;
	// TURN HANDLER
	private boolean firstTurn;
	private LinkedList<Player> turnOrder; // mutable because cards rules
	private ListPointer<Player> activePlayer; // pointer to turnOrder, rule 102.1
	private ListPointer<Phase> currentPhase;
	private ListPointer<Step> currentStep;
	private int stepTimeFrame = BEGIN_TIME_FRAME;//Begin, During, End
	private int phaseTimeFrame = BEGIN_TIME_FRAME;//Begin, During, End
	private int turnTimeFrame = BEGIN_TIME_FRAME;//Begin, During, End
	// COUNTERS
	private int idObjectCounter;
	private int idBattlefieldCounter;
	private int idStackCounter;
	private int idExileCounter;
	private int idGraveyardCounter;

	// PUBLIC ZONE RULE 400.1
	private Zone stack;
	private Zone battleField;
	private Zone ante;
	private Zone command;
	private Zone exile;
	private int numberOfZone;
	
	// COMBAT VARIABLES
	private ListPointer<Player> attackingPlayer;
	private ListPointer<Player> defendingPlayers;
	private ListPointer<Permanent> attackingCreatures;
	private ListPointer<Permanent> blockingCreatures;
	private ListPointer<String> stepDeclareAttackers;
	private ListPointer<String> stepDeclareBlockers;
	private ListPointer<String> stepCheckEvasionAbility;
	private boolean blockValid; // flag di controllo per validità dei blocchi
	private ListPointer<String> stepCombatDamage;
	private LinkedList<Target> possibleTarget;
	// --------------------------------------------------------------------------------------

	public Game() {
		this.idObjectCounter = 0;
		this.nonActivePlayers = new LinkedList<Player>();
		// init mulligan priority
		this.mulliganPriorityOrder = new LinkedList<Player>();
		this.mulliganPriorityMarker = new ListPointer<Player>(this.mulliganPriorityOrder);
		this.setMulliganType(LONDON_MULLIGAN);
		this.setDiscardTurn(false);
		
		this.scryPriorityOrder = new LinkedList<Player>();
		this.scryPriorityMarker = new ListPointer<Player>(this.scryPriorityOrder);
		
		this.players = new LinkedList<Player>();
		
		// init priority
		this.priorityOrder = new LinkedList<Player>();
		this.priorityMarker = new ListPointer<Player>(this.priorityOrder);
		this.sevenzerofourPlayerOrder = new LinkedList<Player>();
		this.sevenzerofourPlayerMarker = new ListPointer<Player>(this.sevenzerofourPlayerOrder);
		this.playerHasToMakeChoiceszf = false;
		this.chooserPlayer = new ListPointer<Player>(this.priorityOrder);
		this.startingPlayer = new ListPointer<Player>(this.priorityOrder);
		// init stage
		this.stage = STARTING_STAGE;
		// init turn
		this.firstTurn = true;
		this.turnOrder = new LinkedList<Player>();
		this.activePlayer = new ListPointer<Player>(this.turnOrder);
		this.currentPhase = new ListPointer<Phase>();
		this.currentStep = new ListPointer<Step>();
		// 400.2
		this.stack = new Zone("stack",true,this.giveMeMyId());
		
		this.battleField = new Zone("battleField",true,this.giveMeMyId());
		this.ante = new Zone("ante",true,this.giveMeMyId());
		this.command = new Zone("command",true,this.giveMeMyId());
		this.exile = new Zone("exile",true,this.giveMeMyId());
		
		this.mulliganFlag = false;
		this.mulliganPhase = false;
		this.scryTurn = false;
		this.finishedEndPhaseOfMulligan = false;
		this.controlStateBasedActions = false;
		
		this.castingSpell = null;
		this.resolvingSpell = null;
		
		this.resolvedSpell = false;
		
		this.activeAbility = null;		
		this.resolvingAbility = null;
		

		this.idBattlefieldCounter = 0;
		this.idStackCounter = 0;
		this.idExileCounter = 0;
		this.idGraveyardCounter = 0;
		
		this.attackingPlayer = new ListPointer<Player>(this.priorityOrder);
		this.defendingPlayers = new ListPointer<Player>(new LinkedList<Player>());
		this.attackingCreatures = new ListPointer<Permanent>(new LinkedList<Permanent>());
		this.blockingCreatures = new ListPointer<Permanent>(new LinkedList<Permanent>());
		this.stepDeclareAttackers = new ListPointer<String>(new LinkedList<String>());
		this.stepDeclareBlockers = new ListPointer<String>(new LinkedList<String>());
		this.stepCheckEvasionAbility = new ListPointer<String>(new LinkedList<String>());
		this.setBlockValid(true);
		this.stepCombatDamage = new ListPointer<String>(new LinkedList<String>());
		this.possibleTarget = new LinkedList<Target>();
	}

	public void destroy(Permanent permanent){
		//rule 701.6a
		this.battleField.remove(permanent);
		Card card = permanent.getOriginCard();
		if(card.getCardType().get(0).contains("planeswalker")){
			
			for(Permanent attacker : this.attackingCreatures.getListReference()){
				if(attacker.getTarget() == permanent){
					attacker.setTarget(null);
				}
			}
		} else if(card.getCardType().get(0).contains("creature")){
			for(Permanent attacker : this.attackingCreatures.getListReference()){
				attacker.getBlockedBy().remove(permanent);
				attacker.getDamageAssignmentOrder().remove(permanent);
			}
			for(Permanent blocker : this.blockingCreatures.getListReference()){
				blocker.getBlockedCreatures().remove(permanent);
				blocker.getDamageAssignmentOrder().remove(permanent);
			}
			this.attackingCreatures.remove(permanent);
			this.blockingCreatures.remove(permanent);
		}
		int idOwner = card.getIdOwner();
		Player owner;
		for(Player player : this.priorityOrder){
			if(player.getId() == idOwner){
				owner = player;
				owner.getGraveyard().add(card);
			}
		}
	}
	
	public void resetDeclareAttacker(){
		this.attackingCreatures = new ListPointer<Permanent>();
	}
	
	public boolean isTwoPlayerGame() {
		return twoPlayerGame;
	}

	public void setTwoPlayerGame(boolean twoPlayerGame) {
		this.twoPlayerGame = twoPlayerGame;
	}

	public Map<String, Ability> getTriggered_ability() {
		return triggered_ability;
	}

	public void setTriggered_ability(Map<String, Ability> triggered_ability) {
		this.triggered_ability = triggered_ability;
	}

	public Map<String, Ability> getStatic_ability() {
		return static_ability;
	}

	public void setStatic_ability(Map<String, Ability> static_ability) {
		this.static_ability = static_ability;
	}

	public Map<String, Ability> getActivated_ability() {
		return activated_ability;
	}

	public void setActivated_ability(Map<String, Ability> activated_ability) {
		this.activated_ability = activated_ability;
	}

	public int getVariant() {
		return variant;
	}

	public void setVariant(int variant) {
		this.variant = variant;
	}

	public int getCasualVariant() {
		return casualVariant;
	}

	public void setCasualVariant(int casualVariant) {
		this.casualVariant = casualVariant;
	}

	public boolean isLimitedRangeOfInfluenceOption() {
		return limitedRangeOfInfluenceOption;	
	}

	public void setLimitedRangeOfInfluenceOption(
			boolean limitedRangeOfInfluenceOption) {
		this.limitedRangeOfInfluenceOption = limitedRangeOfInfluenceOption;
	}

	public int getRangeOfInfluence() {
		return rangeOfInfluence;
	}

	public void setRangeOfInfluence(int rangeOfInfluence) {
		this.rangeOfInfluence = rangeOfInfluence;
	}

	public boolean isAttackMultiplePlayersOption() {
		return attackMultiplePlayersOption;
	}

	public void setAttackMultiplePlayersOption(boolean attackMultiplePlayersOption) {
		this.attackMultiplePlayersOption = attackMultiplePlayersOption;
	}

	public boolean isAttackLeftOption() {
		return attackLeftOption;
	}

	public void setAttackLeftOption(boolean attackLeftOption) {
		this.attackLeftOption = attackLeftOption;
	}

	public boolean isAttackRightOption() {
		return attackRightOption;
	}

	public void setAttackRightOption(boolean attackRightOption) {
		this.attackRightOption = attackRightOption;
	}

	public boolean isDeployCreaturesOption() {
		return deployCreaturesOption;
	}

	public void setDeployCreaturesOption(boolean deployCreaturesOption) {
		this.deployCreaturesOption = deployCreaturesOption;
	}

	public boolean isSharedTeamTurnsOption() {
		return sharedTeamTurnsOption;
	}

	public void setSharedTeamTurnsOption(boolean sharedTeamTurnsOption) {
		this.sharedTeamTurnsOption = sharedTeamTurnsOption;
	}
	
	/*public LinkedList<Player> getStartingTeam() {
		return startingTeam;
	}

	public void setStartingTeam(LinkedList<Player> startingTeam) {
		this.startingTeam = startingTeam;
	}

	public LinkedList<Player> getMultiplayerVariant() {
		return multiplayerVariant;
	}

	public void setMultiplayerVariant(LinkedList<Player> multiplayerVariant) {
		this.multiplayerVariant = multiplayerVariant;
	}*/

	public int getPlayersNumber() {
		return playersNumber;
	}

	public void setPlayersNumber(int playersNumber) {
		this.playersNumber = playersNumber;
	}

	public ListPointer<Player> getChooserPlayer() {
		return chooserPlayer;
	}

	public void setChooserPlayer(int index) {
		this.chooserPlayer.movePointer(index);
	}
	
	public ListPointer<Player> getStartingPlayer() {
		return startingPlayer;
	}

	public void setStartingPlayer(int index) {
		this.startingPlayer.movePointer(index);
	}

	public int getIndexChooserPlayer() {
		return indexChooserPlayer;
	}

	public void setIndexChooserPlayer(int indexChooserPlayer) {
		this.indexChooserPlayer = indexChooserPlayer;
	}

	public LinkedList<Player> getNonActivePlayers() {
		return nonActivePlayers;
	}

	public void setNonActivePlayers(LinkedList<Player> nonActivePlayers) {
		this.nonActivePlayers = nonActivePlayers;
	}

	public LinkedList<Player> getPlayers() {
		return players;
	}


	public void setPlayers(LinkedList<Player> players) {
		this.players = players;
	}
	public Map<String, Ability> getKeyword_ability() {
		return keyword_ability;
	}

	public void setKeyword_ability(Map<String, Ability> keyword_ability) {
		this.keyword_ability = keyword_ability;
	}

	public Attempt getAttempt() {
		return attempt;
	}

	public void setAttempt(Attempt attempt) {
		this.attempt = attempt;
	}

	public LinkedList<Player> getMulliganPriorityOrder() {
		return mulliganPriorityOrder;
	}

	public void setMulliganPriorityOrder(LinkedList<Player> mulliganPriorityOrder) {
		this.mulliganPriorityOrder = mulliganPriorityOrder;
	}

	public ListPointer<Player> getMulliganPriorityMarker() {
		return mulliganPriorityMarker;
	}

	public void setMulliganPriorityMarker(ListPointer<Player> mulliganPriorityMarker) {
		this.mulliganPriorityMarker = mulliganPriorityMarker;
	}

	public LinkedList<Player> getPriorityOrder() {
		return priorityOrder;
	}

	public void setPriorityOrder(LinkedList<Player> priorityOrder) {
		this.priorityOrder = priorityOrder;
	}

	public ListPointer<Player> getPriorityMarker() {
		return priorityMarker;
	}

	public void setPriorityMarker(ListPointer<Player> priorityMarker) {
		this.priorityMarker = priorityMarker;
	}

	public boolean isControlStateBasedActions() {
		return controlStateBasedActions;
	}

	public void setControlStateBasedActions(boolean controlStateBasedActions) {
		this.controlStateBasedActions = controlStateBasedActions;
	}

	public LinkedList<Player> getSevenzerofourPlayerOrder() {
		return sevenzerofourPlayerOrder;
	}

	public void setSevenzerofourPlayerOrder(LinkedList<Player> sevenzerofourPlayerOrder) {
		this.sevenzerofourPlayerOrder = sevenzerofourPlayerOrder;
	}

	public ListPointer<Player> getSevenzerofourPlayerMarker() {
		return sevenzerofourPlayerMarker;
	}

	public void setSevenzerofourPlayerMarker(ListPointer<Player> sevenzerofourPlayerMarker) {
		this.sevenzerofourPlayerMarker = sevenzerofourPlayerMarker;
	}

	public boolean isPlayerHasToMakeChoiceszf() {
		return playerHasToMakeChoiceszf;
	}

	public void setPlayerHasToMakeChoiceszf(boolean playerHasToMakeChoiceszf) {
		this.playerHasToMakeChoiceszf = playerHasToMakeChoiceszf;
	}

	public LinkedList<Player> getScryPriorityOrder() {
		return scryPriorityOrder;
	}

	public void setScryPriorityOrder(LinkedList<Player> scryPriorityOrder) {
		this.scryPriorityOrder = scryPriorityOrder;
	}

	public ListPointer<Player> getScryPriorityMarker() {
		return scryPriorityMarker;
	}

	public void setScryPriorityMarker(ListPointer<Player> scryPriorityMarker) {
		this.scryPriorityMarker = scryPriorityMarker;
	}

	public boolean isFinishedEndPhaseOfMulligan() {
		return finishedEndPhaseOfMulligan;
	}

	public void setFinishedEndPhaseOfMulligan(boolean finishedEndPhaseOfMulligan) {
		this.finishedEndPhaseOfMulligan = finishedEndPhaseOfMulligan;
	}

	public boolean isScryTurn() {
		return scryTurn;
	}

	public void setScryTurn(boolean scryTurn) {
		this.scryTurn = scryTurn;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}
	
	public int getPrestage() {
		return prestage;
	}

	public void setPrestage(int prestage) {
		this.prestage = prestage;
	}

	public boolean isFirstTurn() {
		return firstTurn;
	}

	public void setFirstTurn(boolean firstTurn) {
		this.firstTurn = firstTurn;
	}

	public LinkedList<Player> getTurnOrder() {
		return turnOrder;
	}

	public void setTurnOrder(LinkedList<Player> turnOrder) {
		this.turnOrder = turnOrder;
	}

	public ListPointer<Player> getActivePlayer() {
		return activePlayer;
	}

	public void setActivePlayer(ListPointer<Player> activePlayer) {
		this.activePlayer.movePointer(activePlayer.getObject());
	}

	public ListPointer<Phase> getCurrentPhase() {
		return currentPhase;
	}

	public void setCurrentPhase(LinkedList<Phase> turnActivePlayer) {
		this.currentPhase.setListReference(turnActivePlayer);
		this.currentPhase.next();
	}

	public ListPointer<Step> getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(LinkedList<Step> turnActivePlayer) {
		this.currentStep.setListReference(turnActivePlayer);
		if(turnActivePlayer.size()>0){
			this.currentStep.next();
		}
	}

	public int getStepTimeFrame() {
		return stepTimeFrame;
	}

	public void setStepTimeFrame(int stepTimeFrame) {
		this.stepTimeFrame = stepTimeFrame;
	}

	public int getPhaseTimeFrame() {
		return phaseTimeFrame;
	}

	public void setPhaseTimeFrame(int phaseTimeFrame) {
		this.phaseTimeFrame = phaseTimeFrame;
	}

	public int getTurnTimeFrame() {
		return turnTimeFrame;
	}

	public void setTurnTimeFrame(int turnTimeFrame) {
		this.turnTimeFrame = turnTimeFrame;
	}

	public int getIdObjectCounter() {
		return idObjectCounter;
	}

	public void setIdObjectCounter(int idObjectCounter) {
		this.idObjectCounter = idObjectCounter;
	}

	public Zone getStack() {
		return stack;
	}

	public void setStack(Zone stack) {
		this.stack = stack;
	}

	public Zone getBattleField() {
		return battleField;
	}

	public void setBattleField(Zone battleField) {
		this.battleField = battleField;
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

	public int getNumberOfZone() {
		return numberOfZone;
	}

	public void setNumberOfZone(int numberOfZone) {
		this.numberOfZone = numberOfZone;
	}

	public ChoiceAnswer getChoice() {
		return choice;
	}

	public void setChoice(ChoiceAnswer choice) {
		this.choice = choice;
	}

	public boolean isMulliganFlag() {
		return mulliganFlag;
	}

	public void setMulliganFlag(boolean mulliganFlag) {
		this.mulliganFlag = mulliganFlag;
	}

	public boolean isMulliganPhase() {
		return mulliganPhase;
	}

	public void setMulliganPhase(boolean mulliganPhase) {
		this.mulliganPhase = mulliganPhase;
	}

	public Spell getCastingSpell() {
		return castingSpell;
	}

	public AbilityOnTheStack getActiveAbility() {
		return activeAbility;
	}

	public void setActiveAbility(AbilityOnTheStack activeAbility) {
		this.activeAbility = activeAbility;
	}

	public AbilityOnTheStack getResolvingAbility() {
		return resolvingAbility;
	}

	public void setResolvingAbility(AbilityOnTheStack resolvingAbility) {
		this.resolvingAbility = resolvingAbility;
	}

	public void setCastingSpell(Spell castingSpell) {
		this.castingSpell = castingSpell;
	}
	
	public int giveMeMyId(){  // bisogna settare i timestamp quando si creano gli oggetti
		this.idObjectCounter ++;
		return idObjectCounter;
	}

	public Spell getResolvingSpell() {
		return resolvingSpell;
	}

	public void setResolvingSpell(Spell resolvingSpell) {
		this.resolvingSpell = resolvingSpell;
	}

	public boolean isResolvedSpell() {
		return resolvedSpell;
	}

	public void setResolvedSpell(boolean resolvedSpell) {
		this.resolvedSpell = resolvedSpell;
	}

	public int getIdBattlefieldCounter() {
		this.idBattlefieldCounter++;
		return idBattlefieldCounter;
	}

	public void setIdBattlefieldCounter(int idBattlefieldCounter) {
		this.idBattlefieldCounter = idBattlefieldCounter;
	}

	public int getIdStackCounter() {
		this.idStackCounter++;
		return idStackCounter;
	}

	public void setIdStackCounter(int idStackCounter) {
		this.idStackCounter = idStackCounter;
	}

	public int getIdExileCounter() {
		this.idExileCounter++;
		return idExileCounter;
	}

	public void setIdExileCounter(int idExileCounter) {
		this.idExileCounter = idExileCounter;
	}

	public int getIdGraveyardCounter() {
		this.idGraveyardCounter++;
		return idGraveyardCounter;
	}

	public void setIdGraveyardCounter(int idGraveyardCounter) {
		this.idGraveyardCounter = idGraveyardCounter;
	}

	public ListPointer<Player> getAttackingPlayer() {
		return attackingPlayer;
	}

	public void setAttackingPlayer(ListPointer<Player> attackingPlayer) {
		this.attackingPlayer = attackingPlayer;
	}

	public ListPointer<Player> getDefendingPlayers() {
		return defendingPlayers;
	}

	public void setDefendingPlayers(ListPointer<Player> defendingPlayers) {
		this.defendingPlayers = defendingPlayers;
	}

	public ListPointer<Permanent> getAttackingCreatures() {
		return attackingCreatures;
	}

	public void setAttackingCreatures(ListPointer<Permanent> attackingCreatures) {
		this.attackingCreatures = attackingCreatures;
	}

	public ListPointer<Permanent> getBlockingCreatures() {
		return blockingCreatures;
	}

	public void setBlockingCreatures(ListPointer<Permanent> blockingCreatures) {
		this.blockingCreatures = blockingCreatures;
	}

	public ListPointer<String> getStepDeclareAttackers() {
		return stepDeclareAttackers;
	}

	public void setStepDeclareAttackers(ListPointer<String> stepDeclareAttackers) {
		this.stepDeclareAttackers = stepDeclareAttackers;
	}

	public ListPointer<String> getStepDeclareBlockers() {
		return stepDeclareBlockers;
	}

	public void setStepDeclareBlockers(ListPointer<String> stepDeclareBlockers) {
		this.stepDeclareBlockers = stepDeclareBlockers;
	}
	
	public ListPointer<String> getStepCheckEvasionAbility() {
		return stepCheckEvasionAbility;
	}

	public void setStepCheckEvasionAbility(ListPointer<String> stepCheckEvasionAbility) {
		this.stepCheckEvasionAbility = stepCheckEvasionAbility;
	}
	
	public void resetStepCheckEvasionAbility() {
		this.stepCheckEvasionAbility = new ListPointer<String>(new LinkedList<String>());
	}

	public boolean isBlockValid() {
		return blockValid;
	}

	public void setBlockValid(boolean blockValid) {
		this.blockValid = blockValid;
	}

	public ListPointer<String> getStepCombatDamage() {
		return stepCombatDamage;
	}

	public void setStepCombatDamage(ListPointer<String> stepCombatDamage) {
		this.stepCombatDamage = stepCombatDamage;
	}

	public LinkedList<Target> getPossibleTarget() {
		return possibleTarget;
	}

	public void setPossibleTarget(LinkedList<Target> possibleTarget) {
		this.possibleTarget = possibleTarget;
	}
	
	public MagicObject searchInBattlefield(int id) {
		Zone b = getBattleField();
		for(MagicObject mo : b){
			if(mo.getMagicTargetId() == id){
				return mo;
			}
		}
		return null;
	}

	public int getMulliganType() {
		return mulliganType;
	}

	public void setMulliganType(int mulliganType) {
		this.mulliganType = mulliganType;
	}

	public boolean isDiscardTurn() {
		return discardTurn;
	}

	public void setDiscardTurn(boolean discardTurn) {
		this.discardTurn = discardTurn;
	}
}
