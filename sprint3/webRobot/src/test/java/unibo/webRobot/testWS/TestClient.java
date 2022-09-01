package unibo.webRobot.testWS;

import unibo.webRobot.UpdateGui;

import java.net.URI;
import java.net.URISyntaxException;

public class TestClient {

    public static void main(String[] args) throws URISyntaxException, InterruptedException {

        // 1) open websocket
        WebsocketClientEndpoint clientEndPoint =  new WebsocketClientEndpoint( new URI("ws://localhost:8089/socket"));

        // 2) add listener
        clientEndPoint.addMessageHandler(new IMessageHandler() {
            public void handleMessage(String message) {
                System.out.println("handle message: "+message);
            }
        });

        // 3) send message to websocket
        UpdateGui test= new UpdateGui("WAIT","ON","HOME","10","10");
        //Gson gson = new Gson();
        //String userJson = gson.toJson(test);
        //String json = "{\"statett\":\"WAIT\",\"stateled\":\"ON\",\"position\":\"HOME\",\"pb\":\"10\",\"gb\":\"10\"}";
        clientEndPoint.sendMessage(test.toString());
        //clientEndPoint.sendMessage("OK");
        // wait for messages from websocket
        Thread.sleep(3000);
        //json = "{\"statett\":\"MOVE\",\"stateled\":\"BLINK\",\"position\":\"HOME\",\"pb\":\"5\",\"gb\":\"5\"}";
        //clientEndPoint.sendMessage(json);
    }
}