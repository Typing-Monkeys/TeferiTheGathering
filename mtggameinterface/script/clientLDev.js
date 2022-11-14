
/*jslint browser: true, eqeq: true, plusplus: true, sloppy: true, white: true*/
/*global $ jQuery alert io console GAME_STATUS TEXT_MESSAGE*/
/**************************************************
** GLOBAL VARIABLES
**************************************************/
var deck1 = Array();
var pplayer, aplayer, variant, language,i,p_team,gametype, handCards=[], id_name=[];

/*AREAS*/
var hand, main_player, stackArea, anteArea, commandArea, exileArea, battlefieldArea, playerGraveyArea, playerLibArea;

/*CACHE*/
var cache_cardBlobs = cache_cardPIDs = [];

var generics=["p2","p3","p4","p5"], player_genID=[]; //FIXME per player_genID



//ONCLICK choice button
function makeChoice (choiceId, is_multiple){
    var selected_choices = $("#sel_choice"+choiceId+" #choices"+choiceId+" option:selected").map(function(){return this.value}).get().join("\", \"");
    //Per poter selezionare valori multipli .val() necessita che nell'HTML (sulla select) sia specificato "multiple"
    //ORIGINAL var choiceJSON = '{"choice":{"idChoice":choiceId, "idOption":$("#sel_choice"+choiceId+" #choices"+choiceId+" option:selected").val(), "idPlayer":''}}';
    var choiceJSON = "";
    if(is_multiple === "m"){ //if .val() has read multiple values
        //console.log('{"choice":{"idChoice":'+choiceId+', "idOptions":["'+selected_choices+'"], "idPlayer":""}}'); //Choice DEBUG
        choiceJSON = '{"choice":{"idChoice":'+choiceId+', "idOptions":["'+selected_choices+'"], "idPlayer":""}}';
    }
    if(is_multiple === "s"){
        //var selected_choices = $("#sel_choice"+choiceId+" #choices"+choiceId+" option:selected").val() || []; //LEGACY SNIPPET, only for single choice
        choiceJSON = '{"choice":{"idChoice":'+choiceId+', "idOptions":['+selected_choices+'], "idPlayer":""}}';
    }

    socket.emit('choice', JSON.parse(choiceJSON));
    $('#sel_choice'+choiceId).remove();
}



window.onload = function () {

//TEMP
$('#fake_load').click(function(){
   $('.create_room').hide();

   $('.in_room').show();
   $(this).hide();
});
//END TEMP


    var option_button=document.getElementById("option_button");
    var option2_button=document.getElementById("option2_button");
    var option=document.getElementById("option");
    var option2=document.getElementById("option2");
    var command=document.getElementById("command");
    var connect=document.getElementById("connect");
    var disconnect = document.getElementById("disconnect");
    var send_attempt = document.getElementById("send_attempt");
    var priority_button = document.getElementById("priority_button");
    //var send_data = document.getElementById("send_data"); //??
    var start_game = document.getElementById("start_game");
    //var receive_zone = document.getElementById("receive_zone"); //deprecated
    nick = "Debugger"; // document.getElementById("nickname").value;
   
    var ctrlrDIV;
    //_INITIALIZATION_
    $('#conf_mp, #conf_2p, #multi_var_opt, #disconnect, #sel_choice').hide();
    // $("#gt_select, #languages").buttonset();

    $('#nomePlayer').html("Debugger window");
    $('.in_room').hide();
    $('#under_area').hide();


    //$('#card-overlay-background').hide();

    /*
    var deck1 = Array();
    for(var i = 0; i < 40; i++)
    {
        deck1[i] = i+6236;
    }
	*/
    /*for(var i = 40; i < 60; i++)
    {
        deck1[i] = 1730;
    }*/
	/*
    var deck2 = Array();
    for(var i = 0; i < 60; i++)
    {
        deck2[i] = i+6236+60;
    }
	*/

	  var deck1 = Array();
    for(var i = 0; i < 40; i++)
    {
        deck1[i] = i+6236;
    }

	//Inserisce un po' di planeswalker, temporaneo
	for(var i = 0; i < 10; i++)
    {
        deck1[i] = 6557;
    }

	var deck2 = Array();
    for(var i = 0; i < 60; i++)
    {
        deck2[i] = i+6236+60;
    }

    var sideboard1 = Array();
    for(var i = 0; i < 15; i++)
    {
        sideboard1[i] = i+6236+60+15;

    }

    var sideboard2 = Array();
    for(var i = 0; i < 15; i++)
    {
        sideboard2[i] = i+6236+60+15+15;
    }

    /**************************************************
    ** BUTTON FUNCTIONS
    **************************************************/

    //BUTTON JOIN ROOM
    connect.onclick = function(){
        socket = io.connect(IND_HTML_TO_JS); //GLOBAL

        $(connect).prop("disabled", true);
        $('#disconnect').show();
         $('#under_area').show();
        $('#in_room_buttons').show();


        var nplayer = 3// $('#nop').val();
        var gameMode = 1 // $('#gt_select :radio:checked').val();
        
        if( gameMode==0 && nplayer >= 3){
            //socket.emit('new player',{"num_player":nplayer});
            console.info("asking for a "+nplayer+" players room");
        }else if(gameMode==1){
            console.info("connecting debugger");
            socket.emit('new debugger',{"deb":0});
        }else{
            alert("Wrong number of players, setting up a match of 3");
            console.info("asking for a 3 players room");
            socket.emit('new player',{"num_player":3});
        }
        gametype = gameMode;

        $('#conf_2p').show();
        $('#conf_mp').hide();

        //EVENT: PRINT ON HTML CLIENT
        socket.on('messaggio', function (data) {
            onMessage(data);
        });
    };

    //BUTTON DISCONNECT
    disconnect.onclick=function (){
        document.getElementById("message").textContent="disconnected";
        console.log("DISCONNECTED");
        socket.disconnect();
        $('.create_room').show();
        $('.in_room').hide();
    }

    /**************************************************
    ** 				 OTHER FUNCTIONS                 **
    **************************************************/

    function onMessage(message) {
        var ind, choiceId;
        if(IsJsonString(message))
        {
            message = JSON.parse(message);
            switch(message.type)
            {
                case TEXT_MESSAGE:
                    switch(message.subtype){
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
                case CHOICE:
                    //console.log("TYPE2");
                    //console.log(JSON.stringify(message)); //DEBUG
                    type2subX(message,message.subtype);
                    break;
                case GAME_STATUS:
                    //FUTURE TODO - snippet subtype, questo ok per lo 0
                    //console.log("TYPE3");
                    //console.log(JSON.stringify(message)); //De-comment for debug purpose, very verbose!
                    switch(message.subtype){
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
        }
        else
        {
            document.getElementById("message").value += (message + "\n");
        }
    }

    function type1sub1(message){
        //console.log(message);   //DEBUG
        switch(message.data){
            case "readySPD":
                $('#send_p_data').show();
                $('#connect').hide();
                break;
            case "readyStart":
                $('#start_game').show();
                $('.create_room').hide();
                $('.in_room').show();
                break;
            case "gameStarted":
                $('#start_game').hide();
                break;
        }
    }

    function type2subX(message, subtype){
         var choices = JSON.parse(message.data);
            choiceId = choices.idChoice;
            $('#choices_area').append('<div id="sel_choice'+choiceId+'"><label></label><select id="choices'+choiceId+'" '+((subtype==2)?"multiple":"")+'></select><input id="choose'+choiceId+'" type="button" value="Choose" onclick="makeChoice('+choiceId+', '+(subtype==2?"\'m\'":"\'s\'")+');">');
            $('#sel_choice'+choiceId+' label').empty().text(choices.choiceText); //.html('')
            //$('#sel_choice'+choiceId+' #choices'+choiceId).html(''); //inutile
            for (ind=0; ind < choices.choiceOptions.length-1;ind++){
                $('#sel_choice'+choiceId+' #choices'+choiceId).append('<option value="'+choices.choiceOptions[ind].idOption+'">'+choices.choiceOptions[ind].optionText+'</option>');
            }
            $('#sel_choice'+choiceId+' #choices'+choiceId).append('<option value="'+choices.choiceOptions[ind].idOption+'" selected>'+choices.choiceOptions[ind].optionText+'</option>');

            $('#choice_area').show();
    }

    function type3sub0(message){
        console.log("TYPE 3 - 0");
        //console.log(message);   //DEBUG
        console.log(message.data);   //DEBUG
        //prendo la key che mi serve
        
        var data_game_status = JSON.parse(message.data);
        var game_status = data_game_status.debugGame;
        var get_player = game_status["player"];
        var get_bf = game_status["battlefield"];
        var get_exile = game_status["exile"];
        var get_stack = game_status["stack"];
        
        
        console.log("prova1: ",get_player);
        $('#dataPlayers').text('');
        $('#command_players').html('');
        var elem = "";
        var gElem = "";

        
        
        for(var oppId=0; oppId<get_player.opponentsList.length; oppId++){
            /*var hand=game_status.player.opponentsList[oppId].hand;
            for(var cId=0; cId<hand.length; cId++){
                var c1=hand[cId];
                var name=c1.name[0];
                $('#hand').append(name);
                document.getElementById('hand').style.display="block";
                console.log("provamano: ",name);
            }
            
            "nickname":"e",
            "id":1,
            "hand":[
                 "id":6268,
                "faceDown":true,
                "castable":false,
                "idExile":-1,
                "idGraveyard":0,
                "name":[
                    "Nagging Thoughts"
                ],
                "idObject":-1,
                "idOwner":1,
                "idController":1
                },
                ...
            ],
            "graveyard":[ come hand ],
            "library":[ come hand ],
            "lifeTotal":-1,
            "manaPool":"0",
            "poisonCounter":0
            */
            var ple = game_status.player.opponentsList[oppId];
            console.log(ple.nickname);
            
            var nomePle = ple.nickname.toString();
            
            elem +=  '<div id=\'op-player' + ple.id.toString() +'\'';
            elem += 'class=\'col-md-5 col-sm-5 op_player\'> <div class=\'row row-centered\'> <span>' + nomePle;
            elem += ' (id ='+ple.id.toString()+') </span> </div>'; // name
            

            //var totHand = ple.hand.length;
            // elem += "<hr><span>Carte in mano del giocatore "+ple.nickname+"':</span>";  

            elem += '<div class=\'row\'> <div class=\'input-group\'> <span class=\'input-group-addon\'>Player Hand</span><select id=\'sel_hand_player' + ple.id.toString() +'\' onChange=\'openModalSetCard(this, \"'+ ple.id.toString() +'\", \"H\");\' class=\'form-control\'> <optgroup label=\'Lista Hand\'>'
            var cardInHand = "<option class='handCardNameOPT cardlstOPT' value='' disabled selected> Select a Card</option>";
            $.each(ple.hand, function( key, card ) {
                var nameCard ="";
                $.each(card.name, function( ckey, cname ) {
                    if (ckey >0) nameCard +=' // ';
                    nameCard += cname+' ' ;
                })
                
                cardInHand +='<option class=\'handCardNameOPT cardlstOPT\' value=\''+card.id+'\'>'+ nameCard+ '( Card ID ='+card.id+')</option> '; // single card 
            } );
            elem += cardInHand;
            elem += "</optgroup> </select>  </div> </div>";
            
            //console.log(elem);
            //$('#dataPlayers').append(elem); 
            totalListCard[ple.id] = {};
     
            
            totalListCard[ple.id]['lib'] = ple.library.length;
            //elem += "<hr><span>Carte del grimorio del giocatore "+ple.nickname+"':</span>";  
            elem += '<div class=\'row\'> <div class=\'input-group\'> <span class=\'input-group-addon\'>Player Library</span><select id=\'sel_lib_player' + ple.id.toString() +'\' onChange=\'openModalSetCard(this, \"'+ ple.id.toString() +'\", \"L\");\' class=\'form-control\'> <optgroup label=\'Lista Library\'>'

            var cardInLibrary = "<option class='libCardNameOPT cardlstOPT' value='' disabled selected> Select a Card</option>";
            $.each(ple.library, function( key, card ) {
                var nameCard ="";
                $.each(card.name, function( ckey, cname ) {
                    if (ckey >0) nameCard +='// ';
                    nameCard += cname+ ' ';
                })
            
                cardInLibrary +='<option class=\'libCardNameOPT cardlstOPT\' value=\''+card.id+'_'+ key +'\' >'+ nameCard+ '( Card ID ='+card.id+')</option> '; // single card 
            } );
            elem += cardInLibrary;
            elem += "</optgroup> </select>  </div> </div>";

            
             
            //console.log(elem);
            //$('#dataPlayers').append(elem); 
   
            totalListCard[ple.id]['gra'] = ple.library.length;
            //elem += "<hr><span>Carte nel cimitero del giocatore "+ple.nickname+"':</span>";            
            elem += '<div class=\'row\'> <div class=\'input-group\'> <span class=\'input-group-addon\'>Player Graveyard</span><select d=\'sel_grv_player' + ple.id.toString() +'\' onChange=\'openModalSetCard(this, \"'+ ple.id.toString() +'\", \"G\");\' class=\'form-control\'> <optgroup label=\'Lista Graveyard\'>';

            var cardInGraveyard ="<option class='grvCardNameOPT cardlstOPT' value='' disabled selected> Select a Card</option>";
            $.each(ple.graveyard, function( key, card ) {
                var nameCard ="";
                $.each(card.name, function( ckey, cname ) {
                    if (ckey >0) nameCard +='// ';
                    nameCard += cname+ ' ';
                })
            
                cardInGraveyard +='<option class=\'grvCardNameOPT cardlstOPT\' value=\''+card.id+'_'+ key +'\' >'+ nameCard+ '( Card ID ='+card.id+')</option> '; // single card 
            } );
            elem += cardInGraveyard;
            elem += "</optgroup> </select>  </div> </div>";
        
            
            elem +='</div>';
            
            var datiUser ="";
                    
            datiUser += '<div id=\'dataPlayer'+ple.id+'\' class=\'dataPlayerRow\'>';
                datiUser += '<div class=\'playername\'>'+ple.nickname+'</div>';
                datiUser += '<div class=\'puntivita\'>Life Point <input value=\''+ple.lifeTotal+'\' id=\'dataLTPlayer'+ple.id+'\'\> </div>';
                datiUser += '<div>ManaPool</div><input value=\''+ple.manaPool+'\' type="text" name="manaCost" id=\'dataLMPlayer'+ple.id+'\')" size="24"> <span class="manaSymbolBox clickable" onclick="addManaCost(\'{C}\',\''+ple.id+'\')"><img src="images/C.gif" alt="C" width="15" height="15"></span> <span class="manaSymbolBox clickable" onclick="addManaCost(\'{W}\',\''+ple.id+'\')"><img src="images/W.gif" alt="W" width="15" height="15"></span> <span class="manaSymbolBox clickable" onclick="addManaCost(\'{U}\',\''+ple.id+'\')"><img src="images/U.gif" alt="U" width="15" height="15"></span> <span class="manaSymbolBox clickable" onclick="addManaCost(\'{B}\',\''+ple.id+'\')"><img src="images/B.gif" alt="B" width="15" height="15"></span> <span class="manaSymbolBox clickable" onclick="addManaCost(\'{R}\',\''+ple.id+'\')"><img src="images/R.gif" alt="R" width="15" height="15"></span> <span class="manaSymbolBox clickable" onclick="addManaCost(\'{G}\',\''+ple.id+'\')"><img src="images/G.gif" alt="G" width="15" height="15"></span>';
                datiUser += '<div class=\'poisoncounter\'>Poison Counter <input value=\''+ple.poisonCounter+'\' id=\'poisonCounter'+ple.id+'\'\> </div>';
                datiUser += '<input type=\'button\' id=\'butmodplay\' value=\'Modifica\' onclick=\'modPlayer(\"'+ple.id+'\");\'>';
            datiUser += '</div>';

            
            $('#command_players').append(datiUser); 
            
            
            /**********************************************************************************************
            *** HTML TO CREATE ***
            <div id="op-player1" class="col-md-5 col-sm-5">
                <div class="row row-centered">
                    <span>Player 1</span>
                </div>
 
                <div class="row">
                    <div class="input-group"> 
                        <span class="input-group-addon">Hand</span>
                        <select class="form-control">
                            <optgroup label="Lista Hand">
                            </optgroup>
                        </select>
                     </div>
                </div>
                <div class="row">
                    <div class="input-group"> 
                        <span class="input-group-addon">Library</span>
                        <select class="form-control">
                            <optgroup label="Lista Library">
                            </optgroup>
                        </select>
                     </div>
                
                </div>
                <div class="row">
                    <div class="input-group"> 
                        <span class="input-group-addon">Graveyard</span>
                        <select class="form-control">
                            <optgroup label="Lista Graveyard">
                            </optgroup>
                        </select>
                     </div>
                
                </div>
                
            </div>
            *********************************************************************************************************/

        }
        
        $('#playersList').html(elem); 
        
        
        gElem += '<div class=\'row\'> <div class=\'input-group\'> <span class=\'input-group-addon\'>BattleField</span><select id=\'sel_Battlefield\' onChange=\'openModalSetCard(this, null, \"B\");\' class=\'form-control\'> <optgroup label=\'Lista Battlefield\'>'
        var cardInBF = "<option class='bfCardNameOPT cardlstOPT' value='' disabled selected> Select a Card</option>";
        $.each(get_bf, function( key, card ) {
            var nameCard ="";
            $.each(card.name, function( ckey, cname ) {
                if (ckey >0) nameCard +=' // ';
                nameCard += cname+' ' ;
            });

            var cardOwer = card.idOwner;

            cardInBF +='<option class=\'handCardNameOPT cardlstOPT\' value=\''+card.originCard.id+'_'+cardOwer+'\'>'+ nameCard+ '( Card ID = '+card.originCard.id+'; Owner ID = '+cardOwer+')</option> '; // single card 
        } );
        gElem += cardInBF;
        gElem += "</optgroup> </select>  </div> </div>";
        
        
        gElem += '<div class=\'row\'> <div class=\'input-group\'> <span class=\'input-group-addon\'>Exile</span><select id=\'sel_Exile\' onChange=\'openModalSetCard(this, null, \"E\");\' class=\'form-control\'> <optgroup label=\'Lista Exile\'>'
        var cardInExc = "<option class='excCardNameOPT cardlstOPT' value='' disabled selected> Select a Card</option>";
        $.each(get_exile, function( key, card ) {
            var nameCard ="";
            $.each(card.name, function( ckey, cname ) {
                if (ckey >0) nameCard +=' // ';
                nameCard += cname+' ' ;
            });

            var cardOwer = card.idOwner;

            cardInExc +='<option class=\'excCardNameOPT cardlstOPT\' value=\''+card.id+'_'+cardOwer+'\'>'+ nameCard+ '( Card ID = '+card.id+'; Owner ID = '+cardOwer+')</option> '; // single card 
        } );
        gElem += cardInExc;
        gElem += "</optgroup> </select>  </div> </div>"; 
        
        
        
        gElem += '<div class=\'row\'> <div class=\'input-group\'> <span class=\'input-group-addon\'>Stack</span><select id=\'sel_Stack\' onChange=\'openModalSetCard(this, null, \"S\");\' class=\'form-control\'> <optgroup label=\'Lista Exile\'>'
        var cardInStc = "<option class='stcCardNameOPT cardlstOPT' value='' disabled selected> Select a Card</option>";
        $.each(get_stack, function( key, card ) {
            var nameCard ="";
            $.each(card.originCard.name, function( ckey, cname ) {
                if (ckey >0) nameCard +=' // ';
                nameCard += cname+' ' ;
            });

            var cardOwer = card.idOwner;

            cardInStc +='<option class=\'stcCardNameOPT cardlstOPT\' value=\''+card.originCard.id+'_'+cardOwer+'\'>'+ nameCard+ '( Card ID = '+card.originCard.id+'; Owner ID = '+cardOwer+')</option> '; // single card 
        } );
        gElem += cardInStc;
        gElem += "</optgroup> </select>  </div> </div>";
        
        
        $('#command_area').html(gElem);
    }



    /*	#UNDER_AREA	*/

    //nasconde e visualizza la carta cliccata *--
    $('#card-overlay-background').click(function() {
        $(this).hide();
    });
    $('#card-overlay').click(function(event) {
        event.stopPropagation();
    });
    //--*

    /* --------------------- BARELY FUNCTIONs ---------------------------- */

    function IsJsonString(str) {
        try {
            JSON.parse(str);
        } catch (e) {
            return false;
        }
        return true;
    }

    //TIP -todo- visualizzare la carta solo se c'è il CardID

    function viewCard(){
        $('#card-overlay-background').css("display","flex").show();
        console.log("Asking for "+this.getAttribute('data-id'));

        $('#blob-card').empty();
        getIMGfromID(this.getAttribute('data-id'), '#blob-card');
    }

    function setCardImg(selCard, tag){
        if (selCard.PrintedCardID[language] == null){
            //$('#blob-card').html("<span>Printed Card ID in English "+selCard.PrintedCardID["en"]);
            if((selCard.PrintedCardID["en"][0] === undefined))
                $('#blob-card').html("<span>Printed Card ID unknown</span>");
            console.info("No "+language+" card, goin' en");
            getJSONBlob(selCard.PrintedCardID["en"][0], tag);
        }else{
            //$('#blob-card').html("<span>Printed Card ID in "+language+" "+selCard.PrintedCardID[language]);
            getJSONBlob(selCard.PrintedCardID[language][0], tag);
        }
    }

    function getIMGfromID(cid, tag) {
        if (Object.keys(cache_cardPIDs).length>0){
            //console.log("#AJ#");
            //console.log(cid);
            //console.log(cache_cardPIDs[cid]);//DEBUGs
            if(!cache_cardPIDs[cid] === undefined){
                //console.log("AJ0");
                setCardImg(cache_cardPIDs[cid], tag);
            }else{
               // console.log("AJ1");
                //console.log(cache_cardPIDs);
                imgAjax(cid,tag);
            }
        }else{
            //console.log("AJ2");
            //console.log(cache_cardPIDs);
            imgAjax(cid,tag);
        }
    }

    function imgAjax(cid,tag){

        jQuery.ajax({
            type: "GET",
            url: 'http://mtg.dmi.unipg.it/mancini/rest-server/api/imgFromCardid/' + cid,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            //headers: {"Access-Control-Allow-Origin":"*","Access-Control-Allow-Methods":"GET, POST, PUT, DELETE", "Access-Control-Allow-Headers":"Content-Type, Aceept"},
            success: function (data, status, jqXHR) {
                var card = JSON.parse(data).card;
                console.log(card);

                /*cache_cardPIDs[cid]=jQuery.parseJSON(jqXHR.responseText);
                //console.log("HERE"); //DEBUG
                setCardImg(jQuery.parseJSON(jqXHR.responseText), tag);*/
            },

            error: function (jqXHR, status) {
             // error handler
             console.log("ERR");
            }
        });

    }


    function getJSONBlob(pcid, tag) {

        if (Object.keys(cache_cardBlobs).length>0)
            if(!cache_cardBlobs[pcid] === undefined){
                 var bimage = document.createElement('img');
                 bimage.src = 'data:image/png;base64,' + jQuery.parseJSON(jqXHR.responseText).image;
                 $(tag).html(bimage);
            }else
                blobAjax(pcid, tag);
        else
            blobAjax(pcid, tag);
    }

    function blobAjax(pcid, tag){
         jQuery.ajax({
             type: "GET",
             url: 'http://mtg.dmi.unipg.it/mancini/rest-server/api/imgFromCardid/'+pcid,
             contentType: "application/json; charset=utf-8",
             dataType: "json",
             //headers: {"Access-Control-Allow-Origin":"*","Access-Control-Allow-Methods":"GET, POST, PUT, DELETE", "Access-Control-Allow-Headers":"Content-Type, Aceept"},
             success: function (data, status, jqXHR) {
                 //console.log(jQuery.parseJSON(jqXHR.responseText));
                 //insertCardImg(jQuery.parseJSON(jqXHR.responseText).image);
                 var bimage = document.createElement('img');
                 bimage.src = 'data:image/png;base64,' + jQuery.parseJSON(jqXHR.responseText).image;
                 $(tag).html(bimage);
             },

             error: function (jqXHR, status) {
                 // error handler
                 console.log("ERR");
             }
         });
    }

}

var flagOut = 999;
var totalListCard = {};

function openModalSetCard(theselectId, thePle, typeOpp){
    var cardid = theselectId.value;
    console.log(cardid);
    
    if (flagOut == 999) return; // flag di sdebug attivo (999 = non attivo)
    /*
    1. open modal
    2. list of position / action
    3. activate button
    4. auto reload
    */
    
    var cardName = theselectId.options[theselectId.selectedIndex].text;
    
    if (thePle == null){   
        var res = cardid.split("_");
        cardid = res[0];
        thePle = res[1];
    }
    
    $('#theCardModal').css('display' , 'inherit');
    
    var charger ='';
    charger +='<div id=\'namefield\'>' +cardName+ '</div>';
    
    var oppList = {"H": "Hand",
                   "L" : "Library",
                   "G" : "Graveyard",
                   "B" : "Battlefield",
                   "E" : "Exile",
                   "S" : "Stack",
                   "A" : "Ante",
                   "C" : "commander"};
    
    var opt = '<select id=\'whereTo\' onChange=\'openRange(\"'+thePle+'\");\'><option disabled selected value =\'\'>Select Location</option>';
    
    $.each(oppList, function( idk, operational ) {
        
        opt += '<option value =\''+idk+'\'';
        if (idk == typeOpp) opt +=' disabled ';
        opt +='>'+operational+'</option>';
    });
    
    opt +='</select>';
    
    charger += opt;
    charger += '<div id=\'inputPlacer\'></div><input type=\'button\' onclick=\'sandEmit(\"'+thePle+'\", \"'+cardid+'\", \"'+typeOpp+'\");\' value=\'Set Position\' /> <div onclick=\'closeModal();\' id=\'exitModal\'></div>';
    
    $('#theCardModal').html(charger); 
    
    
}

function updateTextInput(val) {
  document.getElementById('textInput').value=val; 
}

function closeModal() {
    $('#theCardModal').css('display' , 'none');
    $('#theCardModal').html(" "); 
    
}




function openRange(thePlay){
    var typeOpp = document.getElementById("whereTo").value;
    var toHtml = "";
    
    if (typeOpp == "L"){
        var tits = totalListCard[thePlay].lib -1;
        toHtml = '<input id=\'valToPlace\' type=\'range\' name=\'rangeInput\' min=\'0\' max=\''+tits+'\' onchange=\'updateTextInput(this.value);\'> <input type=\'text\' id=\'textInput\' disabled value=\'\'>';   
    }
    if (typeOpp == "G"){
        var tits = totalListCard[thePlay].gra-1;
        toHtml = '<input id=\'valToPlace\' type=\'range\' name=\'rangeInput\' min=\'0\' max=\''+tits+'\' onchange=\'updateTextInput(this.value);\'> <input type=\'text\' id=\'textInput\' disabled value=\'\'>';   
    }
    
    $('#inputPlacer').html(toHtml);     
}







function debugOnOff(){
    
    var sign=document.getElementById("DebugButton").value;
    
    if(sign=='Debug Off'){
        $('#DebugButton').val('Debug On');
        $('#DebugButton').css("background-color","darkgreen");
        flagOut = 0;
        socket.emit('DebugOn');
    }else{
        $('#DebugButton').val('Debug Off');
        $('#DebugButton').css("background-color","darkred");
        flagOut = 999;
        socket.emit('DebugOff');
    }
}

function sandEmit(idPleyer, cardId, fromOpp){
    
    var toOpp = document.getElementById("whereTo").value;
    
    if (toOpp == null) {
        alert("Select a function!");
        return;
    }
    var numCard = null;
    var numPlace = null;
    
    if ((fromOpp == "L") || (fromOpp == "G")){
        var res = cardId.split("_");
        cardId = res[0] ;
        numCard = res[1];
        console.log(numCard);
    }
    
     if ((toOpp == "L") || (toOpp == "G")){
        numPlace = document.getElementById("valToPlace").value;
    }   
    
    
    var sendToNode = {"idPlayer":idPleyer,
                   "idCard" : cardId,
                   "from" : fromOpp,
                   "numCard" : numCard,
                   "toOpp" : toOpp,
                   "numPlace":numPlace};
    
    socket.emit('DebugSet', sendToNode);
    $('#theCardModal').css('display' , 'none');
    $('#theCardModal').html(" "); 
    
}

function modPlayer(idplayer){
    console.log('ciao');
    if (flagOut == 999) return; // flag di sdebug attivo (999 = non attivo)
    
    var dataLTPlayer = document.getElementById('dataLTPlayer'+idplayer).value;
    var dataLMPlayer = document.getElementById('dataLMPlayer'+idplayer).value;
    var poisonCounter = document.getElementById('poisonCounter'+idplayer).value;
    
    
    var sendToNode = {"idPlayer":idplayer,
                   "dataLTPlayer" : dataLTPlayer,
                   "dataLMPlayer" : dataLMPlayer,
                   "poisonCounter" : poisonCounter};
    
    socket.emit('DebugSetToPlayer', sendToNode);
    
}


function addManaCost(manaCost,idplayer){
	    var element = document.getElementById('dataLMPlayer'+idplayer);
	        
	    element.value = element.value + manaCost;
}





