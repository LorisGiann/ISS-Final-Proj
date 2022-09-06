/*
ioutils.js
*/

const infoDisplay     = document.getElementById("display");
const messageArea = document.getElementById("messageArea");

function setMessageToWindow(outfield, message) {
     var output = message.replace("\n","<br/>")
     outfield.innerHTML = `<tt>${output}</tt>`
}

function addMessageToWindow(message) {
     var output = message.replace("\n","<br/>")
      messageArea.innerHTML += `<div>${output}</div>`
}


function callServerUsingAjax(message) {
    //alert("callServerUsingAjax "+message)
    $.ajax({
     //imposto il tipo di invio dati (GET O POST)
      type: "POST",
      //Dove  inviare i dati
      url: "robotmove",
      //Dati da inviare
      data: "info=" + message,
      dataType: "html",
      //Visualizzazione risultato o errori
      success: function(msg){  //msg ha tutta la pagina ...
        //console.log("call msg="+msg);
        setMessageToWindow(infoDisplay,message+" done")
      },
      error: function(){
        alert("Chiamata fallita, si prega di riprovare..."); 
        //sempre meglio impostare una callback in caso di fallimento
      }
     });
}

