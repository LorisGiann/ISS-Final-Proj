var stompClient = null;
var hostAddr = "http://localhost:50850/move";


function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);

}

function connect() {
    var socket = new SockJS('/it-unibo-iss');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        stompClient.subscribe('/topic/display-tearoomstatemanager', function (msg) {
            updateStatemanager(JSON.parse(msg.body).content);
        });
        stompClient.subscribe('/topic/display-smartbell', function (msg) {
            updateClient(JSON.parse(msg.body).content);
        });
        stompClient.subscribe('/topic/display-maxstaytime', function (msg) {
            kickClient(JSON.parse(msg.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}



