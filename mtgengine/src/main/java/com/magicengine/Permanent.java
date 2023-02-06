package com.magicengine;

import java.util.*;

public class Permanent extends MagicObject {

	Status status;
	private Card originCard;
	private int idBattlefield;
	private boolean transformed; // Da gestire per le carte double face
	private boolean summoningSickness;
	private boolean attacking;
	private boolean attackAlone;
	private boolean attackingAlone;
	private boolean blocking;
	private boolean blockAlone;
	private boolean blockingAlone;
	private boolean blocked;
	private boolean removedFromCombat;
	private int combatDamage;
	private int damageDealt;
	private int markedDamage;
	private int damageToAssign;
	private boolean Haste;
	private LinkedList <Integer> attached_to;
	private LinkedList<Integer> attached_by;
	private LinkedList<Permanent> attby_perm;
	
	// lista degli attaccanti che possono essere bloccati
	// TODO: modificare costruttore ed altri metodi
	private LinkedList<Permanent> attackersICanBlock;
	

	@Override
	public String toString() {
		return "Permanent [status=" + status + ", originCard=" + originCard + ", idBattlefield=" + idBattlefield
				+ ", transformed=" + transformed + ", summoningSickness=" + summoningSickness + ", attacking="
				+ attacking + ", attackAlone=" + attackAlone + ", attackingAlone=" + attackingAlone + ", blocking="
				+ blocking + ", blockAlone=" + blockAlone + ", blockingAlone=" + blockingAlone + ", blocked=" + blocked
				+ ", removedFromCombat=" + removedFromCombat + ", combatDamage=" + combatDamage + ", damageDealt="
				+ damageDealt + ", markedDamage=" + markedDamage + ", damageToAssign=" + damageToAssign
				+ ", attached_to=" + attached_to + ", attached_by=" + attached_by + ", target=" + target
				+ ", blockedCreatures=" + blockedCreatures + ", blockedBy=" + blockedBy + ", damageAssignmentOrder="
				+ damageAssignmentOrder + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attached_by == null) ? 0 : attached_by.hashCode());
		result = prime * result + ((attached_to == null) ? 0 : attached_to.hashCode());
		result = prime * result + (attackAlone ? 1231 : 1237);
		result = prime * result + (attacking ? 1231 : 1237);
		result = prime * result + (attackingAlone ? 1231 : 1237);
		result = prime * result + (blockAlone ? 1231 : 1237);
		result = prime * result + (blocked ? 1231 : 1237);
		result = prime * result + ((blockedBy == null) ? 0 : blockedBy.hashCode());
		result = prime * result + ((blockedCreatures == null) ? 0 : blockedCreatures.hashCode());
		result = prime * result + (blocking ? 1231 : 1237);
		result = prime * result + (blockingAlone ? 1231 : 1237);
		result = prime * result + combatDamage;
		result = prime * result + ((damageAssignmentOrder == null) ? 0 : damageAssignmentOrder.hashCode());
		result = prime * result + damageDealt;
		result = prime * result + damageToAssign;
		result = prime * result + idBattlefield;
		result = prime * result + markedDamage;
		result = prime * result + ((originCard == null) ? 0 : originCard.hashCode());
		result = prime * result + (removedFromCombat ? 1231 : 1237);
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + (summoningSickness ? 1231 : 1237);
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + (transformed ? 1231 : 1237);
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
		Permanent other = (Permanent) obj;
		if (attached_by == null) {
			if (other.attached_by != null)
				return false;
		} else if (!attached_by.equals(other.attached_by))
			return false;
		if (attached_to == null) {
			if (other.attached_to != null)
				return false;
		} else if (!attached_to.equals(other.attached_to))
			return false;
		if (attackAlone != other.attackAlone)
			return false;
		if (attacking != other.attacking)
			return false;
		if (attackingAlone != other.attackingAlone)
			return false;
		if (blockAlone != other.blockAlone)
			return false;
		if (blocked != other.blocked)
			if (other.blockedCreatures != null)
			return false;
		if (blockedBy == null) {
			if (other.blockedBy != null)
				return false;
		} else if (!blockedBy.equals(other.blockedBy))
			return false;
		if (blockedCreatures == null) {
				return false;
		} else if (!blockedCreatures.equals(other.blockedCreatures))
			return false;
		if (blocking != other.blocking)
			return false;
		if (blockingAlone != other.blockingAlone)
			return false;
		if (combatDamage != other.combatDamage)
			return false;
		if (damageAssignmentOrder == null) {
			if (other.damageAssignmentOrder != null)
				return false;
		} else if (!damageAssignmentOrder.equals(other.damageAssignmentOrder))
			return false;
		if (damageDealt != other.damageDealt)
			return false;
		if (damageToAssign != other.damageToAssign)
			return false;
		if (idBattlefield != other.idBattlefield)
			return false;
		if (markedDamage != other.markedDamage)
			return false;
		if (originCard == null) {
			if (other.originCard != null)
				return false;
		} else if (!originCard.equals(other.originCard))
			return false;
		if (removedFromCombat != other.removedFromCombat)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (summoningSickness != other.summoningSickness)
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (transformed != other.transformed)
			return false;
		return true;
	}

	public void setAttached_to(LinkedList<Integer> attached_to) {
		this.attached_to = attached_to;
	}

	public void setAttached_by(LinkedList<Integer> attached_by) {
		this.attached_by = attached_by;
	}
	private Target target; 	//target combattimento	
	//private int target_id; 	//target aura(???)
	private ListPointer<Permanent> blockedCreatures;
	private ListPointer<Permanent> blockedBy;
	private ListPointer<Permanent> damageAssignmentOrder;	
	
	public Permanent(Card card, int idObject, int idController, int idBattlefield) {
		super(card, idObject);
		this.status = new Status(false, false, true, true);
		this.originCard = card;
		this.idBattlefield = idBattlefield;
		this.transformed = false;
		this.summoningSickness = true;   //modificare in false per velocizzare test
		this.attacking = false;
		this.attackAlone = false;
		this.attackingAlone = false;
		this.blocking = false;
		this.blockAlone = false;
		this.blockingAlone = false;
		this.blocked = false;
		this.removedFromCombat = false;
		this.combatDamage = 0;
		this.damageDealt = 0;
		this.markedDamage = 0;
		this.damageToAssign = 0;
		this.setIdController(idController);
		this.target = null;
		//this.target_id = -1;
		this.blockedCreatures = new ListPointer<Permanent>(new LinkedList<Permanent>());
		this.blockedBy = new ListPointer<Permanent>(new LinkedList<Permanent>());
		this.damageAssignmentOrder = new ListPointer<Permanent>(new LinkedList<Permanent>());
		this.attached_to = new LinkedList<Integer>();
		this.attached_by = new LinkedList<Integer>();
		this.attby_perm = new LinkedList<Permanent>();
		this.attackersICanBlock = new LinkedList<Permanent>();
	}

	// Costruttore per il PrivateGame (in caso di faceDown = true dovr� essere messa una creatura 2/2)
	public Permanent(Permanent permanent, boolean faceDown)
	{
		//super(permanent, faceDown);
		super(permanent, faceDown, permanent.getMagicTargetId()); 
		this.status = permanent.getStatus();
		if(faceDown)
			this.originCard = new Card(permanent.getOriginCard(), true);
		else
			this.originCard = new Card(permanent.getOriginCard(), false);
		this.idBattlefield = permanent.getIdBattlefield();
		this.transformed = permanent.isTransformed();
		this.summoningSickness = permanent.isSummoningSickness();
		this.attacking = permanent.isAttacking();
		this.attached_to = permanent.attached_to;
		this.attached_by = permanent.attached_by;
		this.attby_perm = permanent.attby_perm;
	}
	
	public boolean isHaste() {
		return Haste;
	}
	
	public boolean getHaste() {
		return Haste;
	}

	public void setHaste(boolean Haste) {
		this.Haste = Haste;
	}
	
	public LinkedList<Permanent> getAttby_perm() {
		return attby_perm;
	}

	public void setAttby_perm(LinkedList<Permanent> attby_perm) {
		this.attby_perm = attby_perm;
	}

	/*public int getTarget_id() {
		return target_id;
	}

	public void setTarget_id(int target_id) {
		this.target_id = target_id;
	}*/

	public Status getStatus() {
		return status;
	}

	public LinkedList<Integer> getAttached_by() {
		return attached_by;
	}

	public LinkedList <Integer> getAttached_to() {
		return attached_to;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Card getOriginCard() {
		return originCard;
	}

	public void setOriginCard(Card originCard) {
		this.originCard = originCard;
	}
	
	public int getIdBattlefield() {
		return idBattlefield;
	}

	public void setIdBattlefield(int idBattlefield) {
		this.idBattlefield = idBattlefield;
	}

	public boolean isTransformed() {
		return transformed;
	}

	public void setTransformed(boolean transformed) {
		this.transformed = transformed;
	}

	public boolean isSummoningSickness() {
		return summoningSickness;
	}

	public void setSummoningSickness(boolean summoningSickness) {
		this.summoningSickness = summoningSickness;
	}

	public boolean isAttacking() {
		return attacking;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	public boolean isAttackAlone() {
		return attackAlone;
	}

	public void setAttackAlone(boolean attackAlone) {
		this.attackAlone = attackAlone;
	}

	public boolean isAttackingAlone() {
		return attackingAlone;
	}

	public void setAttackingAlone(boolean attackingAlone) {
		this.attackingAlone = attackingAlone;
	}

	public boolean isBlocking() {
		return blocking;
	}

	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}

	public boolean isBlockAlone() {
		return blockAlone;
	}

	public void setBlockAlone(boolean blockAlone) {
		this.blockAlone = blockAlone;
	}

	public boolean isBlockingAlone() {
		return blockingAlone;
	}

	public void setBlockingAlone(boolean blockingAlone) {
		this.blockingAlone = blockingAlone;
	}

	public boolean isBlocked() {
		return blocked;
	}


	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public boolean isRemovedFromCombat() {
		return removedFromCombat;
	}

	public void setRemovedFromCombat(boolean removedFromCombat) {
		this.removedFromCombat = removedFromCombat;
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

	public int getMarkedDamage() {
		return markedDamage;
	}

	public void setMarkedDamage(int markedDamage) {
		this.markedDamage = markedDamage;
	}

	public int getDamageToAssign() {
		return damageToAssign;
	}

	public void setDamageToAssign(int damageToAssign) {
		this.damageToAssign = damageToAssign;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}


	public ListPointer<Permanent> getBlockedCreatures() {
		return blockedCreatures;
	}

	public void setBlockedCreatures(ListPointer<Permanent> blockedCreatures) {
		this.blockedCreatures = blockedCreatures;
	}
	
	public void resetBlockedCreatures() {
		this.blockedCreatures = new ListPointer<Permanent>(new LinkedList<Permanent>());
	}

	public ListPointer<Permanent> getBlockedBy() {
		return blockedBy;
	}

	public void setBlockedBy(ListPointer<Permanent> blockedBy) {
		this.blockedBy = blockedBy;
	}
	
	public void resetBlockedBy() {
		this.blockedBy = new ListPointer<Permanent>(new LinkedList<Permanent>());
	}

	public ListPointer<Permanent> getDamageAssignmentOrder() {
		return damageAssignmentOrder;
	}

	public void setAttackersICanBlock(LinkedList<Permanent> attackers) {
		this.attackersICanBlock = attackers;
	}
	
	public void setAttackersICanBlock(Collection<? extends Permanent> attackers) {
		this.attackersICanBlock = new LinkedList<Permanent>(attackers);
	}

	public void resetAttackersICanBlock() {
		this.attackersICanBlock = new LinkedList<Permanent>();
	}
	
	public LinkedList<Permanent> getAttackersICanBlock() {
		return attackersICanBlock;
	}
	
	public void setDamageAssignmentOrder(ListPointer<Permanent> damageAssignmentOrder) {
		this.damageAssignmentOrder = damageAssignmentOrder;
	}
	
	public ArrayList<LinkedList<Ability>> getAbilities()  {
		ArrayList<LinkedList<Ability>> all_abilities;
			if(this.attby_perm != null) {
				all_abilities = new ArrayList<LinkedList<Ability>>(super.abilities);
					for(Permanent p : this.attby_perm) {
						all_abilities.add(p.getAbilities().get(0));
					}
				return all_abilities;
			}
			else {
				return super.abilities;
			}
	}
	
	public boolean checkKeywordAbility(String keyword_text) {
		// Restituisce true se la faccia attiva presenta 
		// una keyword ability con testo uguale a quello dato
		// TODO: gestire faccia attiva
		return this.keywordAbilities.get(0)
				                    .stream()
				                    .anyMatch((Ability a) -> a.getKeyword_text().trim().equals(keyword_text));
	}
	
	@Override
	public void setIdController(int idController) {
		super.setIdController(idController);
		//TODO: sickness
	}
	public class Status
	{
		private boolean tapped;
		private boolean flipped;
		private boolean faceUp;
		private boolean phasedIn;
		
		public Status(boolean tapped, boolean flipped, boolean faceUp,
				boolean phasedIn) {
			super();
			this.tapped = tapped;
			this.flipped = flipped;
			this.faceUp = faceUp;
			this.phasedIn = phasedIn;
		}
		public boolean isTapped() {
			return tapped;
		}
		public void setTapped(boolean tapped) {
			this.tapped = tapped;
		}
		public boolean isFlipped() {
			return flipped;
		}
		public void setFlipped(boolean flipped) {
			this.flipped = flipped;
		}
		public boolean isFaceUp() {
			return faceUp;
		}
		public void setFaceUp(boolean faceUp) {
			this.faceUp = faceUp;
		}
		public boolean isPhasedIn() {
			return phasedIn;
		}
		public void setPhasedIn(boolean phasedIn) {
			this.phasedIn = phasedIn;
		}
	}
}