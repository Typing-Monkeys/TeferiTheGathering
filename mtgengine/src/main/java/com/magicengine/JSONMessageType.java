package com.magicengine;

public class JSONMessageType {
	int type; // 1 = write to textarea, 2 = add Make Choice, 3 = update zones
	int subtype; // 1 = choice singola
	Object data;
	
	public JSONMessageType(int type, Object data) {
		super();
		this.type = type;
		this.subtype = 0;
		this.data = data;
	}

	public JSONMessageType(int type, int subtype, Object data) {
		super();
		this.type = type;
		this.subtype = subtype;
		this.data = data;
	}
}
