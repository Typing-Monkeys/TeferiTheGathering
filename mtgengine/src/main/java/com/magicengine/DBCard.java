package com.magicengine;

import java.util.LinkedList;

import com.magicengine.DBCard.Face;

public class DBCard {

	private int cardID; 
	private int multiverseid;
	private String cardName;
	private String alternateCardName;
	private boolean doublefacedCard;
	private boolean splitCard;
	private boolean flipCard;
	private Face face1;
	private Face face2;
	private LinkedList<Face> facce = null;
	
	public DBCard(int cardID, int multiverseid, String cardName, String alternateCardName,
			boolean doublefacedCard, boolean splitCard, boolean flipCard,
			Face face1, Face face2) {
		super();
		this.cardID = cardID;
		this.cardName = cardName;
		this.alternateCardName = alternateCardName;
		this.doublefacedCard = doublefacedCard;
		this.splitCard = splitCard;
		this.flipCard = flipCard;
		this.multiverseid = multiverseid;
		this.face1 = face1;
		this.face2 = face2;
		this.face1.facciaI=0;
		this.face2.facciaI=1;

		//this.facce.add(face1);
		//this.facce.add(face2);
	}

	public LinkedList<Face> faces(){
		if(this.facce==null) {
			this.facce = new LinkedList<Face>();
			this.facce.add(face1);
			this.facce.add(face2);
		}
		return this.facce;
	}
	public int getcardID() {
		return cardID;
	}

	public void setCardID(int cardID) {
		this.cardID = cardID;
	}


	public String getCardName() {
		return cardName;
	}


	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getAlternateCardName() {
		return alternateCardName;
	}

	public void setAlternateCardName(String alternateCardName) {
		this.alternateCardName = alternateCardName;
	}

	public boolean isDoublefacedCard() {
		return doublefacedCard;
	}

	public void setDoublefacedCard(boolean doublefacedCard) {
		this.doublefacedCard = doublefacedCard;
	}

	public boolean isSplitCard() {
		return splitCard;
	}

	public void setSplitCard(boolean splitCard) {
		this.splitCard = splitCard;
	}

	public boolean isFlipCard() {
		return flipCard;
	}

	public void setFlipCard(boolean flipCard) {
		this.flipCard = flipCard;
	}

	public Face getFace1() {
		return face1;
	}

	public void setFace1(Face face1) {
		this.face1 = face1;
	}

	public Face getFace2() {
		return face2;
	}

	public void setFace2(Face face2) {
		this.face2 = face2;
	}

	public int getMultiverseid() {
		return multiverseid;
	}

	public void setMultiverseid(int multiverseid) {
		this.multiverseid = multiverseid;
	}

	public class Face
	{
		private int facciaI;
		private String faceName;
		private boolean levelerCard;
		private String power;
		private String toughness;
		private String loyalty;
		private String manaCost;
		private String multiverseid;
		private LinkedList<String> types;
		private LinkedList<String> supertypes;
		private LinkedList<String> subtypes;
		private LinkedList<String> colorIndicator;
		private LinkedList<String> abilities;
		private LinkedList<String> keywordAbilities;
		
		public Face(String faceName, boolean levelerCard, String power,
				String toughness, String loyalty, String manaCost,
				LinkedList<String> types, LinkedList<String> supertypes,
				LinkedList<String> subtypes, LinkedList<String> colorIndicator,
				LinkedList<String> abilities,
				LinkedList<String> keywordAbilities,int faceid) {
			super();
			this.faceName = faceName;
			this.levelerCard = levelerCard;
			this.power = power;
			this.toughness = toughness;
			this.loyalty = loyalty;
			this.manaCost = manaCost;
			this.types = types;
			this.supertypes = supertypes;
			this.subtypes = subtypes;
			this.colorIndicator = colorIndicator;
			this.abilities = abilities;
			this.keywordAbilities = keywordAbilities;
		}
		
		public int getFaceID() {
			return facciaI;
		}		
		public void setFaceID(int i) {
			this.facciaI=i;
		}

		
		public String getFaceName() {
			return faceName;
		}


		public void setFaceName(String faceName) {
			this.faceName = faceName;
		}


		public boolean isLevelerCard() {
			return levelerCard;
		}


		public void setLevelerCard(boolean levelerCard) {
			this.levelerCard = levelerCard;
		}


		public String getPower() {
			return power;
		}


		public void setPower(String power) {
			this.power = power;
		}


		public String getToughness() {
			return toughness;
		}


		public void setToughness(String toughness) {
			this.toughness = toughness;
		}


		public String getLoyalty() {
			return loyalty;
		}


		public void setLoyalty(String loyalty) {
			this.loyalty = loyalty;
		}


		public String getManaCost() {
			return manaCost;
		}


		public void setManaCost(String manaCost) {
			this.manaCost = manaCost;
		}

		public String getMultiverseid() {
			return multiverseid;
		}

		public void setMultiverseid(String multiverseid) {
			this.multiverseid = multiverseid;
		}

		public LinkedList<String> getTypes() {
			return types;
		}


		public void setTypes(LinkedList<String> types) {
			this.types = types;
		}


		public LinkedList<String> getSupertypes() {
			return supertypes;
		}


		public void setSupertypes(LinkedList<String> supertypes) {
			this.supertypes = supertypes;
		}


		public LinkedList<String> getSubtypes() {
			return subtypes;
		}


		public void setSubtypes(LinkedList<String> subtypes) {
			this.subtypes = subtypes;
		}


		public LinkedList<String> getColorIndicator() {
			return colorIndicator;
		}


		public void setColorIndicator(LinkedList<String> colorIndicator) {
			this.colorIndicator = colorIndicator;
		}


		public LinkedList<String> getAbilities() {
			return abilities;
		}


		public void setAbilities(LinkedList<String> abilities) {
			this.abilities = abilities;
		}


		public LinkedList<String> getKeywordAbilities() {
			return keywordAbilities;
		}


		public void setKeywordAbilities(LinkedList<String> keywordAbilities) {
			this.keywordAbilities = keywordAbilities;
		}
		


		private class LevelerAbility
		{
			private String N1;
			private String N2;
			private LinkedList<String> abilities;
			private String power;
			private String toughness;
			public LevelerAbility(String n1, String n2,
					LinkedList<String> abilities, String power, String toughness) {
				super();
				N1 = n1;
				N2 = n2;
				this.abilities = abilities;
				this.power = power;
				this.toughness = toughness;
			}
			public String getN1() {
				return N1;
			}
			public void setN1(String n1) {
				N1 = n1;
			}
			public String getN2() {
				return N2;
			}
			public void setN2(String n2) {
				N2 = n2;
			}
			public LinkedList<String> getAbilities() {
				return abilities;
			}
			public void setAbilities(LinkedList<String> abilities) {
				this.abilities = abilities;
			}
			public String getPower() {
				return power;
			}
			public void setPower(String power) {
				this.power = power;
			}
			public String getToughness() {
				return toughness;
			}
			public void setToughness(String toughness) {
				this.toughness = toughness;
			}
		}
	} 
}
