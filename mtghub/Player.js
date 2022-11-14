/**************************************************
** GAME PLAYER CLASS
**************************************************/
var Player = function(gameRoom,PlayerId){
	var room = gameRoom,
		id = PlayerId,
		javaSocket;

	/* Getters and setters
	var getRoom = function() {
		return room;
	};

	var getId = function() {
		return id;
	};

	var setRoom = function(newRoom){
		room = newRoom
	};

	var setPlayerId = function(newPlayerId){
		id = newPlayerId;
	};*/

	
	// Define which variables and methods can be accessed
	return {
		room: room,
		id: id,
		javaSocket: javaSocket 
	}
};

// Export the Player class so you can use it in
// other files by using require("Player").Player
exports.Player = Player;



var Debugger = function(gameRoom,DebuggerId){
	var room = gameRoom,
		id = DebuggerId,
		javaSocket;

	return {
		room: room,
		id: id,
		javaSocket: javaSocket 
	}
};

exports.Debugger = Debugger;