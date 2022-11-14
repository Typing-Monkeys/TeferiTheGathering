package com.magicengine;

public class JSONMessage {
	static int count = 0;
	int id;
	int remaining;
	int idPlayer; // 0 = Broadcast, 1...N = ID Player
	Object data;
	
	public JSONMessage(int idPlayer, Object data, int remaining) {
		super();
		this.id = count;
		this.idPlayer = idPlayer;
		this.data = data;
		this.remaining = remaining;
		
		if(remaining == 0)
			count++;
	}	
}
