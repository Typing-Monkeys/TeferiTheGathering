<!-- TOC ignore:true -->
# <img src="./logo.png" height=30px> **Teferi The Gathering** <img src="./logo.png" height=30px> <!-- TOC ignore:true -->

<!-- TOC ignore:true -->
### **Rule index**

<!-- TOC -->

- [ **Teferi The Gathering**  ](#-teferi-the-gathering--)
		- [**Rule index**](#rule-index)
	- [***702.2 Deathtouch*** :skull:](#7022-deathtouch-skull)
		- [**Descrizione** :mag:](#descrizione-mag)
		- [**Implementazione :computer:**](#implementazione-computer)
			- [1. Trovare `Deathtouch` nei mazzi](#1-trovare-deathtouch-nei-mazzi)
			- [2. Attivazione di `Deathtouch`](#2-attivazione-di-deathtouch)
			- [3. `Deathtouch` viene aggiunta alle state-base action](#3-deathtouch-viene-aggiunta-alle-state-base-action)
			- [4. Le carte che subiscono danno vengono marchiate con `Deathtouch`](#4-le-carte-che-subiscono-danno-vengono-marchiate-con-deathtouch)
			- [4.  Aggiunto flag `deathtouched` nel `Java`](#4--aggiunto-flag-deathtouched-nel-java)
	- [***702.3 Defender*** 🧱](#7023-defender-)
		- [**Descrizione** :mag:](#descrizione-mag-1)
		- [**Implementazione :computer:**](#implementazione-computer-1)
			- [1. Trovare `Defender` nei mazzi](#1-trovare-defender-nei-mazzi)
			- [2. Attivazione di `Defender`](#2-attivazione-di-defender)
	- [***702.4 Double Strike*** :bowling: :bowling:](#7024-double-strike-bowling-bowling)
		- [**Descrizione** :mag:](#descrizione-mag-2)
		- [**Implementazione :computer:**](#implementazione-computer-2)
			- [1. Trovare `Double Strike` nei mazzi](#1-trovare-double-strike-nei-mazzi)
	- [***702.7 First Strike*** :bowling:](#7027-first-strike-bowling)
		- [**Descrizione** :mag:](#descrizione-mag-3)
		- [**Implementazione :computer:**](#implementazione-computer-3)
			- [1. Trovare `First Strike` nei mazzi](#1-trovare-first-strike-nei-mazzi)
	- [***510.4 Gestione Turno Bonus***  :bowling: :bowling: :bowling:](#5104-gestione-turno-bonus--bowling-bowling-bowling)
		- [***Descrizione***](#descrizione)
		- [***Implementazione***](#implementazione)
			- [1. Controllare la presenza di creature con ***First/Double Strike*** tra gli attaccanti o i difensori.](#1-controllare-la-presenza-di-creature-con-firstdouble-strike-tra-gli-attaccanti-o-i-difensori)
			- [***2. Controllo se ci sono creature con Fist o Double strike tra gli attaccanti o i difensori quandi ci si trova nel combat damage step***](#2-controllo-se-ci-sono-creature-con-fist-o-double-strike-tra-gli-attaccanti-o-i-difensori-quandi-ci-si-trova-nel-combat-damage-step)
			- [***3. Riporta i puntatori delle liste delgi attaccanti in cima alla lista***](#3-riporta-i-puntatori-delle-liste-delgi-attaccanti-in-cima-alla-lista)
			- [***4. Rimetto a posto le liste delgi attaccanti dopo che si è risolto il turno bonus***](#4-rimetto-a-posto-le-liste-delgi-attaccanti-dopo-che-si-è-risolto-il-turno-bonus)
			- [***5. Aggiorno i danni subiti nel turno bonus e distruggo le eventuali carte che hanno danni maggiori ti toughness***](#5-aggiorno-i-danni-subiti-nel-turno-bonus-e-distruggo-le-eventuali-carte-che-hanno-danni-maggiori-ti-toughness)
	- [***702.8 Flash*** :flashlight:](#7028-flash-flashlight)
		- [**Descrizione** :mag:](#descrizione-mag-4)
		- [**Implementazione :computer:**](#implementazione-computer-4)
			- [1. Trovare `Flash` nei mazzi](#1-trovare-flash-nei-mazzi)
			- [2. Attivazione di `Flash`](#2-attivazione-di-flash)
	- [***702.10 Haste ⚡***](#70210-haste-)
		- [**Descrizione** :mag:](#descrizione-mag-5)
		- [**Implementazione :computer:**](#implementazione-computer-5)
			- [1. Trovare haste nei mazzi](#1-trovare-haste-nei-mazzi)
			- [2. Attivazione di Haste](#2-attivazione-di-haste)

<!-- /TOC -->


## ***702.2 Deathtouch*** :skull:

### **Descrizione** :mag:
- `.a` _Deathtouch is a static ability._
- `.b` _A creature with toughness greater than 0 that's been dealt damage by a source with deathtouch since the last time state-based actions were checked is destroyed as a state-based action._ (See rule [704](https://yawgatog.com/resources/magic-rules/#R704))
- `.c` _Any nonzero amount of combat damage assigned to a creature by a source with deathtouch is considered to be lethal damage for the purposes of determining if a proposed combat damage assignment is valid, regardless of that creature's toughness._ (See rules [510.1c-d](https://yawgatog.com/resources/magic-rules/#R5101c).)
- `.d` _The deathtouch rules function no matter what zone an object with deathtouch deals damage from._
- `.e` _If an object changes zones before an effect causes it to deal damage, its last known information is used to determine whether it had deathtouch._
- `.f` _Multiple instances of deathtouch on the same object are redundant._

### **Implementazione :computer:**

#### 1. Trovare `Deathtouch` nei mazzi
- Quando il **gioco** è nella fase `STARTING_STAGE`,
- prendiamo tutti i player in gioco e per ognuno otteniamo tutte le **abilità** di ogni carta che hanno nel mazzo,
- controlliamo quale di queste ha nell'`abilityText` la parola `Deathtouch` 
- Una volta effettuato questo controllo andiamo ad **impostare**, per ogni carta, le proprietà `keyword_ability` e `static_ability` a `true` della rispettiva abilità.

``` java
rule "702.2a" 

/** 
 	@date 2022/2023 
 	@author Cristian Cosci, Fabrizio Fagiolo, Nicolò Vescera 
 **/ 

 /*Deathtouch is a static ability.*/ 
 agenda-group "general" 
 	when 
 		$g : Game(stage == Game.STARTING_STAGE) 
 		$p : Player($id : id, $nickname: nickname, 
				$deck: deck; $lib: library, library.size() > 0); 
 		$c : Card() from $lib 
 		$la: LinkedList() from $c.getAbilities() 
 		$a : Ability(keyword_ability==false && static_ability==false &&  
 			abilityText.toLowerCase().startsWith("deathtouch")) from $la 
 	then 
 		$a.setStatic_ability(true);
 		System.out.println("702.2a -> Trovato Deathtouch: " + $c.getName()); 
 		 
 		update($p) 
 end 
```

#### 2. Attivazione di `Deathtouch`
- Quando il **gioco** è nella fase `GAME_STAGE`,
- andiamo ad analizzare le carte presenti nel **campo di battaglia** prendendo le liste sia degli **attaccanti** che dei **difensori**.
- Andando così a creare una lista di tutte le **carte in combattimento**.
- Dalla **lista** controlliamo quali carte abbiano il flag `deathtouched` impostato a `true`,
- infine, **distruggiamo** le carte selezionate.  

``` java
 rule "702.2b" 
 dialect "mvel" 
 salience 500 
 no-loop true  
 /** 
 	@date 2022/2023 
 	@author Cristian Cosci, Fabrizio Fagiolo, Nicolò Vescera 
 **/ 
 /*Deathtouched Creatures are destroyed.*/ 
 agenda-group "general" 
 when 
 	$g: Game( 
 			stage == Game.GAME_STAGE,  
 			$attaccanti: attackingCreatures.listReference, 
 			$bloccanti: blockingCreatures.listReference, 
 			controlStateBasedActions 
 		) 
  
    	$allCardsInCombat: (Permanent() from $attaccanti or 
			     Permanent() from $bloccanti) 
     	$pmt: Permanent ( 
 			cardType[0].contains("creature"), 
 			deathtouched == true 
 		) from $allCardsInCombat 
 		 
 	then 
 		System.out.println("702.2b --> Morte per Deathtouch."); 
 		System.out.println("\t"+$pmt.getNameAsString+" subisce Deathtouch"); 
 		System.out.println("\t"+$pmt.getNameAsString+" viene distrutta"); 
 		 
 		$g.destroy($pmt); 
  
 		update($g) 
 end 
```
 
#### 3. `Deathtouch` viene aggiunta alle state-base action

- Andiamo ad aggiungere la regola scritta in precedenza alla lista delle `state-base action`


``` java	 
 LinkedList ruleList = new LinkedList(); 
 ruleList.addAll([ 
 	"704.5a", 
 	"704.5b", 
 	"704.5c", 
 	"704.5f", 
 	"702.2b" 
 	"704.5g", 
 	"704.5i", 
 	"704.5j" 
 ]); 
```

#### 4. Le carte che subiscono danno vengono marchiate con `Deathtouch`

- Quando il **gioco** è nella fase `GAME_STAGE`, nello step `BEGIN_TIME_FRAME`
- andiamo ad analizzare le carte presenti nel **campo di battaglia** prendendo le creature **bloccanti** e **attaccanti**.
- Successivamente ci assicuriamo di trovarsi nello step `"combat damage"`.
- E come fatto in precedenza andiamo a creare una lista di tutte le **carte in combattimento (attaccanti e bloccanti)**, 
- la lista comprende tutte le carte indipendentemente da dove si trovano.
- Dalla **lista** controlliamo che questa sia ancora in vita (`toughness > 0`), che subisca almeno 1 danno (`combatDamage > 0`) e che non sia stata già marchiata con `Deathtouch` (`deathtouched == false`). 
- viene eseguito il controllo `deathtouched == false` perché più istanze di Deathtouch `sono ridondanti`. 
- Quindi se le precedenti condizioni risultano vere e se esiste almeno una carta che abbia `Deathtouch` tra le creature che bloccano (rispettivamente le liste `blockedCreatures` e `blockedBy`),
- la carta viene marchiata con `Deathtouch` (`deathtouched = true`)


``` java	 
 rule "702.2c" 
 dialect "mvel" 
 salience 500 
 no-loop true  
 /** 
 	@date 2022/2023 
 	@author Cristian Cosci, Fabrizio Fagiolo, Nicolò Vescera 
 **/ 
 /*Deathtouched Creatures are destroyed.*/ 
 agenda-group "general" 
 	when 
 		$g:Game(
			stage == Game.GAME_STAGE, 
			stepTimeFrame == Game.BEGIN_TIME_FRAME, 
			$blk: blockingCreatures, 
			$atk: attackingCreatures
			) 
 		eval($g.currentStep.getObject().name == "combat damage")  
 		 
 		$allCardsInCombat: ( Permanent() from $atk.listReference or
				      Permanent() from $blk.listReference) 
 		$pmt: Permanent( 
 				$difensori: blockedCreatures.listReference, 
 				$attaccanti: blockedBy.listReference, 
 				cardType[0].contains("creature"), 
 				Integer.parseInt(toughness[0]) > 0, 
 				combatDamage > 0, 
 				deathtouched == false 
 			) from $allCardsInCombat 
 		 
 		exists (  
 			Permanent(
				cardType[0].contains("creature") &&
				checkKeywordAbility("Deathtouch")) from $difensori or  
 			Permanent(
				cardType[0].contains("creature") && 
				checkKeywordAbility("Deathtouch")) from $attaccanti 
 		) 
 			 
 	then 
 		System.out.println("702.2c --> Marchiamento Deathtouch."); 
 		System.out.println("\t" + $pmt.getNameAsString+" viene marchiata con Deathtouch !"); 
 		 
 		$pmt.setDeathtouched(true); 
  
 		update($g) 
 end 
```

#### 4.  Aggiunto flag `deathtouched` nel `Java`
- Per i controlli precedenti abbiamo bisogno di inserire all'interno della classe `Permanent` il flag `deathtouched`. 
- Andiamo quindi a dichiarare il flag e successivamente creiamo il getter e il setter. 
  
``` java	 
 private boolean deathtouched = false; 
  
 public boolean isDeathtouched() { 
 	return deathtouched; 
 } 

 public void setDeathtouched(boolean deathtouched) { 
 	this.deathtouched = deathtouched; 
 } 
```

<hr>

## ***702.3 Defender*** 🧱

### **Descrizione** :mag:

- `.a` _Defender is a static ability._
- `.b` _A creature with defender can't attack._
- `.c` _Multiple instances of defender on the same creature are redundant._

### **Implementazione :computer:**

#### 1. Trovare `Defender` nei mazzi

- Quando il **gioco** è nella fase `STARTING_STAGE`,
- prendiamo tutti i player in gioco e per ognuno prendiamo tutte le **abilità** di ogni carta che hanno nel mazzo,
- controlliamo quale di queste ha nell'`abilityText` la parola `Defender` 
- Una volta effettuato questo controllo andiamo ad **impostare**, per ogni carta, le proprietà `keyword_ability` e `static_ability` a `true` della rispettiva abilità.


``` java
rule "702.3a"
/*
	9th January 2023
	702.3a. Defender is a static ability.
*/
dialect "mvel"
no-loop true
agenda-group "general"	
	when
		$g : Game(stage == Game.STARTING_STAGE)
		$p : Player($id : id, $nickname: nickname, $deck: deck, $lib: library, library.size() > 0);
		$c : Card() from $lib
		$la: LinkedList() from $c.getAbilities()
		$a : Ability(keyword_ability==false && abilityText.startsWith("Defender")) from $la
	then
		System.out.println("702.3a -> Trovata Defender");
		$a.setKeyword_ability(true);	
		$a.setStatic_ability(true);
		update($p)
end
```

#### 2. Attivazione di `Defender`

La regola `508.1a choice`, responsabile dell'individuazione **dei possibili attaccanti**, è stata modificata come segue:

- se all'interno del campo di battaglia viene trovata una carta con abilità `Defender` questa non **attaccherà** e setteremo il flag `nodefender=false`, nel caso in cui la carta non avesse l'abilità `Defender` potrà attaccare come in un normale combattimento e avremmo che il flag `nodefender` resterà settato a `true`.


``` java
rule "508.1a choice"
/*
--- November 19, 2021 ---
508.1a. The active player chooses which creatures that he or she controls, 
if any, will attack. The chosen creatures must be untapped, and each one must 
either have haste or have been controlled by the active player continuously since 
the turn began.*/
agenda-group "general"
dialect "mvel"
salience 50
no-loop true
when
	$g:Game(stage == Game.GAME_STAGE, $ac : attackingPlayer, stepTimeFrame == Game.BEGIN_TIME_FRAME)
	eval($g.currentStep.getObject().name == "declare attackers")
	eval($g.stepDeclareAttackers.getObject() == "508.1a")
	$p: Player($id : id) from $ac.object
then
	System.out.println("508.1a choice --> Il giocatore sta scegliendo gli attaccanti");
	MakeChoice choice = new MakeChoice();
	choice.idChoice = 50811;//ATTENZIONE
	choice.choiceText = "Choose which creature will attack";
	boolean found = false;
	choice.addOption(-1, "No attack");
	
	/*
	Rule 702.3b. A creature with defender can't attack.
	*/
	for(Permanent permanent : $g.battleField) {
		if(permanent.idController == $id && !permanent.getStatus().isTapped() && !permanent.summoningSickness && permanent.cardType[0].contains("creature")){
			boolean nodefender = true;
		
			// Defender check of the selected permanent.
			if (permanent.checkKeywordAbility("Defender")){
				System.out.println("702.3b -> Card has Defender, can't attack !");
				nodefender = false;
			}	
			
			if (nodefender){
				choice.addOption(permanent.magicTargetId, permanent.getNameAsString());
				found = true;
			}
		}
	}
	if(found){
		GameEngine.sendToNode(choice, Game.CHOICE, Game.MULTIPLE_CHOICE, $id);
	} else {
		GameEngine.sendToNode("You have not creature to attack.");
		System.out.println("508.1a --> Il giocatore non ha creature per attaccare");
		$g.stepDeclareAttackers.next();
	}
	update($g);
end
```

<hr>

## ***702.4 Double Strike*** :bowling: :bowling:

### **Descrizione** :mag:
- `.a` _Double strike is a static ability that modifies the rules for the combat damage step._ (See rule [510](https://yawgatog.com/resources/magic-rules/#R510), "Combat Damage Step.")
- `.b` _If at least one attacking or blocking creature has first strike (see rule [702.7](https://yawgatog.com/resources/magic-rules/#R7027)) or double strike as the combat damage step begins, the only creatures that assign combat damage in that step are those with first strike or double strike. After that step, instead of proceeding to the end of combat step, the phase gets a second combat damage step. The only creatures that assign combat damage in that step are the remaining attackers and blockers that had neither first strike nor double strike as the first combat damage step began, as well as the remaining attackers and blockers that currently have double strike. After that step, the phase proceeds to the end of combat step._
- `.c` _Removing double strike from a creature during the first combat damage step will stop it from assigning combat damage in the second combat damage step._
- `.d` _Giving double strike to a creature with first strike after it has already dealt combat damage in the first combat damage step will allow the creature to assign combat damage in the second combat damage step._
- `.e` _Multiple instances of double strike on the same creature are redundant._

### **Implementazione :computer:**
#### 1. Trovare `Double Strike` nei mazzi

- Quando il **gioco** è nella fase `STARTING_STAGE`,
- prendiamo tutti i player in gioco e per ognuno prendiamo tutte le **abilità** di ogni carta che hanno nel mazzo,
- controlliamo quale di queste ha nel `keyword_text` la parola `Double Strike`.
- Una volta effettuato questo controllo andiamo ad **impostare**, per ogni carta, le proprietà `keyword_ability` e `static_ability` a `true` della rispettiva abilità.
``` java
rule "702.4a"
/**
	@date 2022/2023
	@author Tommaso Romani, Nicolò Posta
**/
/*Double Strike is a static ability.*/
agenda-group "general"
	when
		$g : Game(stage == Game.STARTING_STAGE)
		$p : Player($id : id, $nickname: nickname, $deck: deck; $lib: library, library.size() > 0);
		$c : Card() from $lib
		$la: LinkedList() from $c.getAbilities()
		$a : Ability(keyword_ability==false && static_ability==false && 
			abilityText.toLowerCase().contains("double strike")) from $la
	then
		$a.setStatic_ability(true);
		$a.setKeyword_ability(true);
		$a.setKeyword_text("double strike");
		System.out.println("702.4a -> Trovato Double Strike: " + $c.getName());
		update($p)
end
```

<hr>

## ***702.7 First Strike*** :bowling:

### **Descrizione** :mag:
- `.a` _First strike is a static ability that modifies the rules for the combat damage step. (See rule [510](https://yawgatog.com/resources/magic-rules/#R510), "Combat Damage Step.")_
- `.b` _If at least one attacking or blocking creature has first strike or double strike (See rule [702.4](https://yawgatog.com/resources/magic-rules/#R7024) as the combat damage step begins, the only creatures that assign combat damage in that step are those with first strike or double strike. After that step, instead of proceeding to the end of combat step, the phase gets a second combat damage step. The only creatures that assign combat damage in that step are the remaining attackers and blockers that had neither first strike nor double strike as the first combat damage step began, as well as the remaining attackers and blockers that currently have double strike. After that step, the phase proceeds to the end of combat step._
- `.c` _Giving first strike to a creature without it after combat damage has already been dealt in the first combat damage step won't preclude that creature from assigning combat damage in the second combat damage step. Removing first strike from a creature after it has already dealt combat damage in the first combat damage step won't allow it to also assign combat damage in the second combat damage step (unless the creature has double strike)._
- `.d` _Multiple instances of first strike on the same creature are redundant._

### **Implementazione :computer:**
#### 1. Trovare `First Strike` nei mazzi

- Quando il **gioco** è nella fase `STARTING_STAGE`,
- prendiamo tutti i player in gioco e per ognuno prendiamo tutte le **abilità** di ogni carta che hanno nel mazzo,
- controlliamo quale di queste ha nel `keyword_text` la parola `First Strike`.
- Una volta effettuato questo controllo andiamo ad **impostare**, per ogni carta, le proprietà `keyword_ability` e `static_ability` a `true` della rispettiva abilità.
``` java
rule "702.7a"
/**
	@date 2022/2023
	@author Tommaso Romani, Nicolò Posta
**/
/*First Strike is a static ability.*/
agenda-group "general"
	when
		$g : Game(stage == Game.STARTING_STAGE)
		$p : Player($id : id, $nickname: nickname, $deck: deck; $lib: library, library.size() > 0);
		$c : Card() from $lib
		$la: LinkedList() from $c.getAbilities()
		$a : Ability(keyword_ability==false && static_ability==false && 
			abilityText.toLowerCase().contains("first strike")) from $la
	then
		$a.setStatic_ability(true);
		$a.setKeyword_ability(true);
		$a.setKeyword_text("first strike");
		System.out.println("702.7a -> Trovato First Strike: " + $c.getName());
		update($p)
end
```
<hr>

## ***510.4 Gestione Turno Bonus***  :bowling: :bowling: :bowling:

### ***Descrizione***

- _If at least one attacking or blocking creature has first strike (see rule 702.7) or double strike (see rule 702.4) as the combat damage step begins, the only creatures that assign combat damage in that step are those with first strike or double strike. After that step, instead of proceeding to the end of combat step, the phase gets a second combat damage step. The only creatures that assign combat damage in that step are the remaining attackers and blockers that had neither first strike nor double strike as the first combat damage step began, as well as the remaining attackers and blockers that currently have double strike. After that step, the phase proceeds to the end of combat step_


### ***Implementazione***
#### 1. Controllare la presenza di creature con ***First/Double Strike*** tra gli attaccanti o i difensori.
- Se sono presenti creature con `First` o `Double Strike` allora viene aggiunto un `combat damage` step bonus.
``` java
rule "506.1 part 3"
/* Tommaso Romani Nicolò Posta
There are two combat damage steps if any attacking or blocking creature has first strike 
(see rule 702.7) or double strike (see rule 702.4).*/
dialect "mvel"
salience 50
no-loop true
agenda-group "general"
when
	$g:Game(stage == Game.GAME_STAGE, $bf: battleField, $ph: currentPhase.getObject())
	eval($g.currentPhase.getObject().name == "combat")
	eval($ph.size() <= 5)
	//TODO: mettere che siano tra gli attaccanti o i bloccanti
	exists (Permanent(
			(checkKeywordAbility("Double strike") || checkKeywordAbility("First strike"))
		) from $bf)
then
	System.out.println("506.1 part 3 -> Aggiunto combat damage per First/Double strike");
	
	$ph.remove($ph.size()-1);
	$ph.remove($ph.size()-1);
	$ph.add(new Step("combat damage",true));
	$ph.add(new Step("combat damage",false));
	$ph.add(new Step("end of combat",false));

	update($g)

end
```
#### ***2. Controllo se ci sono creature con Fist o Double strike tra gli attaccanti o i difensori quandi ci si trova nel combat damage step***
- Se ci si trova nel `Combat Damage Step` aggiuntivo allora mi salvo tutte le creature in una lista ausiliaria.
- Pulisco la lista degli attacker e ci riaggiungo le creature con `First/Double Strike`.
- Aggiorno la variabile booleana `firstStrikeAttacking` a `true` per informare che le creature con `First/Double Strike` stanno per attaccare.

``` java
rule "510.4 part 1a"
/*
Tommaso Romani Nicolò Posta
February 3, 2022/2023
510.4. If at least one attacking or blocking creature has first strike (see rule 702.7) or double strike (see rule 702.4) as the combat damage step begins, the 
only creatures that assign combat damage in that step are those with first strike or double strike. After that step, instead of proceeding to the end of 
combat step, the phase gets a second combat damage step. The only creatures that assign combat damage in that step are the remaining attackers and blockers 
that had neither first strike nor double strike as the first combat damage step began, as well as the remaining attackers and blockers that currently have 
double strike. After that step, the phase proceeds to the end of combat step.*/
agenda-group "general"
dialect "mvel"
no-loop true
salience 500
when
	$g:Game(stage == Game.GAME_STAGE, stepTimeFrame == Game.BEGIN_TIME_FRAME, $atk: attackingCreatures, $blk: blockingCreatures)
	eval($g.currentStep.getObject().name == "combat damage" && $g.currentStep.getObject().additional == true)
	eval($g.stepCombatDamage.getObject() == "510.1a")
	eval($g.firstStrikeAttaking == false)
	$allCardsInCombact: (Permanent() from $atk.listReference or Permanent() from $blk.listReference)

	exists(Permanent(
		(checkKeywordAbility("Double strike") || checkKeywordAbility("First strike"))
	) from $allCardsInCombact)
	
then
	System.out.println("510.4 part 1a");
	//Mi salvo la lista di tutti gli attaccanti e difensori di prima
	System.out.println("Tutti gli attaccanti selezionati "+$atk.listReference);
	

	// copia della lista
	for(Permanent tmp: $atk.listReference) {
		$g.stepFirstStikeAttackingCreatures.add(tmp);
	}

	// ricerca carte first/double strike
	$atk.listReference.clear();
	for(Permanent tmp: $g.stepFirstStikeAttackingCreatures) {
		if (tmp.checkKeywordAbility("First strike") || tmp.checkKeywordAbility("Double strike")){
			System.out.println("Trovato FIRST/DOUBLE STRIKE da riaggiungere alla lista degli attaccanti");
			$atk.listReference.add(tmp);
		}
	}
	
	System.out.println("Tutti gli attaccanti selezionati dopo averli salvati " + $g.stepFirstStikeAttackingCreatures);
	System.out.println("Tutti gli attaccanti selezionati con First Strike" + $atk.listReference);

	$g.firstStrikeAttaking = true;
	update($g)

end
```
#### ***3. Riporta i puntatori delle liste delgi attaccanti in cima alla lista***
- Controllo di essere ancora nel turno bonus.
- Controllo di trovarmi ancora nella fase di attacco delle creature con `First/Double Strike` e che `notRedoDamage` sia `false`.
- Allora sposto i puntatori in cima alle liste, aggiorni il time frame e il game.


``` java
rule "510.4 part 3a"
/*
Tommaso Romani Nicolò Posta
February 3, 2022/2023
510.4. If at least one attacking or blocking creature has first strike (see rule 702.7) or double strike (see rule 702.4) as the combat damage step begins, the 
only creatures that assign combat damage in that step are those with first strike or double strike. After that step, instead of proceeding to the end of 
combat step, the phase gets a second combat damage step. The only creatures that assign combat damage in that step are the remaining attackers and blockers 
that had neither first strike nor double strike as the first combat damage step began, as well as the remaining attackers and blockers that currently have 
double strike. After that step, the phase proceeds to the end of combat step.*/
agenda-group "general"
dialect "mvel"
no-loop true
salience 400
when
	$g:Game(stage == Game.GAME_STAGE, stepTimeFrame == Game.DURING_TIME_FRAME)
	eval($g.currentStep.getObject().name == "combat damage" && $g.currentStep.getObject().additional == true)
	eval($g.stepCombatDamage.getObject() == "510.3")
	eval($g.firstStrikeAttaking == true)
	eval($g.notRedoDamage == true)
then

	$g.attackingPlayer.toHead();
	$g.defendingPlayers.toHead();
	
	$g.attackingCreatures.toHead();
	
	$g.blockingCreatures.toHead();

	$g.stepCombatDamage.movePointer(0);
	$g.currentStep.next();

	$g.stepTimeFrame = Game.BEGIN_TIME_FRAME;
	System.out.println("510.4 part 3a");

	$g.notRedoDamage = false;
	update($g)

end
```
#### ***4. Rimetto a posto le liste delgi attaccanti dopo che si è risolto il turno bonus***
- Controllo che non ci si trovi più nello step di danno bonus, ma che sia ancora vero `firstStrikeAttacking`.
- Controllo che si sia già triggerata la ***510.4 part 3a*** tramite `notRedoDamage`.
- Allora rimetto a posto le liste affinchè possa svolgersi correttamente il turno delle creature che non hanno attaccato nel turno bonus.

``` java
rule "510.4 part 2a"
/*
Tommaso Romani Nicolò Posta
February 3, 2022/2023
510.4. If at least one attacking or blocking creature has first strike (see rule 702.7) or double strike (see rule 702.4) as the combat damage step begins, the 
only creatures that assign combat damage in that step are those with first strike or double strike. After that step, instead of proceeding to the end of 
combat step, the phase gets a second combat damage step. The only creatures that assign combat damage in that step are the remaining attackers and blockers 
that had neither first strike nor double strike as the first combat damage step began, as well as the remaining attackers and blockers that currently have 
double strike. After that step, the phase proceeds to the end of combat step.*/
agenda-group "general"
dialect "mvel"
no-loop true
salience 500
when
	$g:Game(stage == Game.GAME_STAGE, stepTimeFrame == Game.BEGIN_TIME_FRAME, $atk: attackingCreatures)
	eval($g.currentStep.getObject().name == "combat damage" && $g.currentStep.getObject().additional == false)
	eval($g.stepCombatDamage.getObject() == "510.1a")
	eval($g.firstStrikeAttaking == true)
	eval($g.notRedoDamage == false)
then
	System.out.println("510.4 part 2a");


	for(Permanent tmp: $atk.listReference) {
		$g.auxiliaryListFirstStrike.add(tmp);
	
	}
	$atk.listReference.clear();

	// double strike
	for(Permanent tmp: $g.auxiliaryListFirstStrike) {
		if (tmp.checkKeywordAbility("Double strike")){
			$atk.listReference.add(tmp);
		}
	}
	
	for(Permanent tmp: $g.stepFirstStikeAttackingCreatures) {
		if (!tmp.checkKeywordAbility("First strike") && !tmp.checkKeywordAbility("Double strike")){
			$atk.listReference.add(tmp);
		}
	}

	$g.stepFirstStikeAttackingCreatures.clear();

	for(Permanent tmp: $g.auxiliaryListFirstStrike) {
		if (!tmp.checkKeywordAbility("Double strike")){
			$g.stepFirstStikeAttackingCreatures.add(tmp);
		}
	}
	$g.auxiliaryListFirstStrike.clear();
	$g.firstStrikeAttaking = false;
	update($g);

end
```
#### ***5. Aggiorno i danni subiti nel turno bonus e distruggo le eventuali carte che hanno danni maggiori ti toughness***
- Controllo di essere ancora nel turno bonus all'ultimo step del danno.
- Controllo che la regola ***120.3e*** si è triggerata aggiornando la variabile `updateDamage`.
- Controllo che nel battlefield ci sono permanenti che hanno danni maggiori di `toughness`.
- Allora distruggo quei permanenti.

``` java
rule "510.4 update_damage"
/*
Tommaso Romani Nicolò Posta
February 3, 2022/2023
510.4. If at least one attacking or blocking creature has first strike (see rule 702.7) or double strike (see rule 702.4) as the combat damage step begins, the 
only creatures that assign combat damage in that step are those with first strike or double strike. After that step, instead of proceeding to the end of 
combat step, the phase gets a second combat damage step. The only creatures that assign combat damage in that step are the remaining attackers and blockers 
that had neither first strike nor double strike as the first combat damage step began, as well as the remaining attackers and blockers that currently have 
double strike. After that step, the phase proceeds to the end of combat step.*/
agenda-group "general"
dialect "mvel"
no-loop true
salience 500
when
	$g:Game(stage == Game.GAME_STAGE, stepTimeFrame == Game.BEGIN_TIME_FRAME, $bf: battleField)
	eval($g.currentStep.getObject().name == "combat damage" && $g.currentStep.getObject().additional == false)
	eval($g.updateDamage == true)

	$pmt: Permanent(
				cardType[0].contains("creature"),
				Integer.parseInt(toughness[0]) > 0,
				Integer.parseInt(toughness[0]) <= markedDamage
			) from $bf
then
	
	System.out.println("510.4 --> Controlling if there are creatures that have marked damage greater or equal than toughness.");
	System.out.println($pmt.getNameAsString+" ha toughness "+Integer.parseInt($pmt.toughness[0])+" e danni "+$pmt.markedDamage);
	System.out.println($pmt.getNameAsString+" viene distrutta");
	$g.destroy($pmt);
	System.out.println("----------- TEST 510.4 -----------");
	$g.updateDamage = false;
	update($g)
end
```
<hr>


## ***702.8 Flash*** :flashlight:

### **Descrizione** :mag:

- `.a` _Flash is a static ability that functions in any zone from which you could play the card it's on. "Flash" means "You may play this card any time you could cast an instant."_

- `.b` _702.8b. Multiple instances of flash on the same object are redundant._

### **Implementazione :computer:**

#### 1. Trovare `Flash` nei mazzi

- Quando il **gioco** è nella fase `STARTING_STAGE`,
- prendiamo tutti i player in gioco e per ognuno prendiamo tutte le **abilità** di ogni carta che hanno nel mazzo,
- controlliamo quale di queste ha nel `keyword_text` la parola `flash`.
- Una volta effettuato questo controllo andiamo ad **impostare**, per ogni carta, le proprietà `keyword_ability` e `static_ability` a `true` della rispettiva abilità.


``` java
rule "702.8a"
/**
	 Flash is a static ability
	 
	 @author Cristian Cosci, Nicolò Vescera
	 @date 2022/2023
**/
dialect "mvel"
no-loop true
agenda-group "general"	
	when
		$g : Game(stage == Game.STARTING_STAGE)
		$p : Player($id : id, $nickname: nickname, $deck: deck; $lib: library, library.size() > 0);
		$c : Card() from $lib
		$la: LinkedList() from $c.getKeywordAbilities()
		$a : Ability(keyword_ability==false && static_ability==false && 
			keyword_text.toLowerCase().startsWith("flash")) from $la
	then
		$a.setStatic_ability(true);
		$a.setFlash(true);
		
		System.out.println("702.8a -> Trovato Flash: " + $c.getName());

		update($p)
end
```


#### 2. Attivazione di `Flash`

La regola che gestisce le instant è stata modificata nel seguente modo:

- Per ogni carta presente **nella mano del player**,
- aggiungo un secondo controllo a quello già esistente che aggiunge la carta alla lista delle **possibili carte giocabili** se ha tra le sue abilità `Flash`.
- Quindi così i controlli andranno ad **"escludersi" a vicenda**: se la carta è già stata aggiunta al primo controllo, risulterà inutile controllare tutte le sue abilità per poi aggiungerla erroneamente una seconda volta.


``` java
// Casting Spells
rule "601.2a instant"
/* 
November 19, 2021
601.2a
To propose the casting of a spell, a player first moves that card (or that copy of a card) from where it is to the stack. It becomes the topmost 
object on the stack. It has all the characteristics of the card (or the copy of a card) associated with it, and that player becomes its controller. 
The spell remains on the stack until it's countered, it resolves, or an effect moves it elsewhere.
*/

agenda-group "general"
dialect "mvel"
when 
	Game(stage == Game.GAME_STAGE, castingSpell == null)
	
	$act: Action($id: id, option == Game.CAST_INSTANT_SPELL)
	$p: Player(id == $id, $hand: hand)

then
	MakeChoice choice = new MakeChoice();
	choice.idChoice = 60121;
	choice.choiceText = "Choose which card to play";
	boolean found = false;
	// La ricerca delle carte castabili va fatta in tutte le zone
	for(Card card : $hand) {
		boolean foundInHand = false;
		
		if( card.cardType.size() > 0 && 
			card.cardType[0].contains("instant") && 
			!card.cardType[0].contains("land")) {
				choice.addOption(card.magicTargetId, card.getNameAsString());
				found = true;
				foundInHand = true;
		}
		
		if (!foundInHand){
			System.out.println("702.8a -> Sto cercando flash tra le varie zone");
			
			for (LinkedList abilityList: card.getKeywordAbilities()){
				for (Ability ability: abilityList){
					if (ability.hasFlash()) {
						System.out.println("702.8a -> Carta giocabile con Flash " + card.getNameAsString());
						choice.addOption(card.magicTargetId, card.getNameAsString());
						found = true;
					}
				}
			}
		}
	}

	if(found) {
		GameEngine.sendToNode(choice, Game.CHOICE, Game.ONE_OF_CHOICE, $id);
		GameEngine.sendToNode("The player " + $p.nickname + " wants cast a spell.");
		System.out.println("The player " + $p.nickname + " wants cast a spell.");
	} else {
		GameEngine.sendToNode("You have not instant spells to cast.");
		System.out.println("601.2a -> The player " + $p.nickname + "  has not instant spells to cast.");
		retract($p.action);
		$p.action = new Action($id,0,0,0);
		insert($p.action);
	}
end
```

<hr>

## ***702.10 Haste ⚡***


### **Descrizione** :mag:

- `.a` _Haste is a static ability._

- `.b` _If a creature has haste, it can attack even if it hasn't been controlled by its controller continuously since their most recent turn began._  (See rule [302.6](https://yawgatog.com/resources/magic-rules/#R3026).)

- `.c` _If a creature has haste, its controller can activate its activated abilities whose cost includes the tap symbol or the untap symbol even if that creature hasn't been controlled by that player continuously since their most recent turn began._  (See rule [302.6](https://yawgatog.com/resources/magic-rules/#R3026).)

- `.d` Multiple instances of haste on the same creature are redundant.

### **Implementazione :computer:**

#### 1. Trovare haste nei mazzi


- Quando il **gioco** è nella fase `STARTING_STAGE`,
- prendiamo tutti i player in gioco e per ognuno prendiamo tutte le **abilità** di ogni carta che hanno nel mazzo,
- controlliamo quale di queste ha nel `keyword_text` la parola `haste`.
- Una volta effettuato questo controllo andiamo ad **impostare**, per ogni carta, le proprietà `keyword_ability` e `static_ability` a `true` della rispettiva abilità.


<br>


```java

/*
  Febbraio 2023
  Haste is a static ability.
*/

rule "702.10a"
agenda-group "general"	
dialect "mvel"
    when 
        $g:  Game(stage == Game.STARTING_STAGE)
        $p : Player($id : id, $nickname: nickname, $deck: deck; $lib: library, library.size() > 0);
        $c : Card() from $lib
        $la: LinkedList() from $c.getAbilities()
        $a : Ability(keyword_ability==false && static_ability == false && abilityText.startsWith("haste")) from $la
        
        then
  	      $a.setStatic_ability(true);
  	      System.out.println("Trovato Haste");
  	      update($p)
end
```

<br>

#### 2. Attivazione di Haste

- Per prima cosa andiamo a **prendere tutte le carte** presenti nel `campo di battaglia`.
- In seguito selezioniamo solo le carte di tipo _Creatura_ (`cardType[0] == "creature"`), che abbiano l'abilità `Haste` e con il flag `summoningSickness = true`.
- Infine, per ogni Carta risultante dalla precedente selezione, impostiamo il flag `summoningSickness=false` dato che l'abilità `Haste` va ad **invalidare la summoningSickness.** 


<br>


```java
/*
    Febbraio 2023

    If a creature has haste, it can attack even if it hasn't been controlled by its controller continuously since their most recent turn began.
    (See rule 302.6.)
    
    If a creature has haste, its controller can activate its activated abilities whose cost includes the tap symbol or the untap symbol 
    even if that creature hasn't been controlled by that player continuously since their most recent turn began.(See rule 302.6.)
    
    Multiple instances of haste on the same creature are redundant.
*/

rule "702.10b"
agenda-group "general"	
no-loop true
dialect "mvel"
    when 
        $g: Game( 
  		      stage == Game.GAME_STAGE,
  		      $bf: battleField
  	      )
        $pmt: Permanent(
  		      cardType[0].contains("creature"),
  		      summoningSickness,
  		      checkKeywordAbility("haste")
  		      ) from $bf
        then 
  	      $pmt.summoningSickness=false;
  	      System.out.println("Trovata creatura con haste nel battlefield");
  	      
  	      update($g)
end
```