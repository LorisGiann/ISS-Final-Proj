package unibo.webRobot;

import org.json.JSONObject;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import Robots.common.IWsHandler;
import unibo.comm22.utils.ColorsOut;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/*
In Spring we can create a customized handler by extends abstract class
AbstractWebSocketHandler or one of it's subclass,
either TextWebSocketHandler or BinaryWebSocketHandler:
 */
public class WebSocketHandler extends AbstractWebSocketHandler implements IWsHandler {
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        ColorsOut.out("WebSocketHandler | Added the session:" + session, ColorsOut.MAGENTA);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        ColorsOut.out("WebSocketHandler | Removed the session:" + session, ColorsOut.MAGENTA);
        super.afterConnectionClosed(session, status);
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String stringMessage = message.getPayload();
        ColorsOut.outappl("WebSocketHandler | handleTextMessage Received: " + stringMessage, ColorsOut.GREEN);
        //Gestione di comandi remoti via Ws come RobotController
        //sendToAll("webRobot WebSocketHandler echo: "+cmd);
        try {
            JSONObject json = new JSONObject(stringMessage);
            String info = json.getString("info");
            ColorsOut.outappl("WebSocketHandler | handleTextMessage info: " + info, ColorsOut.GREEN);
        } catch (Exception e) {
            ColorsOut.outerr("WebSocketHandler | handleTextMessage ERROR:"+e.getMessage());
        }

    }
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        System.out.println("WebSocketHandler | handleBinaryMessage Received " );
        //session.sendMessage(message);
        //Send to all the connected clients
        Iterator<WebSocketSession> iter = sessions.iterator();
        while( iter.hasNext() ){
            iter.next().sendMessage(message);
        }
    }

    public void sendToAll(String message)  {
        try{
            ColorsOut.outappl("WebSocketHandler | sendToAll String: " + message, ColorsOut.CYAN);
            //JSONObject jsm = new JSONObject(message);
            //IApplMessage mm = new ApplMessage(message);
            //String mstr    = mm.msgContent();//.replace("'","");
            sendToAll( new TextMessage(message)) ;
        }catch( Exception e ){
            ColorsOut.outerr("WebSocketHandler | sendToAll String ERROR:"+e.getMessage());
        }
    }
    public void sendToAll(TextMessage message) {
        //ColorsOut.outappl("WebSocketHandler | sendToAll " + message.getPayload() + " TextMessage sessions:" + sessions.size(), ColorsOut.CYAN);
        Iterator<WebSocketSession> iter = sessions.iterator();
        while( iter.hasNext() ){
            try{
                WebSocketSession session = iter.next();
                ColorsOut.outappl("WebSocketHandler | sendToAll " +
                        message.getPayload() + " for session " + session.getRemoteAddress() , ColorsOut.MAGENTA);
                //synchronized(session){
                    session.sendMessage(message);
                //}
            }catch(Exception e){
                ColorsOut.outerr("WebSocketHandler | TextMessage ERROR:"+e.getMessage());
            }
        }
    }

}
