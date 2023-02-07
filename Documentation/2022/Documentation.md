<!-- TOC ignore:true -->
# **Teferi The Gatering** :pig: :bikini:

<!-- TOC ignore:true -->
### **Rule index**

<!-- TOC -->

- [**Teferi The Gatering** :pig: :bikini:](#teferi-the-gatering-pig-bikini)
    - [**Rule index**](#rule-index)
  - [***702.2 Deathtouch*** :skull:](#7022-deathtouch-skull)
    - [**Descrizione** :mag:](#descrizione-mag)
    - [**Implementazione :computer:**](#implementazione-computer)
  - [***702.3 Defender*** 🧱](#7023-defender-)
    - [**Descrizione** :mag:](#descrizione-mag-1)
    - [**Implementazione :computer:**](#implementazione-computer-1)
  - [***702.4 Double Strike*** :bowling: :bowling:](#7024-double-strike-bowling-bowling)
    - [**Descrizione** :mag:](#descrizione-mag-2)
    - [**Implementazione :computer:**](#implementazione-computer-2)
  - [***702.7 First Strike*** :bowling:](#7027-first-strike-bowling)
    - [**Descrizione** :mag:](#descrizione-mag-3)
    - [**Implementazione :computer:**](#implementazione-computer-3)
  - [***702.8 Flash*** :flashlight:](#7028-flash-flashlight)
    - [**Descrizione** :mag:](#descrizione-mag-4)
    - [**Implementazione :computer:**](#implementazione-computer-4)
  - [***702.10 Haste ⚡***](#70210-haste-)
    - [**Descrizione** :mag:](#descrizione-mag-5)
    - [**Implementazione :computer:**](#implementazione-computer-5)

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


<hr>

## ***702.3 Defender*** 🧱

### **Descrizione** :mag:

- `.a` _Defender is a static ability._
- `.b` _A creature with defender can't attack._
- `.c` _Multiple instances of defender on the same creature are redundant._

### **Implementazione :computer:**


<hr>

## ***702.4 Double Strike*** :bowling: :bowling:

### **Descrizione** :mag:
- `.a` _Double strike is a static ability that modifies the rules for the combat damage step._ (See rule [510](https://yawgatog.com/resources/magic-rules/#R510), "Combat Damage Step.")
- `.b` _If at least one attacking or blocking creature has first strike (see rule [702.7](https://yawgatog.com/resources/magic-rules/#R7027)) or double strike as the combat damage step begins, the only creatures that assign combat damage in that step are those with first strike or double strike. After that step, instead of proceeding to the end of combat step, the phase gets a second combat damage step. The only creatures that assign combat damage in that step are the remaining attackers and blockers that had neither first strike nor double strike as the first combat damage step began, as well as the remaining attackers and blockers that currently have double strike. After that step, the phase proceeds to the end of combat step._
- `.c` _Removing double strike from a creature during the first combat damage step will stop it from assigning combat damage in the second combat damage step._
- `.d` _Giving double strike to a creature with first strike after it has already dealt combat damage in the first combat damage step will allow the creature to assign combat damage in the second combat damage step._
- `.e` _Multiple instances of double strike on the same creature are redundant._

### **Implementazione :computer:**


<hr>

## ***702.7 First Strike*** :bowling:

### **Descrizione** :mag:
- `.a` _First strike is a static ability that modifies the rules for the combat damage step. (See rule [510](https://yawgatog.com/resources/magic-rules/#R510), "Combat Damage Step.")_
- `.b` _If at least one attacking or blocking creature has first strike or double strike (See rule [702.4](https://yawgatog.com/resources/magic-rules/#R7024) as the combat damage step begins, the only creatures that assign combat damage in that step are those with first strike or double strike. After that step, instead of proceeding to the end of combat step, the phase gets a second combat damage step. The only creatures that assign combat damage in that step are the remaining attackers and blockers that had neither first strike nor double strike as the first combat damage step began, as well as the remaining attackers and blockers that currently have double strike. After that step, the phase proceeds to the end of combat step._
- `.c` _Giving first strike to a creature without it after combat damage has already been dealt in the first combat damage step won't preclude that creature from assigning combat damage in the second combat damage step. Removing first strike from a creature after it has already dealt combat damage in the first combat damage step won't allow it to also assign combat damage in the second combat damage step (unless the creature has double strike)._
- `.d` _Multiple instances of first strike on the same creature are redundant._

### **Implementazione :computer:**


<hr>

## ***702.8 Flash*** :flashlight:

### **Descrizione** :mag:

- `.a` _Flash is a static ability that functions in any zone from which you could play the card it's on. "Flash" means "You may play this card any time you could cast an instant."_

- `.b` _702.8b. Multiple instances of flash on the same object are redundant._

### **Implementazione :computer:**


<hr>

## ***702.10 Haste ⚡***


### **Descrizione** :mag:

- `.a` _Haste is a static ability._

- `.b` _If a creature has haste, it can attack even if it hasn't been controlled by its controller continuously since their most recent turn began._  (See rule [302.6](https://yawgatog.com/resources/magic-rules/#R3026).)

- `.c` _If a creature has haste, its controller can activate its activated abilities whose cost includes the tap symbol or the untap symbol even if that creature hasn't been controlled by that player continuously since their most recent turn began._  (See rule [302.6](https://yawgatog.com/resources/magic-rules/#R3026).)

- `.d` Multiple instances of haste on the same creature are redundant.

### **Implementazione :computer:**
