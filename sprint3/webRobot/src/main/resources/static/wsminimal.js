/*
wsminimal.js
*/

var socket;

const messageArea = document.getElementById("messageArea");

function addMessageToWindow(message) {
     var output = message.replace("\n","<br/>")
      messageArea.innerHTML += `<div>${output}</div>`
}

function sendMessage(message) {
    var jsonMsg = JSON.stringify( {'name': message});
    socket.send(jsonMsg);
    console.log("Sent Message: " + jsonMsg);
}

function updateInfoConsole(message) {
	//console.log(message);
    //console.log("update");
    //var str = replaceAll(message, "&quot;", '"');
    var obj = JSON.parse(message);

    document.getElementById("statett").innerHTML = obj.statett;
    document.getElementById("stateled").innerHTML = obj.stateled;
    document.getElementById("position").innerHTML = obj.position;
    document.getElementById("pb").innerHTML = obj.pb;
    document.getElementById("gb").innerHTML = obj.gb;

}

function connect(){
  var host     = document.location.host;
  var pathname =  document.location.pathname;
  var addr     = "ws://" +host + pathname + "socket"  ;
    alert("connect addr=" + addr   );
  // Assicura che sia aperta un unica connessione
  if(socket!==undefined && socket.readyState!==WebSocket.CLOSED){
    alert("WARNING: Connessione WebSocket già stabilita");
  }
  var socket = new WebSocket(addr); //CONNESSIONE

  socket.onopen = function (event) {
    alert("connected")
    //addMessageToWindow("Connected");
  };
  socket.onmessage = function (event) { //RICEZIONE
    console.log("ws-status:" + `${event.data}`);
    msg = event.data;
    addMessageToWindow(msg);
    updateInfoConsole(msg)
  };
  return socket;
}//connect

