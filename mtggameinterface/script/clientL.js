/**************************************************
** GLOBAL VARIABLES
**************************************************/

var arrayimage = {}; // array globale delle immagini

var pplayer, aplayer, variant, language, i, p_team, gametype, handCards = [],
    id_name = [];
var playerColored = false;
/*AREAS*/
var hand, main_player, stackArea, anteArea, commandArea, exileArea, battlefieldArea, playerGraveyArea, playerLibArea;

/*CACHE*/
var cache_cardBlobs = cache_cardPIDs = [];

var generics = ["p2", "p3", "p4", "p5"],
    player_genID = []; //FIXME per player_genID

function showPannel() {
    var x = document.getElementById("nickname").value;

    if (x != null) {
        //cambiare per multiplayer
        document.getElementById("2p_var_opt").style.display = "none";
        document.getElementById("multi_var_opt").style.display = "none";
        document.getElementById("aside_buttons").style.display = "block";
    }
    if (x == "") {
        document.getElementById("2p_var_opt").style.display = "none";
        document.getElementById("multi_var_opt").style.display = "none";
        document.getElementById("aside_buttons").style.display = "none";
    }
}

function zoom_in() {

    var scale = 'scale(1.4,1.4)';
    document.getElementById("battlefield_area").style.transform = scale;

}

function zoom_out() {

    var scale = 'scale(1,1)';
    document.getElementById("battlefield_area").style.transform = scale;
}


function makeChoice(choiceId, is_multiple) {
    var selected_choices = $("#sel_choice" + choiceId + " #choices" + choiceId + " option:selected").map(function() {
        return this.value
    }).get().join("\", \"");
    //Per poter selezionare valori multipli .val() necessita che nell'HTML (sulla select) sia specificato "multiple"
    //ORIGINAL var choiceJSON = '{"choice":{"idChoice":choiceId, "idOption":$("#sel_choice"+choiceId+" #choices"+choiceId+" option:selected").val(), "idPlayer":''}}';
    var choiceJSON = "";
    if (is_multiple === "m") { //if .val() has read multiple values
        //console.log('{"choice":{"idChoice":'+choiceId+', "idOptions":["'+selected_choices+'"], "idPlayer":""}}'); //Choice DEBUG
        choiceJSON = '{"choice":{"idChoice":' + choiceId + ', "idOptions":["' + selected_choices + '"], "idPlayer":""}}';
    }
    if (is_multiple === "s") {
        //var selected_choices = $("#sel_choice"+choiceId+" #choices"+choiceId+" option:selected").val() || []; //LEGACY SNIPPET, only for single choice
        choiceJSON = '{"choice":{"idChoice":' + choiceId + ', "idOptions":[' + selected_choices + '], "idPlayer":""}}';
    }

    socket.emit('choice', JSON.parse(choiceJSON));
    $('#sel_choice' + choiceId).remove();
}

//Funzioni drag&drop
var currentCardPlayed = "";

function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
    ev.preventDefault();
	var card = document.getElementById(ev.dataTransfer.getData("text"));
	var type = card.getAttribute("type");
	var opt;
    var opt2 = document.getElementById("option2").value;
	
	if (type === "land") {
		opt = 6;
		currentCardPlayed = "land";
	}else if (type === "instant") {
		opt = 2;
		currentCardPlayed = "instant";
	}else {
		//Non instant spell
		opt = 3;
		currentCardPlayed = "non_instant";
	}
	
    var attempt = {
        "attempt": {
            "id": "",
            "command": TAKE_ACTION,
            "option": opt,
            "option2": opt2
        }
    };
    socket.emit('attempt', attempt);
}
//Fine funzioni Drag&Drop


window.onload = function() {

    //TEMP
    $('#fake_load').click(function() {
        $('.create_room').hide();
        $('.in_room').show();
        $(this).hide();
    });
    //END TEMP


    var option_button = document.getElementById("option_button");
    var option2_button = document.getElementById("option2_button");
    var option = document.getElementById("option");
    var option2 = document.getElementById("option2");
    var command = document.getElementById("command");
    var connect = document.getElementById("connect");
    var disconnect = document.getElementById("disconnect");
    var send_attempt = document.getElementById("send_attempt");
    var priority_button = document.getElementById("priority_button");
    //var send_data = document.getElementById("send_data"); //??
    var start_game = document.getElementById("start_game");
    //var receive_zone = document.getElementById("receive_zone"); //deprecated
    var nick = document.getElementById("nickname").value;
    var ctrlrDIV;
    //_INITIALIZATION_
    $('#conf_mp, #conf_2p, #multi_var_opt, #disconnect, #sel_choice').hide();
    $("#gt_select, #languages").buttonset();
    $('#nomePlayer').html("Create Room");
    $('.in_room').hide();

// - DECK 0 DECLARATION ----------------------------------------------------------------------------- 

    var deck0_creatures_only = Array();
    deck0_creatures_only[0] = 1065; // Island
    deck0_creatures_only[1] = 1066; // Swamp
    deck0_creatures_only[2] = 1064; // Plains
    deck0_creatures_only[3] = 1068; // Forest
    deck0_creatures_only[4] = 1067; // Mountain
    deck0_creatures_only[5] = 2191; // Cao Ren, Wei Commander (Creature, Horsemanship, Black)   
    deck0_creatures_only[6] = 2882; // Marsh Threader (Creature, Landwalk, Swampwalk, White)   
    deck0_creatures_only[7] = 911; // Crow of Dark Tidings (Creature, Flying, Black)
    deck0_creatures_only[8] = 1673; // Commander Greven il-Vec (Creature, Fear, Black)
    deck0_creatures_only[9] = 880; // Forgotten creation (Creature, Skulk, Blue)
    deck0_creatures_only[10] = 1678; // Dauthi Horror (Creature, Shadow, Black)
    deck0_creatures_only[11] = 2192; // Barbarian General (Creature, Horsemanship, Red)
    deck0_creatures_only[12] = 2194; // Boggart Brute (Creature, Menace, Red)
    deck0_creatures_only[13] = 1459; // Welkin Tern (Creature, Blue, Flying)
    deck0_creatures_only[14] = 2197; // Benthic Behemoth (Creature, Islandwalk, Blue)
    deck0_creatures_only[15] = 1710; // Canyion Wildcat (Creature, Mountainwalk, Red)
    deck0_creatures_only[16] = 2881; // Bladetusk Boar (Creature, Red, Intimidate)
    deck0_creatures_only[17] = 2886; // Immerwolf (Creature, Intimidate, Green)
    deck0_creatures_only[18] = 1460; // Bala Ged Thief (Creature, Black)
    deck0_creatures_only[19] = 2199; // Zodiac Rooster (Creature, Plainswalk, Green)  
    deck0_creatures_only[20] = 2198; // Bayou Dragonfly (Creature, Flying/Swampwalk, Green)
    deck0_creatures_only[21] = 2196; // Heartwood Treefolk (Creature, Forestwalk, Green)
    deck0_creatures_only[22] = 2885; // Archangel Avacyn (Creature, Flying, White)
    deck0_creatures_only[23] = 2887; // Armory Automaton (Creature artifact)
    deck0_creatures_only[24] = 2884; // Elite Cat Warrior (Creature, Forestwalk, Green)
    deck0_creatures_only[25] = 1511; // Frontier Guide (Creature, Green)
    deck0_creatures_only[26] = 1201; // Llanowar Elves (Creature, Green)




// - DECK 1 DECLARATION ----------------------------------------------------------------------------- 

    var deck1= Array(); // Red/Black
    var i=0;
    for(i; i<=9;i++)
        deck1.push(1067); //Mountain
    for(i; i<=19;i++)
        deck1.push(1066); //Swamp
    for(i; i<=23;i++)
        deck1.push(911); //Flying (Black)
    for(i; i<=27;i++)
        deck1.push(2881); //Intimidate (Red)
    for(i; i<=31;i++)
        //deck1.push(2883); //Landwalk (Red)
        deck1.push(1710); //Landwalk (Red)
    for(i; i<=35;i++)
        deck1.push(1678); //Shadow (Black)
    for(i; i<=39;i++) 
        deck1.push(2192); //Horsemanship (Red)
    for(i; i<=43;i++)
        deck1.push(2191); //Horsemanship (Black)
    for(i; i<=47;i++)
        deck1.push(1673); //Fear (Black)
    for(i; i<=51;i++)
        deck1.push(2194); //Menace (Red)
    for(i; i<=55;i++)
        //deck1.push(2180); //Enchant Player (Black)
        deck1.push(1460); //Bela (Normal Creature)
    for(i; i<=59;i++)
        deck1.push(2159); //Enchant Equipment (Black)


// - DECK 2 DECLARATION ----------------------------------------------------------------------------- 
    var deck2= Array(); // White/Blu
    i=0;
    for(i; i<=9;i++)
        deck2.push(1064); //Plains
    for(i; i<=19;i++)
        deck2.push(1065); //Island
    for(i; i<=23;i++)
        deck2.push(1459); //Flying (Blu)
    for(i; i<=27;i++)
        deck2.push(2162); //Flying (White) Enchant
    for(i; i<=31;i++)
        deck2.push(2197); //Landwalk (Blu)
    for(i; i<=35;i++)
        deck2.push(2882); //Landwalk (White)
    for(i; i<=39;i++) 
        deck2.push(880); //Skulk (Blu)
    for(i; i<=43;i++)
        //deck2.push(2169); //Aura Blu
        deck2.push(2885); //Archangel Avacyn Flying
    for(i; i<=47;i++)
        //deck2.push(2176); //Enchant Aura
        deck2.push(2887); //artifact creature Armory Automaton
    for(i; i<=51;i++)
        deck2.push(2186); //Flying (Blu) Enchant
    for(i; i<=55;i++)
        deck2.push(1169); //Flying(Blu) Enchant
    for(i; i<=59;i++)
        deck2.push(2886); //Immerwolf Intimidate

    // - DECK 3 DECLARATION ----------------------------------------------------------------------------- 
    var deck3= Array(); // green/
    i=0;
    for(i; i<=9;i++)
        deck3.push(1068); //Forest
    for(i; i<=15;i++)
        deck3.push(2886); //Intimidate Immerwolf (creature green)
    for(i; i<=20;i++)
        deck3.push(2196); //Forestwalk Heartwood Treefolk (creature green)
    for(i; i<=25;i++)
        deck3.push(2198); //Flying Bayou Dragonfly (creature green)
    for(i; i<=29;i++)
        deck3.push(2199); //Zodiac roster (creature green)
    for(i; i<=34;i++)
        deck3.push(2884); //Forestwalk Elite Cat Warrior (creature green)
    for(i; i<=37;i++)
        deck3.push(1511); //Frontier guide (creature green)
    for(i; i<=40;i++) 
        deck3.push(1201); //Llanowar elves (creature green)
    for(i; i<=43;i++)
        deck3.push(2170); //Enchant Entangling Vines (green)
    for(i; i<=47;i++)
        deck3.push(2189); //Enchant genju of the cedars (green)
    for(i; i<=49;i++)
        deck3.push(2195); //Enchant Elemental Resonance (green)
    for(i; i<=51;i++)
        deck3.push(2166); //Enchant Decomposition (green)
    for(i; i<=54;i++)
        deck3.push(2178); //Enchant Wurmweaver Coil (green)
    for(i; i<=57;i++)
        deck3.push(2190); //Enchant Genju of the realm (green)
    for(i; i<=59;i++)
        deck3.push(2161); //Enchant Armor of thorns (green)


    var sideboard1 = Array();
    for (var i = 0; i < 7; i++) {
        sideboard1[i] = 1083;

    }

    var sideboard2 = Array();
    for (var i = 0; i < 7; i++) {
        sideboard2[i] = 1083;
    }

    /**************************************************
     ** BUTTON FUNCTIONS
     **************************************************/

    //BUTTON JOIN ROOM
    connect.onclick = function() {
        socket = io.connect(IND_HTML_TO_JS); //GLOBAL

        $(connect).prop("disabled", true);
        $('#disconnect').show();

        var nplayer = $('#nop').val();
        var gameMode = $('#gt_select :radio:checked').val();
        if (gameMode == 0 && nplayer >= 3) {
            socket.emit('new player', {
                "num_player": nplayer
            });
            console.info("asking for a " + nplayer + " players room");
        } else if (gameMode == 1) {
            console.info("asking for a 2 players room")
            socket.emit('new player', {
                "num_player": 2
            });
        } else {
            alert("Wrong number of players, setting up a match of 3");
            console.info("asking for a 3 players room")
            socket.emit('new player', {
                "num_player": 3
            });
        }
        gametype = gameMode;

        $('#conf_2p').show();
        $('#conf_mp').hide();

        //EVENT: PRINT ON HTML CLIENT
        socket.on('messaggio', function(data) {
            onMessage(data);
        });

        /**
         * Quando il server invia questo messaggio ai client
         * vuol dire che un client ha abbandonato o il server NodeJs
         * è stato chiuso. Il client deve tornare allo stato iniziale e
         * mostra a video il messaggio ricevuto.
         * 
         * @author Fabrizio Fagiolo, Nicolò Vescera
         */
        socket.on('disconnect', function(data) {
            disconnectFunction();

            //console.log(data);
            alert(data);
           
            // ricarica la pagine per resettare lo stato iniziale del client
            window.location.reload();
        });
    };

    //BUTTON START GAME
    start_game.onclick = function() {
        socket.emit('start game');
    }

    /* DEPRECATED
    //BUTTON RECEIVE ZONE
    receive_zone.onclick = function(){
        var receiveZone = {"receiveZone":""};
        socket.emit('receive zone', receiveZone);   //will ask for TYPE3
    }
    */
    //BUTTON SEND_ATTEMPT
    send_attempt.onclick = function() {
        var opt = document.getElementById("option").value;
        var opt2 = document.getElementById("option2").value;
        //var com=document.getElementById("command").value;
        var attempt = {
            "attempt": {
                "id": "",
                "command": TAKE_ACTION,
                "option": opt,
                "option2": opt2
            }
        };
        socket.emit('attempt', attempt);
    };


    priority_button.onclick = function() {
        var opt = 1;
        var opt2 = document.getElementById("option2").value;
        //var com=document.getElementById("command").value;
        var attempt = {
            "attempt": {
                "id": "",
                "command": TAKE_ACTION,
                "option": opt,
                "option2": opt2
            }
        };
        socket.emit('attempt', attempt);
    };

    //BUTTON DISCONNECT
    /**
     * Questa funzione fa disconnettere il client e ritorna allo stato iniziale
     * Viene avviata quando:
     *  - il bottone Disconnect viene premuto
     *  - il server invia un messaggio di disconnect al client
     * 
     * @author Fabrizio Fagiolo, Nicolò Vescera
     */
    function disconnectFunction() {
        document.getElementById("message").textContent = "disconnected";
        console.log("DISCONNECTED");
        socket.disconnect();
		document.getElementById('nomePlayer').classList.remove("inGamePlayerName");
        $('.create_room').show();
        $('.in_room').hide();
    }
    disconnect.onclick = disconnectFunction;

    /**************************************************
     **               OTHER FUNCTIONS                 **
     **************************************************/

    function onMessage(message) {
        var ind, choiceId;
        if (IsJsonString(message)) {
            message = JSON.parse(message);
            switch (message.type) {
                case TEXT_MESSAGE:
                    switch (message.subtype) {
                        case 0:
                            //console.log("TYPE1"); //DEBUG
                            document.getElementById("message").value += (JSON.stringify(message.data) + "\n");
                            document.getElementById("message").scrollTop = document.getElementById("message").scrollHeight;
                            break;
                        case 1:
                            type1sub1(message);
                            break;
                    }
                    break;
                case CHOICE: // Player must take a choice o multiple choice
                    //console.log("TYPE2");
                    //console.log(JSON.stringify(message)); //DEBUG
                    type2subX(message, message.subtype);
                    break;
                case GAME_STATUS: // Update game interface
                    //FUTURE TODO - snippet subtype, questo ok per lo 0
                    //console.log("TYPE3");
                    //console.log(JSON.stringify(message)); //De-comment for debug purpose, very verbose!
                    switch (message.subtype) {
                        case 0:
                            type3sub0(message);
                            break;
                            //case 1:
                    }
                    break;
                default:
                    console.log(JSON.parse(message.data));
                    break;
            }
        } else {
            document.getElementById("message").value += (message + "\n");
        }
    }

    // Manage interface pre-game (room choice)
    function type1sub1(message) {
        //console.log(message);   //DEBUG
        switch (message.data) {
            case "readySPD":
                $('#send_p_data').show();
                $('#connect').hide();
                break;
            case "readyStart":
                $('#start_game').show();
                $('.create_room').hide();
                $('.in_room').show();
				document.getElementById('nomePlayer').classList.add("inGamePlayerName");
                break;
            case "gameStarted":
                $('#start_game').hide();
				$('.background').hide();
                break;
        }
    }

    // Create choice and multiple choice with <select> element
    function type2subX(message, subtype) {
        var choices = JSON.parse(message.data);

        console.log('scelte: ');
        console.log(choices);
        console.log('##########');

        choiceId = choices.idChoice;
		
		/*if(currentCardPlayed === "land" && choiceId == 4)
		{
			makeChoice(4, "s");
		}*/
		
        $('#choices_area').append('<div id="sel_choice' + choiceId + '" style="max-width:620px;"><label></label><select id="choices' + choiceId + '" ' + ((subtype == 2) ? "multiple" : "") + ' style="max-width:620px;"></select><input id="choose' + choiceId + '" type="button" value="Choose" onclick="makeChoice(' + choiceId + ', ' + (subtype == 2 ? "\'m\'" : "\'s\'") + ');">');
        $('#sel_choice' + choiceId + ' label').empty().text(choices.choiceText); //.html('')
        //$('#sel_choice'+choiceId+' #choices'+choiceId).html(''); //inutile
        for (ind = 0; ind < choices.choiceOptions.length - 1; ind++) {
            $('#sel_choice' + choiceId + ' #choices' + choiceId).append('<option value="' + choices.choiceOptions[ind].idOption + '">' + choices.choiceOptions[ind].optionText + '</option>');
        }

        $('#sel_choice' + choiceId + ' #choices' + choiceId).append('<option value="' + choices.choiceOptions[ind].idOption + '" selected>' + choices.choiceOptions[ind].optionText + '</option>');
		
        $('#choice_area').show();
    }

    // Update interface
    function type3sub0(message) {
        //console.log("TYPE 3 - 0");
        //console.log(message.data);   //DEBUG
        var data_game_status = JSON.parse(message.data);
        var game_status = data_game_status.privateGame;
        main_player = game_status.player;
        //console.log(main_player);
        //console.log("DAMIANO MARTINA DEBUG GAME:");
        //console.log(data_game_status);
        stackArea = game_status.stack;
        anteArea = game_status.ante;
        commandArea = game_status.command;
        exileArea = game_status.exile;
        battlefieldArea = game_status.battlefield;
        playerLibArea = main_player.library;
        playerGraveyArea = main_player.graveyard;

        //              $('#battlefield').append('');//!important MUST BE append - otw it will erase the combatzone
        //              $('#combat_zone').text(''); //potrebbe essere utile generare la combat_zone al bisogno così da evitare il problema sopra

        $('.content').empty(); //empty to refresh all -- must set more .content areas

        for (var oppId = 0; oppId < main_player.opponentsList.length; oppId) {
            $('#op-player' + ++oppId).show();
            populateOpponent(oppId);
        }

        populateStack();
        populateAnte();
        populateCommand();
        populateExile();
        populateBattlefield();
        populatePlayer();
        checkAttach();
        checkAttach();

        if (game_status.priorityPlayer != -1) {
            if (game_status.priorityPlayer == main_player.id) {
                $('#priorita-si-no-my1').html("P");
                $('#priorita-si-no-op1').html("");
				$('#priority_button').addClass("box-shadow");
            } else {
                $('#priorita-si-no-my1').html("");
                $('#priorita-si-no-op1').html("P");
				$('#priority_button').removeClass("box-shadow");
            }
        } else {
            $('#priorita-si-no-my1').html("X");
        }

        if (game_status.activePlayer != -1) {
            if (game_status.activePlayer == main_player.id) {
                $('#active-si-no-my1').html("A");
                $('#active-si-no-op1').html("");
            } else {
                $('#active-si-no-my1').html("");
                $('#active-si-no-op1').html("A");
            }
        } else {
            $('#active-si-no-my1').html("X");
        }

        //Player Indicator

        //Riempio l'indicatore del player con il nick giusto
        if (main_player.id == 1) {
            var divplayer1 = document.getElementById("player1");
            var divplayer2 = document.getElementById("player2");
            divplayer1.innerHTML = main_player.nickname;
            divplayer2.innerHTML = main_player.opponentsList[0].nickname;
        }
        if (main_player.id == 2) {
            var divplayer1 = document.getElementById("player1");
            var divplayer2 = document.getElementById("player2");
            divplayer1.innerHTML = main_player.opponentsList[0].nickname;
            divplayer2.innerHTML = main_player.nickname;
        }

        //Cambio di Player
        if (game_status.activePlayer == 1) {
            var divplayer1 = document.getElementById("player1");
            var divplayer2 = document.getElementById("player2");
            divplayer1.style.backgroundColor = 'rgba(249, 119, 61, .7)';
            divplayer2.style.backgroundColor = '#696969';
			
			if(main_player.id == 1) {
				document.getElementById("my-team").classList.add("turn");
				document.getElementById("opponent-team").classList.remove("turn");
			}
			else {
				document.getElementById("opponent-team").classList.add("turn");
				document.getElementById("my-team").classList.remove("turn");
			}
			
        }
        if (game_status.activePlayer == 2) {
            var divplayer1 = document.getElementById("player1");
            var divplayer2 = document.getElementById("player2");
            divplayer2.style.backgroundColor = 'rgba(249, 119, 61, .7)';
            divplayer1.style.backgroundColor = '#696969';
			document.getElementById("opponent-team").classList.add("turn");
			document.getElementById("my-team").classList.remove("turn");
			
			if(main_player.id == 1) {
				document.getElementById("opponent-team").classList.add("turn");
				document.getElementById("my-team").classList.remove("turn");
			}
			else {
				document.getElementById("my-team").classList.add("turn");
				document.getElementById("opponent-team").classList.remove("turn");
			}
        }



        //Phase Indicator
        if (game_status.currentPhase == "beginning") {
            var li = document.getElementById("beginning");
            li.setAttribute("style", "background-color: rgba(249, 119, 61, .7);");
            console.log("Entra");
        } else {
            var li = document.getElementById("beginning");
            li.setAttribute("style", "background-color: #696969;");
        }

        if (game_status.currentPhase == "precombat main") {
            var li = document.getElementById("precombat_main");
            li.setAttribute("style", "background-color: rgba(249, 119, 61, .7);");
        } else {
            var li = document.getElementById("precombat_main");
            li.setAttribute("style", "background-color: #696969;");
        }

        if (game_status.currentPhase == "combat") {
            var li = document.getElementById("phase_combat");
            li.setAttribute("style", "background-color: rgba(249, 119, 61, .7);");
        } else {
            var li = document.getElementById("phase_combat");
            li.setAttribute("style", "background-color: #696969;");
        }

        if (game_status.currentPhase == "postcombat main") {
            var li = document.getElementById("postcombat_main");
            li.setAttribute("style", "background-color: rgba(249, 119, 61, .7);");
        } else {
            var li = document.getElementById("postcombat_main");
            li.setAttribute("style", "background-color: #696969;");
        }

        if (game_status.currentPhase == "ending") {
            var li = document.getElementById("ending");
            li.setAttribute("style", "background-color: rgba(249, 119, 61, .7);");
        } else {
            var li = document.getElementById("ending");
            li.setAttribute("style", "background-color: #696969;");
        }

        //Step indicator
        if (game_status.currentStep != null) {
            var divstep = document.getElementById("step");
            divstep.innerHTML = game_status.currentStep;
			divstep.classList.add("step");
        }

        $('#game_phase').html("Phase: <span>" + game_status.currentPhase + "</span>");
        $('#game_step').html("Step: <span>" + game_status.currentStep + "</span>");
        if (!playerColored) {
            try {
                var idPlayer = main_player.id;
                if (idPlayer != null && idPlayer != 0 && idPlayer != -1) {
                    document.getElementById('nomePlayer').classList.add('playerID' + idPlayer);
                    document.getElementById('myDOT').classList.add('playerdotID' + idPlayer);
                    document.getElementById('myDOT').id = idPlayer;
                    var k = 1;
                    for (var oppId = 0; oppId < main_player.opponentsList.length; oppId++, k++) {
                        try {
                            var idOpponent = main_player.opponentsList[oppId].id;
                            document.getElementById('opponentDOT' + k).classList.add('playerdotID' + idOpponent);
                            document.getElementById('opponentDOT' + k).id = idOpponent;
                        } catch (e) {
                            console.log("Errore: " + e);
                        }
                    }
                    playerColored = true;
                }
            } catch (e) {
                console.log("Errore: " + e);
            }
        }
    }


    /*  #UNDER_AREA */

    //Multiplayer OR 2Player form show
    $('#gt_select :radio').click(function() {
        var chkd = $('#gt_select :radio:checked').attr('value'); //.val()?
        if (chkd == 1) {
            $('#2p_var_opt').show();
            $('#multi_var_opt').hide();
            $('#conf_mp').hide();
            $('#conf_2p').show();
        } else {
            $('#conf_2p').hide();
            $('#conf_mp').show();
            $('#multi_var_opt').show();
            $('#2p_var_opt').hide();
        }
    });

    $('#al').click(function() {
        $('#ar').removeProp('checked');
    });
    $('#ar').click(function() {
        $('#al').removeProp('checked');
    });

    //get the values for Multiplayer
    $('#conf_mp').click(function() {
        var deck, sideboard;
        var m_var = $('#multi_var :radio:checked').val();
        var roi = $('#roi').val();
        var c_var = $('#multi_cvar :radio:checked').val();

        //  gametype = $('#gt_select :radio:checked').val();

        if (typeof c_var === 'undefined') {
            c_var = 0;
        }
        if (!(roi > 0)) {
            roi = 0;
        }
        var lroi = $('#lroi').is(':checked') ? 1 : 0;
        //console.log("isLROI: "+lroi); //DEBUG
        var amp = $('#amp').is(':checked') ? 1 : 0;
        var dc = $('#dc').is(':checked') ? 1 : 0;
        var stt = $('#stt').is(':checked') ? 1 : 0;
        var al = $('#al').is(':checked') ? 1 : 0;
        var ar = $('#ar').is(':checked') ? 1 : 0;

        p_team = $('#team_name').val();

        nick = document.getElementById("nickname").value;
        if ((document.getElementById("deck").value) == "1") {
            deck = deck1;  // rosso / nero
        } else if ((document.getElementById("deck").value) == "2") {
            deck = deck2; // blu / bianco
        } else if ((document.getElementById("deck").value) == "3") {
            deck = deck3; // mono verde 
        } else if((document.getElementById("deck").value) == "4"){
            deck = deck0_creatures_only;
        }
        if ((document.getElementById("sideboard").value) == "1") {
            sideboard = sideboard1;
        } else if ((document.getElementById("sideboard").value) == "2") {
            sideboard = sideboard2;
        }

        socket.emit('ready', {
            "playerSettings": {
                "playerInfo": {
                    "id": "",
                    "nickname": nick
                },
                "playerDeck": deck,
                "playerSideboard": sideboard,
                "twoplayer": gametype,
                "variant": m_var,
                "c_variant": c_var,
                "lroi": lroi,
                "roi": roi,
                "amp": amp,
                "dc": dc,
                "stt": stt,
                "al": al,
                "ar": ar
            }
        });
        document.getElementById('nomePlayer').innerHTML = "PLAYER: " + nick;
        $('#nomePlayer').append(" @" + p_team);

        //then HIDE
        $(this).hide();

    });

    $('#conf_2p').click(function() {
        var deck, sideboard;
        var c_var = $('#2p_cvar :radio:checked').val();
        language = $('#languages :radio:checked').val();
        console.info("lng " + language);

        if (typeof c_var === 'undefined') {
            c_var = 0;
        }

        nick = document.getElementById("nickname").value;
        if ((document.getElementById("deck").value) == "1") {
            deck = deck1;
        } else if ((document.getElementById("deck").value) == "2") {
            deck = deck2;
        } else if ((document.getElementById("deck").value) == "3") {
            deck = deck3;
        } else if ((document.getElementById("deck").value) == "4") {
            deck = deck0_creatures_only;
        }
        if ((document.getElementById("sideboard").value) == "1") {
            sideboard = sideboard1;
        } else if ((document.getElementById("sideboard").value) == "2") {
            sideboard = sideboard2;
        }

        socket.emit('ready', {
            "playerSettings": {
                "playerInfo": {
                    "id": "",
                    "nickname": nick
                },
                "playerDeck": deck,
                "playerSideboard": sideboard,
                "twoplayer": gametype,
                "variant": 0,
                "c_variant": c_var,
                "lroi": 0,
                "roi": 0,
                "amp": 0,
                "dc": 0,
                "stt": 0,
                "al": 0,
                "ar": 0
            }
        });
        document.getElementById('nomePlayer').innerHTML = "Player: " + nick;

        //then HIDE
        $(this).hide();
    });

    //nasconde e visualizza la carta cliccata *--
    $('#card-overlay-background').click(function() {
        $(this).hide();
    });
    $('#card-overlay').click(function(event) {
        event.stopPropagation();
    });
    //--*

    /* --------------------- BARELY FUNCTIONs ---------------------------- */
    function populateOpponent(id) {
        id--;
        if (main_player.opponentsList[id].lifeTotal > 0)
            $('#lp-op' + id).html("LP: " + main_player.opponentsList[id].lifeTotal);
        if (main_player.opponentsList.length > 0)
            $('#library-op' + id).html('<span class="gen-overlay-background">Lib: ' + main_player.opponentsList[id].library.length);
        else
            $('#library-op' + id).html("No library");
        $('#hand-op' + id).html("<span>Hnd: " + main_player.opponentsList[id].hand.length + "</span>");
        $('#grave-op' + id).html("<span>Gry: " + main_player.opponentsList[id].graveyard.length + "</span>");
    }

    function populateStack() {
        var inv = false;
        for (var ind = 0; ind < stackArea.length; ind++) {
            var stackSPANCard = document.createElement("SPAN");
            $(stackSPANCard).addClass("card faceDown_false")
                .attr("id", "stack_" + ind)
                .attr('obj-id', stackArea[ind].idStack)
                .attr("data-id", stackArea[ind].originCard.id)
                .attr("title", stackArea[ind].name);
            //.append(getIMGfromID(stackArea[ind].originCard.id, "#stack_"+ind));
            ctrlrDIV = document.createElement("SPAN");
            var classi = "ctrl_circle round border_ playerID";
            $(ctrlrDIV).addClass(classi);

            /*
            for(var i = 0; i < battlefieldArea.length; i++){
                if(stackArea[ind].magicTargetId == battlefieldArea[i].magicTargetId){
                    var idTo = "stack_" + ind;
                    var idFrom = battlefieldArea[i].magicTargetId;
                    inv = true;
                    console.log("ID To: " + idTo);
                    console.log("ID From: " + idFrom);
                    setTimeout(makeLine, 500, "" + idFrom, "" + idTo, inv);
                }
            }
            */

            getCardImageFromId(stackArea[ind].originCard.id, ctrlrDIV);
            var unatarget = false;
            var div = document.createElement("div");
            $(div).addClass('popup');
            var popup = document.createElement("SPAN");
            $(popup).addClass('popuptext')
                .attr('id', 'myPopup' + "stack_" + ind);

            ctrlrDIV.appendChild(div);

            stackSPANCard.appendChild(ctrlrDIV);
            $(stackSPANCard).appendTo('#stack_area .content');
            try {
                if (stackArea[ind].targetted_id != null && stackArea[ind].targetted_id != -1) {
                    if (stackArea[ind].originCard.cardType[0].includes("enchantment")) {
                        console.log("Enchantement");
                    } else if (stackArea[ind].originCard.cardType[0].includes("artifact")) {
                        if (stackArea[ind].originCard.subtype[0].includes("Fortification")) {
                            console.log("Fortify");
                        } else {
                            console.log("Others");
                        }
                    } else {
                        console.log("Fallito");
                    }

                    var idFrom = "stack_" + ind;
                    var idTo = stackArea[ind].targetted_id;
                    var img1 = document.getElementById("img1");
                    var svg1 = document.getElementById("svg1");
                    if (img1 == null && svg1 == null) {
                        setTimeout(makeLine, 500, "" + idFrom, "" + idTo, inv);
                    } else if (img1 != null && svg1 == null) {
                        img1.parentNode.removeChild(img1);

                    } else if (svg1 != null && img1 == null) {
                        svg1.parentNode.removeChild(svg1);

                    } else {
                        img1.parentNode.removeChild(img1);
                        svg1.parentNode.removeChild(svg1);
                    }


                } else if (stackArea[ind].targetted_id != null &&
                    stackArea[ind].targetted_id.length != undefined &&
                    stackArea[ind].targetted_id.length > 0) {
                    console.log("MAI ESEGUITO NELLA SUA ESISTENZA");
                    for (var f = 0; f < stackArea[ind].targetted_id.length; f++) {
                        //var idFrom  =stackArea[ind].idStack;
                        var idFrom = "stack_" + ind;
                        var idTo = stackArea[ind].targetted_id[f];
                        setTimeout(makeLine, 500, "" + idFrom, "" + idTo, inv);
                    }
                }
            } catch (e) {
                console.log(e);
            }
        }
        $('#stack_area .content span').each(function() {
            $(this).click(viewCard);
        }); //da rimettere
    }

    function populateAnte() {
        for (var ind = 0; ind < anteArea.length; ind++) {
            if (!anteArea[ind].faceDown) {
                var anteSPANCard = document.createElement("SPAN");
                $(anteSPANCard).addClass("card faceDown_false")
                    //.attr("id","ante_"+ind)
                    .attr("id", "ante_" + anteArea[ind].magicTargetId)
                    .attr("data-id", anteArea[ind].id)
                    .attr("title", anteArea[ind].name);
                //.append(getIMGfromID(anteArea[ind].id), "#ante_"+ind);
                $('#ante_area .content').append(anteSPANCard);
            } else
                $('#ante_area .content').append("<span class='card faceDown_true' data-id='" + anteArea[ind].id + "'>" + anteArea[ind].name);
            //TODO need css to overlap!
        }
    }

    function populateCommand() {
        for (var ind = 0; ind < commandArea.length; ind++) {
            $('#command_area .content').append("<span class='card faceDown_" + commandArea[ind].faceDown + "' data-id='" + commandArea[ind].id + "'>" + commandArea[ind].name);
        }
    }

    function populateExile() {
        for (var ind = 0; ind < exileArea.length; ind++) {
            $('#exile_area .content').append("<span class='card faceDown_" + exileArea[ind].faceDown + "' data-id='" + exileArea[ind].id + "'>" + exileArea[ind].name);

        }
    }

    function populatePlayer() {

        $('#mp-my1').html("MP " + main_player.manaPool);
        $('#lp-my1').html("LP " + main_player.lifeTotal);
        $('#pc-my1').html("Poison " + main_player.poisonCounter);

        if (main_player.teamMates.length < 1) {
            $('#my-player1').css("width", "auto");
            $('#my-team1').css("width", "auto");
        }

        populatePlayerHand();
        populatePlayerLibrary();
        populatePlayerGraveyard();
    }


    function populatePlayerHand() {
        //console.log("PASSO DI QUI"); DEBUG
        for (var indi = 0; indi < main_player.hand.length; indi++) {
            var handCardSPAN = document.createElement("SPAN");
            $(handCardSPAN).addClass("card faceDown_false")
                //.addClass("border_"+player_genID[main_player.hand[indi].idOwner])
                .attr("id", "cid" + indi)
                .attr("data-id", main_player.hand[indi].id)
                .attr("data-pos", indi)
                .attr("title", main_player.hand[indi].name)
				.attr("type", main_player.hand[indi].cardType)
				.attr("draggable", true)
				.attr("ondragstart", "drag(event)");
            //.append(getIMGfromID(main_player.hand[indi].id, "#cid"+indi));

            //TODO [lowPriority] AppendChild to the card for the controller
            ctrlrDIV = document.createElement("SPAN");
            $(ctrlrDIV).addClass("ctrl_circle round border_" /*+(player_genID [ main_player.hand[indi].idController!=-1? player_genID[main_player.hand[indi].idController] : "null" ] )*/ );
            //$(ctrlrDIV).text(main_player.hand[indi].name);
            getCardImageFromId(main_player.hand[indi].id, ctrlrDIV);

            /*$(ctrlrDIV).css("background-image",
                "url(http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" /
                    + main_player.hand[indi].image[0] + "&type=card)");*/ // TODO
            handCardSPAN.appendChild(ctrlrDIV);
            document.getElementById("hand-my1").appendChild(handCardSPAN);
        }
        hand = document.getElementById("hand-my1");
        handCards = hand.getElementsByTagName("SPAN");
        for (var i = 0; i < handCards.length; i++) {
            handCards[i].addEventListener("click", viewCard, true);
        }
    }

    function populatePlayerLibrary() {
        var p_libDIV = document.getElementById("library-my1");
        if (playerLibArea.length > 0) {
            var p_lib_cardSPAN = document.createElement("SPAN");
            $(p_lib_cardSPAN).addClass("card faceDown_true");
            var p_lib_card_amnt = document.createElement("SPAN");
            $(p_lib_card_amnt).html(playerLibArea.length).addClass("noborder gen-overlay-background round");
            p_lib_cardSPAN.appendChild(p_lib_card_amnt);
            $(p_libDIV).html(p_lib_cardSPAN);
        } //else
        //$(p_libDIV).html("Empty library");
    }

    function populatePlayerGraveyard() {
        for (var indi = 0; indi < main_player.graveyard.length; indi++) {
            $('#grave-my1 .content').attr("title", main_player.graveyard[indi].name)
                .append("<div class='card faceDown_false' data-id='" + main_player.graveyard[indi].id + "'>" + main_player.graveyard[indi].name + "</div>");
            //.append(getIMGfromID(main_player.graveyard[ind].id, "#graveyard_cid"+ind));
        }
    }

    function checkAttach() {
        // Scorro il battlefield
        for (var i = 0; i < battlefieldArea.length; i++) {
            // Trovo un permanent che ha un attached_to settato
            if (battlefieldArea[i].attached_to != null && battlefieldArea[i].attached_to.length != 0) {
                console.log("ID NEL BATTLEFIELD: " + battlefieldArea[i].magicTargetId);
                setAttach(battlefieldArea[i].magicTargetId, battlefieldArea[i].attached_to);
            }
        }
    }

    function setAttach(attacher_id, attach_list) {
        //Per ogni elemento nell'attach list
        for (var i = 0; i < attach_list.length; i++) {
            //scorro di nuovo il battlefield
            for (var j = 0; j < battlefieldArea.length; j++) {
                // Quando trovo un permanent con id che compare anche nella lista attached to
                if (attach_list[i] === battlefieldArea[j].magicTargetId) {
                    // memorizzo la posizione del permanent attaccante all'interno della lista attached_by del permanente attaccato
                    var pos = getPosition(attacher_id, battlefieldArea[j]);
                    doAttach(attacher_id, battlefieldArea[j].magicTargetId, pos);
                }
            }
        }
    }

    function doAttach(attacherId, attachedId, pos) {
        var x, y;
        var x1, y1;
        var span_elements = document.getElementsByTagName("span");
        //Seleziono tutti gli span (sto cercando gli span che contengono i permanent)
        for (var i = 0; i < span_elements.length; i++) {
            if (span_elements[i].id == attachedId) {
                // Tra quelli selezionati trovo quello che deve essere attachato tramite id
                // e mi procuro gli offset
                x = span_elements[i].offsetLeft;
                y = span_elements[i].offsetTop;
                for (var it = 0; it < span_elements.length; it++) {
                    //Cerco l'id del permanent attaccante e ne setto i parametri
                    //in modo da avere la visualizzazione corretta dell'attach
                    if (span_elements[it].id == attacherId) {
                        x1 = x + (10 * (pos + 1));
                        y1 = y - (20 * (pos + 1));
                        span_elements[it].style.left = x1 + 'px';
                        span_elements[it].style.top = y1 + 'px';
                        span_elements[it].style.zIndex = '0';
                        span_elements[it].style.position = 'absolute';
                    }
                }
            }
        }
    }

    function getPosition(searchID, permanent) {
        var retval = 0;
        for (var i = 0; i < permanent.attached_by.length; i++) {
            if (permanent.attached_by[i] == searchID) {
                retval = i;
            }
        }
        return retval;
    }

    function populateBattlefield() {
        //@Galt (stocodice fa schifo)

        console.log("####à Battlefield ####")
        console.log(battlefieldArea)
        console.log("######################")

        var player_battlefieldDIV = document.getElementById("shared_bf");
        var player_combatDIV = document.getElementById("combat");

        for (var indi = 0; indi < battlefieldArea.length; indi++) { //TODO border and battlefieldID
            var permanentSPAN = document.createElement("SPAN");
            if (battlefieldArea[indi].status.faceUp) {
                $(permanentSPAN).addClass('card faceDown_false tapped_' + battlefieldArea[indi].status.tapped + " playerID" + battlefieldArea[indi].idController)
                    .attr('id', battlefieldArea[indi].magicTargetId)
                    .attr('obj-id', battlefieldArea[indi].originCard.id) //Modificato con Ivan
                    .attr("title", battlefieldArea[indi].name);
                permanentSPAN.style.position = 'relative';
                permanentSPAN.style.zIndex = '1';

                ctrlrDIV = document.createElement("SPAN");
                $(ctrlrDIV).addClass("ctrl_circle round border_").attr('id', 'ctrlrDIV');
                getCardImageFromId(battlefieldArea[indi].originCard.id, ctrlrDIV);
                permanentSPAN.appendChild(ctrlrDIV);

                if ( (battlefieldArea[indi].attacking) || (battlefieldArea[indi].blocking) ) {
                    player_combatDIV.appendChild(permanentSPAN); 
                } else {
                    player_battlefieldDIV.appendChild(permanentSPAN); 
                }

            } else {
                $(permanentSPAN).addClass('card faceDown_true tapped_' + battlefieldArea[indi].status.tapped + " playerID" + battlefieldArea[indi].idController).append(battlefieldArea[indi].name);
                // player_battlefieldDIV.appendChild(permanentSPAN); //@Galt !!! (per diana)
                if ( (battlefieldArea[indi].attacking) || (battlefieldArea[indi].blocking) ) {
                    player_combatDIV.appendChild(permanentSPAN); 
                } else {
                    player_battlefieldDIV.appendChild(permanentSPAN); 
                }
            }
        }
    }

    function IsJsonString(str) {
        try {
            JSON.parse(str);
        } catch (e) {
            return false;
        }
        return true;
    }

    //TIP -todo- visualizzare la carta solo se c'è il CardID

    function viewCard() {
        $('#card-overlay-background').css("display", "flex").show();
        console.log("Asking for " + this.getAttribute('data-id'));

        $('#blob-card').empty();
        getIMGfromID(this.getAttribute('data-id'), '#blob-card');
    }

    function setCardImg(selCard, tag) {
        if (selCard.PrintedCardID[language] == null) {
            //$('#blob-card').html("<span>Printed Card ID in English "+selCard.PrintedCardID["en"]);
            if ((selCard.PrintedCardID["en"][0] === undefined))
                $('#blob-card').html("<span>Printed Card ID unknown</span>");
            console.info("No " + language + " card, goin' en");
            getJSONBlob(selCard.PrintedCardID["en"][0], tag);
        } else {
            //$('#blob-card').html("<span>Printed Card ID in "+language+" "+selCard.PrintedCardID[language]);
            getJSONBlob(selCard.PrintedCardID[language][0], tag);
        }
    }

    function getIMGfromID(cid, tag) {
        if (Object.keys(cache_cardPIDs).length > 0) {
            //console.log("#AJ#");
            //console.log(cid);
            //console.log(cache_cardPIDs[cid]);//DEBUGs
            if (!cache_cardPIDs[cid] === undefined) {
                //console.log("AJ0");
                setCardImg(cache_cardPIDs[cid], tag);
            } else {
                // console.log("AJ1");
                //console.log(cache_cardPIDs);
                imgAjax(cid, tag);
            }
        } else {
            //console.log("AJ2");
            //console.log(cache_cardPIDs);
            imgAjax(cid, tag);
        }
    }

    function imgAjax(cid, tag) {

        jQuery.ajax({
            type: "GET",
            //url: 'http://mtg.dmi.unipg.it/mancini/rest-server/api/imgFromCardid/' + cid,
            url: '"https://www.afterlifegdr.com/test/mtg/testjson.php?cardid=' + cid,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            //headers: {"Access-Control-Allow-Origin":"*","Access-Control-Allow-Methods":"GET, POST, PUT, DELETE", "Access-Control-Allow-Headers":"Content-Type, Aceept"},
            success: function(data, status, jqXHR) {
                var card = JSON.parse(data).card;
                console.log(card);

                /*cache_cardPIDs[cid]=jQuery.parseJSON(jqXHR.responseText);
                //console.log("HERE"); //DEBUG
                setCardImg(jQuery.parseJSON(jqXHR.responseText), tag);*/
            },

            error: function(jqXHR, status) {
                // error handler
                console.log("ERR");
            }
        });

    }

    function getCardImageFromId_old(cardId, div) {

        //console.log(div);
        $.getJSON(
            //"http://mtg.dmi.unipg.it/mancini/rest-server/api/imgFromCardid/" + cardId,
            "https://www.afterlifegdr.com/test/mtg/testjson.php?cardid="+ cardId,
            function(data) {
                //console.log(div);
                $(div).css("background-image", "url(http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + data.card.multiverseid + "&type=card)");
            }
        );
    }

    function getCardImageFromId(cardId, div) { // modificato da Damiano 07/2018 storage data
        try {
            if (localStorage.getItem(cardId + "DATAIMG") == null) {
                $.getJSON(
                    //"http://mtg.dmi.unipg.it/mancini/rest-server/api/imgFromCardid/" + cardId,
                    "https://www.afterlifegdr.com/test/mtg/testjson.php?cardid="+ cardId,
                    function(data) {
                        localStorage.setItem(cardId + "DATA", data.card.multiverseid);
                        $(div).css("background-image", "url(http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + data.card.multiverseid + "&type=card)");
                    }
                );
            } else {
                var multiverseid = localStorage.getItem(cardId + "DATAIMG");
                $(div).css("background-image", "url(http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + multiverseid + "&type=card)");
            }
        } catch (e) {}
    }


    function getJSONBlob(pcid, tag) {
        if (Object.keys(cache_cardBlobs).length > 0) {
            if (!cache_cardBlobs[pcid] === undefined) {
                var bimage = document.createElement('img');
                bimage.src = 'data:image/png;base64,' + jQuery.parseJSON(jqXHR.responseText).image;
                $(tag).html(bimage);
            } else {
                blobAjax(pcid, tag);
            }
        } else {
            blobAjax(pcid, tag);
        }
    }


    // carico il blob image
    function blobAjax(pcid, tag) {
        if (typeof arrayimage[pcid] === 'undefined') {
            // l'immagine non è nell'array globale
            jQuery.ajax({
                type: "GET",
                //url: 'http://mtg.dmi.unipg.it/mancini/rest-server/api/imgFromCardid/' + pcid,
                url: '"https://www.afterlifegdr.com/test/mtg/testjson.php?cardid=' + pcid,

                contentType: "application/json; charset=utf-8",
                dataType: "json",
                //headers: {"Access-Control-Allow-Origin":"*","Access-Control-Allow-Methods":"GET, POST, PUT, DELETE", "Access-Control-Allow-Headers":"Content-Type, Aceept"},
                success: function(data, status, jqXHR) {
                    //console.log(jQuery.parseJSON(jqXHR.responseText));
                    //insertCardImg(jQuery.parseJSON(jqXHR.responseText).image);
                    var bimage = document.createElement('img');
                    bimage.src = "url(http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + idimg + "&type=card)";
                    arrayimage[pcid] = bimage.src;
                    $(tag).html(bimage);
                },

                error: function(jqXHR, status) {
                    // error handler
                    console.log("ERR");
                }
            });
        } else {
            //l'immagine  è nell'array globale
            var bimage = arrayimage[pcid];
            $(tag).html(bimage);
        }
    }

    //ONCLICK choice button

    function showpopuptarget(idcard) {
        try {
            console.log("popuppppp" + idcard);
            var popup = document.getElementById("myPopup" + idcard);
            if (popup != null)
                popup.classList.toggle("show");
        } catch (errore) {
            console.log("errore popup: " + errore.message);
        }
    }


    function makenickname() {
        var text = "";
        //var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        var possible = "ABCDEFGHIJKLMNopqrstuvwxyz23456789";

        for (var i = 0; i < 5; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }

    function makeLine(IDfrom, IDto, inv) {
        try {
            // Disegno la linea dallo stack al permanent da attachare
            var sharedbf = document.getElementById("shared_bf");

            // Prendo l'offset del div del battlefield
            var ot = sharedbf.offsetTop;
            var ol = sharedbf.offsetLeft;

            console.log("ID FROM: " + IDfrom);
            console.log("ID TO: " + IDto);

            // Ottengo gli HTML element dei due permanent
            var cardfrom = document.getElementById(IDfrom);
            var cardto = document.getElementById(IDto);

            console.log("CARD FROM: " + cardfrom);
            console.log("CARD TO: " + cardto);

            if (inv == true) {
                var offleft2 = cardfrom.offsetLeft;
                var offtop2 = cardfrom.offsetTop;
                var altezza2 = cardfrom.offsetHeight;
                var larghezza2 = cardfrom.offsetWidth;

                var offleft1 = cardto.offsetLeft + ol;
                var offtop1 = cardto.offsetTop + ot;
                var altezza1 = cardto.offsetHeight;
                var larghezza1 = cardto.offsetWidth;
            } else {
                var offleft1 = cardfrom.offsetLeft;
                var offtop1 = cardfrom.offsetTop;
                var altezza1 = cardfrom.offsetHeight;
                var larghezza1 = cardfrom.offsetWidth;

                var offleft2 = cardto.offsetLeft + ol;
                var offtop2 = cardto.offsetTop + ot;
                var altezza2 = cardto.offsetHeight;
                var larghezza2 = cardto.offsetWidth;
            }


            // Con tutti i dati che ho posso ricavarmi il centro di ogni carta e disegnare una linea tra i due punti
            var x1 = offleft1 + (larghezza1 / 2);
            var y1 = offtop1 + (altezza1 / 2);

            var x2 = offleft2 + (larghezza2 / 2);
            var y2 = offtop2 + (altezza2 / 2);

            var marker = document.createElementNS('http://www.w3.org/2000/svg', 'marker');
            marker.setAttributeNS(null, 'id', 'triangle');
            marker.setAttributeNS(null, 'viewBox', '0 0 10 10');
            marker.setAttributeNS(null, 'refX', '0');
            marker.setAttributeNS(null, 'refY', '5');
            marker.setAttributeNS(null, 'markerUnits', 'StrokeWidth');
            marker.setAttributeNS(null, 'markerWidth', '4');
            marker.setAttributeNS(null, 'markerHeight', '3');
            marker.setAttributeNS(null, 'orient', 'auto');

            var path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
            path.setAttributeNS(null, 'd', 'M 0 0 L 10 5 L 0 10 z');
            path.setAttributeNS(null, 'fill', 'red');

            marker.appendChild(path);

            //Creo l'elemento linea e setto i vari parametri
            var line = document.createElementNS('http://www.w3.org/2000/svg', 'line');

            line.setAttributeNS(null, 'x1', x1);
            line.setAttributeNS(null, 'y1', y1);
            line.setAttributeNS(null, 'x2', x2);
            line.setAttributeNS(null, 'y2', y2);
            line.setAttributeNS(null, 'stroke', 'red');
            line.setAttributeNS(null, 'stroke-width', '5');
            line.setAttributeNS(null, 'marker-end', 'url(#triangle)');

            //Creo un svg panel, setto i parametri e gli appendo la linea che ho creato prima
            var svgpanel = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
            svgpanel.appendChild(line);
            svgpanel.appendChild(marker);
            svgpanel.setAttributeNS(null, 'style', 'position: absolute; z-Index:2;');
            svgpanel.setAttributeNS(null, 'top', ot);
            svgpanel.setAttributeNS(null, 'left', ol);
            svgpanel.setAttributeNS(null, 'width', '1080px');
            svgpanel.setAttributeNS(null, 'height', '800px');
            svgpanel.setAttributeNS(null, 'id', 'svg1');

            // Appendo al body l'svg con la linea
            document.getElementById('body').prepend(svgpanel);
        } catch (e) {
            console.log(e);
        }

    }

    //permette di velocizzare i test con clic automatici sul client quando viene caricata la pagina
    var testMode = false;
    var damiano = false; // martina metti questo a false se ci lavori tu...
    if (testMode) {
        document.getElementById("nickname").value = "Player";
        document.getElementById("clicknickname").click();
        setTimeout(function() {
            document.getElementById("connect").click();
        }, 1000);
        if (damiano) {
            setTimeout(function() {
                document.getElementById("conf_2p").click();
            }, 6000);

        }
    }


}