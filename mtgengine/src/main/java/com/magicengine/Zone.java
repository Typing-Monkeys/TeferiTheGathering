package com.magicengine;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Zone extends LinkedList<MagicObject> implements Target {
	
	private boolean public_zone; //hidden or public
	//private boolean shared; non c'� bisogno perch� le shared vanno su Game e le private su Player
	private String nameZone;
	private int magicTargetId=-1;
	
	public Zone(String nameZone, boolean isPublic) {
		super();
		this.public_zone = isPublic;
		this.nameZone = nameZone;
	}
	
	public Zone(String nameZone, boolean isPublic,int magicTargetId) {
		super();
		this.public_zone = isPublic;
		this.nameZone = nameZone;
		this.magicTargetId=magicTargetId;
	}
	
	public boolean remove(String magicTargetId) {
		int magicTargetIdint = Integer.valueOf(magicTargetId);
		
		for (MagicObject card : this) {
			if (card.getMagicTargetId() == magicTargetIdint) {
				return remove(card);
			}
		}

		return false;
	}

	public int getMagicTargetId() {
		return magicTargetId;
	}


	public void setMagicTargetId(int magicTargetId) {
		this.magicTargetId = magicTargetId;
	}


	public boolean getPublic_zone() {
		return public_zone;
	}


	public void setPublic_zone(boolean public_zone) {
		this.public_zone = public_zone;
	}

	public String getNameZone() {
		return nameZone;
	}


	public void setNameZone(String nameZone) {
		this.nameZone = nameZone;
	}
	
	public void shuffle()
	{
		Collections.shuffle(this);
	}	
}
