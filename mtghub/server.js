/**************************************************
 ** NODE.JS REQUIREMENTS
 **************************************************/

var app = require("express")(),
    server = require("http").createServer(app),
    io = require("socket.io").listen(server),
    net = require("net"),
    JsonSocket = require("json-socket");

var Player = require("./Player").Player;
var Debugger = require("./Player").Debugger; //Player class

/**************************************************
 ** GLOBAL SETTINGS
 **************************************************/
var SERVER_PORT = 2012; //port number for html client
var JAVA_SERVER_PORT = 2013; //java port number
var JAVA_SERVER_ADDR = "127.0.0.1"; //java address
var PLAYERS_NUMBER = 2; //max number of players
var JSON_PLAYERS = { playersNumber: PLAYERS_NUMBER }; //JSON alerts java on players number, it sended when room is full
var JSON_START_GAME = { startGame: "true" };
var JSON_SAVE_GAME = { saveGame: "true" };
var JSON_DEBUG_ON = { debugOn: "true" };
var JSON_DEBUG_OFF = { debugOff: "true" };

/**************************************************
 ** GLOBAL VARIABLES
 **************************************************/
var numconn = 0; //number of player connected to Node.js
var socketGuest = null; //socket of guest player
var room = "magicRoom"; //room of game
var jSocket; //java socket
//maps
var room2jsocket = {}; //KEYS: room of game, VALUE: java socket
var socket2player = {}; //KEYS: html socket, VALUE: player object
//messages
var messages = {}; //save the incoming incomplete messages
var prevData = "";
var idDebugger = null;


/**
 * Questa funzione viene lanciata quando i server NodeJs o Java vengono
 * chiusi di cattiveria. Forzano la disconnessione di tutti i socket
 * io (quelli tra Node e ClientHTML) e JSocket (tra Node e Java).
 * @author Fabrizio Fagiolo, Nicolò Vescera
 * 
 * @param {string} msg messaggio da mostrare ai ClientHTML
 */
function forcedExit(msg="NodeJs server closed") {
    console.log("Closing all Sockets ...");

    for (const [socketRoom, socket] of Object.entries(room2jsocket)) {
        console.log(`Closing Sockets for Room ${socketRoom}`);

        console.log(`Closing JSocket`);
        socket.sendMessage({"kill": 0});

        console.log(`Closing ClientHTML connection`);
        io.sockets.in(socketRoom).emit("clientLeave", msg);
    }

    console.log("Done :D");
};


/**************************************************
 ** SERVER INITIALISATION
 **************************************************/
function init() {
    // Set up Socket.IO to listen on port SERVER_PORT
    server.listen(SERVER_PORT, () => {
        console.log("listening on: " + SERVER_PORT);
    });

    

    /**
     * Quando viene lanciata un'eccezione non gestita
     * viene forzata la chiusura di tutti i Socket (io e JSocket)
     * ed infine chiuso il server NodeJS
     *  
     * @author Fabrizio Fagiolo, Nicolò Vescera
     */
    process.on('uncaughtException', (err, origin) => {
        console.error(`Exception: ${err}`);
        console.error(`from origin: ${origin}`);

        forcedExit();
        process.exit(1);
      });

    /**
     * Quando NodeJs riceve SIGINT (CTRL+C) vengono chiusi
     * tutti i socket con Java per evitare che questo vada in Loop
     * 
     * @author Nicolò Vescera
     */
    process.on('SIGINT', function() {
        console.log("\nDetected SIGINT (CTRL+C) !!");
        forcedExit();
        process.exit(0);
      });
      

    // Start listening for events
    setServerEventHandlers();
}

/**************************************************
 ** SERVER EVENT HANDLERS
 **************************************************/
var setServerEventHandlers = function () {
    io.sockets.on("connection", onServerSocketConnection);
};

// New socket Server connection
function onServerSocketConnection(htmlClient) {
    //Listen for client choice
    htmlClient.on("choice", onChoice);

    // Listen for client disconnected
    htmlClient.on("disconnect", onClientDisconnect);

    // Listen for client Attempt message
    htmlClient.on("attempt", onAttempt);

    // Listen for new player message
    htmlClient.on("new player", onNewPlayer);

    htmlClient.on("new debugger", onNewDebugger);

    // Listen for client settings
    htmlClient.on("ready", onReady);

    //Listen for start game
    htmlClient.on("start game", onStartGame);

    //Listen for receive zone
    htmlClient.on("receive zone", onReceiveZone);

    //Listen for receive game
    htmlClient.on("receive game", onReceiveGame);

    //Listen for save game
    htmlClient.on("save game", onSaveGame);

    //Listen for the Debugger
    htmlClient.on("DebugSet", setPositionDebug);

    //Listen for the ModificatioOn
    htmlClient.on("DebugOn", onDebugOn);

    //Listen for the ModificatioOff
    htmlClient.on("DebugOff", onDebugOff);

    //Listen for the Modify into players
    htmlClient.on("DebugSetToPlayer", setPlayerDatas);
}


/**
 * Socket client has disconnected
 * Questa funzione gestisce la disconnessione di un Client HTML
 *  Se è un debugger a disconnettersi: 
 *      - elimina tutte le informazioni salvate su di lui
 *      - avvisa tutti quelli nella room che il debugger è uscito
 *      - lascia effettivamente la stanza
 * 
 *  Se è un player che sta abbandonando:
 *      - oltre a fare le stesse cose che fa il debug, avvisa java
 *          di terminare il GameEngine relativo alla partita e
 *          avvisa tutti gli altri client connessi di disconnettersi
 *  
 *  Se è un Guest che sta abbandonando:
 *      - Resetta a null la variabile socketGuest
 *      - Fa le setesse code del debugger
 * 
 * @author Fabrizio Fagiolo, Nicolò Vescera
 */
function onClientDisconnect() {
    numconn--;

    // se TRUE, un player si sta disconnettendo
    // se FALSe, il debugger si sta disconnettendo
    var isPlayerDisconnecting = true;

    // inizializzo il messaggio di avviso disconnessione
    var msg = `Player has disconnected ${this.id}`;

    console.debug(socket2player[this.id]);

    //send request "close stream" to java server
    var playerRoom = socket2player[this.id].room;
    var playerId = socket2player[this.id].id;
    jSocket = room2jsocket[playerRoom];

    // player disconnection
    this.leave(playerRoom);

    if (this.id === idDebugger) {
        isPlayerDisconnecting = false;      // indica che il debugger si disconnette
        idDebugger = null;                  // resetta l'idDebugger
        msg = "Debugger has disconnected";  // messagio da mandare ai client
    }

    // guest disconnecting
    if (socketGuest !== null && this.id == socketGuest.id) {
        isPlayerDisconnecting = false;      // indica che il guest si disconnette
        socketGuest = null
        msg = `Guest Player has disconnected ${this.id}`;
    }

    // emit the message
    console.log(msg);
    io.sockets.in(playerRoom).emit("messaggio", msg);

    if (isPlayerDisconnecting) {
        // avvisa tutti i client connessi alla stanza di abbandonare la partita
        io.sockets.in(playerRoom).emit("clientLeave", msg);
        jSocket.sendMessage({"exit": playerId });    // avvisa java di resettare il game
        //room += '1';                               // create new room
    }

    //delete player from maps
    delete socket2player[this.id];
}

// Client send Attemtp message
function onAttempt(data) {
    //Node.js assign id to client
    data.attempt.id = socket2player[this.id].id;

    //forward json message to java
    var playerRoom = socket2player[this.id].room;
    jSocket = room2jsocket[playerRoom];
    jSocket.sendMessage(data);
}

//onChoice
function onChoice(data) {
    console.log(data);
    var playerId = socket2player[this.id].id;
    data.choice.idPlayer = playerId;
    //send to java server the data JSON modified
    var playerRoom = socket2player[this.id].room;
    jSocket = room2jsocket[playerRoom];
    jSocket.sendMessage(data);
}

function onNewDebugger(numPlayer) {
    //Max num player parameter from CreateRoom.html

    this.join(room);

    numDeb = 0;

    var newDebugger = new Debugger(room, numDeb);
    //var roomSize = getSizeRoom(room.toString());
    socket2player[this.id] = newDebugger;
    idDebugger = this.id;

    console.log("debugger connected " + this.id);
    io.sockets.in(room).emit("messaggio", "Debugger join to the room...");
}

// New player has joined
/**
 * Funzione che gestisce la connessione di un nuovo Player.
 *  - Incrementa il numero di connessioni numconn
 *  - Crea i vari socket
 *  - Solo quando ci sono 2 player connessi crea la connessione con Java
 *  - Quando ci sono più di 2 player vengono settati come Guest (può essercene solo 1 credo)
 * 
 * @author Fabrizio Fagiolo, Nicolò Vescera
 * 
 * @param {*} numPlayer     Max num player parameter from CreateRoom.html
 */
function onNewPlayer(numPlayer) {
    numconn++;

    //console.debug(`numconn: ${numconn}`);
    //console.debug("numPlayer: ", typeof numPlayer, numPlayer);
    //console.debug(`PLAYERS_NUMBER: ${PLAYERS_NUMBER}`);
    //console.debug(`JSON_PLAYERS: ${JSON_PLAYERS}`);

    //SOLO SE è il PRIMO -- cioè se numConn == 1
    //if PNumber==2 non tocco nulla
    if (numconn == 1) {
        PLAYERS_NUMBER = numPlayer.num_player; //toReview
        JSON_PLAYERS = { playersNumber: PLAYERS_NUMBER };
    }

    /* if(numconn%PLAYERS_NUMBER >= JSON_PLAYERS) {
        room += '1';
    } */

    //ToDO: choice ROOM
    console.debug(room);
    this.join(room);

    //fill the maps
    var newPlayer = new Player(room, numconn);
    socket2player[this.id] = newPlayer;

    //ToDo: handle more room
    /* 
     * queste 2 riche non fanno molto,
     * la gunzione getSizeRoom ritorna il numero di ClientHTML connessi,
     * basta solo che se ne apre uno (senza compilare nessun campo o premere il botteno Join Room)
     * per aumentarne il valore.
     */
    var roomSize = getSizeRoom(room.toString());
    console.log(roomSize + " players in room");

    /* CHECK IF ROOM IS FULL --------------------------------------------------------------------------------------------------------*/
    if (numconn < PLAYERS_NUMBER) {
        console.log(numconn + " player connected of " + PLAYERS_NUMBER);
        io.sockets
            .in(room)
            .emit(
                "messaggio",
                "Join to the room...waiting for " +
                (PLAYERS_NUMBER - numconn) +
                " players..."
            );
    }

    if (numconn == PLAYERS_NUMBER) {
        //room is full
        console.log("last player connected, ready to play");
        this.emit("messaggio", "Join to the room...");
        io.sockets
            .in(room)
            .emit("messaggio", "Press SEND PLAYER DATA to send data!");
        //open java connection
        var javaSocket = connectToJava();
        javaSocket.sendMessage(JSON_PLAYERS);
        //fill the map
        room2jsocket[room] = javaSocket;
    }

    if (numconn > PLAYERS_NUMBER) {
        console.log("Guest connected " + this.id);

        //guest player
        socketGuest = this;
        
        // avvisa tutti gli altri partecipanti della connessione del guest
        io.sockets.in(room).emit("messaggio", "Guest Player join the room !");
    }
    /*END --------------------------------------------------------------------------------------------------------------------------------*/
}

//send player settings to java
function onReady(data) {
    io.sockets.in(room).emit("messaggio", this.id + " ready");
    //Node.js assign id to client
    console.log("id who pressed " + socket2player[this.id].id);
    data.playerSettings.playerInfo.id = socket2player[this.id].id;
    console.log("player " + this.id + " is ready to play");

    console.debug("Room: " + room);
    console.log(data);

    var playerRoom = socket2player[this.id].room;
    jSocket = room2jsocket[playerRoom];
    jSocket.sendMessage(data); // Notare che se viene invitato prima della connessione con il server JAVA crasha il Node
}

//send start game request to java
function onStartGame() {
    io.sockets.in(room).emit("messaggio", "Start game request sent...");
    var playerRoom = socket2player[this.id].room;
    jSocket = room2jsocket[playerRoom];
    jSocket.sendMessage(JSON_START_GAME);
}

//save game request to java
function onSaveGame() {
    io.sockets.in(room).emit("messaggio", "Save game request sent...");
    var playerRoom = socket2player[this.id].room;
    jSocket = room2jsocket[playerRoom];
    jSocket.sendMessage(JSON_SAVE_GAME);
}

//send zone request to java
function onReceiveZone(data) {
    var playerRoom = socket2player[this.id].room;
    jSocket = room2jsocket[playerRoom];
    data.receiveZone = socket2player[this.id].id;
    console.log("zone" + data);
    jSocket.sendMessage(data);
}

//send game request to java
function onReceiveGame(data) {
    //
    var playerRoom = socket2player[this.id].room;
    jSocket = room2jsocket[playerRoom];
    data.receiveGame = socket2player[this.id].id;
    jSocket.sendMessage(data);
}

/**************************************************
 ** CLIENT INITIALISATION
 **************************************************/
function connectToJava() {
    //Create new socket
    var javaSocket = new JsonSocket(new net.Socket());

    //Open connection with java
    javaSocket.connect(JAVA_SERVER_PORT, JAVA_SERVER_ADDR);

    //Start listening for events
    setClientEventHandlers(javaSocket);

    return javaSocket;
}

/**************************************************
 ** CLIENT EVENT HANDLERS
 **************************************************/
var setClientEventHandlers = function (javaSocket) {
    javaSocket.on("connect", onClientSocketConnection);

    //Listen for java message
    javaSocket.on("data", onData);

    //Listen for java disconnected
    javaSocket.on("close", onClose);
};

//Connection with java opened successfully
function onClientSocketConnection() {
    console.log("Connected to java Server");
}

//Received json message from java
function onData(data) {
    var i;
    data = prevData + data.toString();
    prevData = "";

    // If data contains multiple messages, elaborate them one by one
    data = data.split("\n");
    for (i = 0; i < data.length; i++) {
        // Take a single message
        var message = data[i];
        if (message.length > 1) {
            if (IsJsonString(message)) {
                // Parse the message
                message = JSON.parse(message);
                //console.log('Send to: ' + message.idPlayer);
                //console.log('MessageID: ' + message.id);
                //console.log('Remaining: ' + message.remaining);
                //console.log(message);
                //console.log('Data: ' + message.data);
                if (message.remaining <= 0) {
                    if (message.id in messages) {
                        /*
                                    if(message.idPlayer < 0)
                                    {
                                      messages[message.id] = messages[message.id] + message.data;
                                      sendToDebugger(messages[message.id]);
                                      console.log('Data Debug: ' + message.data);
                                      delete messages[message.id];
                                    }else{
                                    */
                        messages[message.id] = messages[message.id] + message.data;
                        sendToPlayer(message.idPlayer, messages[message.id]);
                        delete messages[message.id];
                    } else {
                        sendToPlayer(message.idPlayer, message.data);
                    }
                } else {
                    if (message.id in messages) {
                        messages[message.id] += message.data;
                    } else {
                        messages[message.id] = message.data;
                    }
                }
            } else {
                prevData = message;
            }
        }
    }

    //if socket guest is connected then send message
    //if(socketGuest){ socketGuest.emit('messaggio', data.toString());}
}

function IsJsonString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}

function sendToPlayer(idPlayer, data) {
    if (idPlayer > 0) {
        if (idPlayer == 999 && idDebugger != null) {
            io.sockets.connected[idDebugger].emit("messaggio", data);
            return;
        }

        for (var socketId in socket2player) {
            if (socket2player[socketId].id == idPlayer) {
                // Send message to a single client
                io.sockets.connected[socketId].emit("messaggio", data);
            }
        }
    } else {
        // Send broadcast message
        io.sockets.in(room).emit("messaggio", data);
    }
}

function onDebugOn() {
    jSocket.sendMessage(JSON_DEBUG_ON);
    io.sockets.in(room).emit("messaggio", "Debug On(attempt disabilitati)");
}

function onDebugOff() {
    jSocket.sendMessage(JSON_DEBUG_OFF);
    io.sockets.in(room).emit("messaggio", "Debug Off(attempt abilitati)");
}

function setPlayerDatas(data) {
    /*
      "idPlayer":idPleyer,
     "dataLTPlayer" : dataLTPlayer,
     "dataLMPlayer" : dataLMPlayer,
     "poisonCounter" : poisonCounter
      
      */
    var playerRoom = socket2player[this.id].room;
    jSocket = room2jsocket[playerRoom];

    var JSON_SET = {
        setDataPlayer: "true",
        idplayer: parseInt(data.idPlayer),
        LTPlayer: parseInt(data.dataLTPlayer),
        LMPlayer: data.dataLMPlayer,
        poisonCounter: parseInt(data.poisonCounter),
    };
    jSocket.sendMessage(JSON_SET);
}

function setPositionDebug(data) {
    /*
      * ["idPlayer":idPleyer,
         "idCard" : cardId,
         "from" : fromOpp,
         "numCard" : numCard,
         "toOpp" : toOpp
         "numPlace":numPlace};
      * ]
      */

    //set up config
    var playerRoom = socket2player[this.id].room;
    jSocket = room2jsocket[playerRoom];

    var oppList = {
        H: "Hand",
        L: "Library",
        G: "Graveyard",
        B: "Battlefield",
        E: "Exile",
        S: "Stack",
        A: "Ante",
        C: "commander",
    };

    var functVar = data.from + "_" + data.toOpp;
    console.log(functVar);
    switch (functVar) {
        case "H_L": // hand to library
            // ancora non c'è la variabile in input
            // var position = data.numCard;
            var position = parseInt(data.numPlace);
            var JSON_MOVE = {
                H_L: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;

        case "H_G": // hand to graveyard
            // ancora non c'è la variabile in input
            // var position = data.numCard;
            var position = parseInt(data.numPlace);
            var JSON_MOVE = {
                H_G: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "H_B": // hand to Battelfield
            var JSON_MOVE = {
                H_B: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "H_E": // hand to exile
            var JSON_MOVE = {
                H_E: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "H_S": // hand to stack
            var JSON_MOVE = {
                H_S: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "L_H": // library to hand
            var position = parseInt(data.numCard);
            var JSON_MOVE = {
                L_H: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "L_G": // library to graveyard
            var position = parseInt(data.numCard);
            var position1 = parseInt(data.numPlace);
            var JSON_MOVE = {
                L_G: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
                numPlace: position1,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "L_B": // library to battlefield
            var position = parseInt(data.numCard);
            var JSON_MOVE = {
                L_B: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "L_E": // library to Exile
            var position = parseInt(data.numCard);
            var JSON_MOVE = {
                L_E: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "L_S": // library to stack
            var position = parseInt(data.numCard);
            var JSON_MOVE = {
                L_S: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "G_H": // graveyard to hand
            var position = parseInt(data.numCard);
            var JSON_MOVE = {
                G_H: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "G_L": // graveyard to library
            var position = parseInt(data.numCard);
            var position1 = parseInt(data.numPlace);
            var JSON_MOVE = {
                G_L: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
                numPlace: position1,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "G_B": // graveyard to battlefield
            var position = parseInt(data.numCard);
            var JSON_MOVE = {
                G_B: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "G_E": // graveyard to exile
            var position = parseInt(data.numCard);
            var JSON_MOVE = {
                G_E: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "G_S": // graveyard to stack
            var position = parseInt(data.numCard);
            var JSON_MOVE = {
                G_S: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "B_H": // battlefield to hand
            var position = parseInt(data.numCard);
            var JSON_MOVE = {
                B_H: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "B_L": // battlefield to library
            var position = parseInt(data.numPlace);
            var JSON_MOVE = {
                B_L: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "B_G": // battlefield to graveyard
            var position = parseInt(data.numPlace);
            var JSON_MOVE = {
                B_G: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "B_E": // battlefield to exile
            //var position = parseInt(data.numCard);
            var JSON_MOVE = {
                B_E: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "B_S": // battlefiled to stack
            //var position = parseInt(data.numCard);
            var JSON_MOVE = {
                B_S: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "E_H": // exile to hand
            //var position = parseInt(data.numCard);
            var JSON_MOVE = {
                E_H: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "E_L": // exile to library
            var position = parseInt(data.numPlace);
            var JSON_MOVE = {
                E_L: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "E_G": // exile to graveyard
            var position = parseInt(data.numPlace);
            var JSON_MOVE = {
                E_G: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "E_B": // exile to battlefield
            //var position = parseInt(data.numCard);
            var JSON_MOVE = {
                E_B: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "E_S": // exile to stack
            //var position = parseInt(data.numCard);
            var JSON_MOVE = {
                E_S: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "S_H": // stack to hand
            var JSON_MOVE = {
                S_H: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "S_L": // stack to library
            var position = parseInt(data.numPlace);
            var JSON_MOVE = {
                S_L: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "S_B": // stack to battlefield
            var JSON_MOVE = {
                S_B: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "S_G": // stack to graveyard
            var position = parseInt(data.numPlace);
            var JSON_MOVE = {
                S_G: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
                numCard: position,
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;
        case "S_E": // stack to exile
            var JSON_MOVE = {
                S_E: "true",
                idplayer: parseInt(data.idPlayer),
                idCard: parseInt(data.idCard),
            };
            console.log("i'm in");
            jSocket.sendMessage(JSON_MOVE);
            break;

        default:
            return;
    }
}

//da fare
function sendToDebugger(data) {
    console.log(
        "\n\n ************************************************** pre emit al debugger \n\n" +
        idDebugger +
        "\n\n *********************************************\n\n"
    );
    // Send message to debugger
    io.sockets.connected[idDebugger].emit("messaggio", data);
}

function onClose() {
    // Dovrebbe chiudere il socket o roba del genere ??
    console.log("Connection from java closed");

    /**
     * Quando Java viene chiuso o crasha (?)
     * vengono chiusi tutti i Socket (io e JSocket) e 
     * viene inviato il messaggio ai ClientHTML
     * 
     * @author Fabrizio Fagiolo, Nicolò Vescera
     */
    forcedExit(msg="Java server closed");
}

/**************************************************
 ** HELP FUNCTIONS
 **
 ** questa funzione ritorna il numero di Client aperti !!
 ** Basta solo aprire un client e non fare nulla per
 ** aumentare la size della Room !!
 **************************************************/
function getSizeRoom(roomName) {
    var num = io.nsps["/"].adapter.rooms[roomName];
    //console.debug(`NUMM: ${num}`);

    return Object.keys(num).length;
}

/**************************************************
 ** RUN THE GAME
 **************************************************/
init();
