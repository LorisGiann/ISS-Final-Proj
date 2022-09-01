package Robots.common;
/*
import unibo.actor22comm.coap.CoapConnection;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.tcp.TcpClientSupport;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
*/
import it.unibo.kactor.ApplMessage;
import it.unibo.kactor.IApplMessage;
import unibo.comm22.coap.CoapConnection;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.tcp.TcpClientSupport;
import unibo.comm22.tcp.TcpConnection;
import unibo.comm22.tcp.TcpServer;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommSystemConfig;
import unibo.webRobot.CoapObserver;
import unibo.webRobot.TcpHandler;

import java.net.Socket;

public class RobotUtils {
    public static final int ctxtserverport           = 8095;

    //public static TcpServer server;
    private static Interaction2021 conn;

    public static void connectWithRobotUsingTcp(String addr){
         try {
             CommSystemConfig.tracing = true;
             // server = new TcpServer("tcpConnection",ctxtserverport,new TcpHandler("eventHandler"));
             // server.activate();

             conn =  TcpClientSupport.connect("localhost",ctxtserverport,100);//1
             ColorsOut.outappl("RobotUtils | connection establish "+addr+":"+ctxtserverport, ColorsOut.CYAN);
             String strmsg = conn.receiveMsg();
             ColorsOut.outappl("RobotUtils | message received: "+strmsg, ColorsOut.CYAN);
             IApplMessage msg = new ApplMessage(strmsg);
             if (msg.isEvent()){
                 String id = msg.msgId();
                 String content = msg.msgContent();
                 ColorsOut.outappl("RobotUtils | receive event "+id+" "+content, ColorsOut.CYAN);
             }


             ColorsOut.out("RobotUtils | connect Tcp conn:" + conn );
             ColorsOut.outappl("RobotUtils | connect Tcp conn:" + conn , ColorsOut.CYAN);
             //sendMsg("basicrobot", "r");
         }catch(Exception e){
            ColorsOut.outerr("RobotUtils | connectWithRobotUsingTcp ERROR:"+e.getMessage());
        }
    }
    public static CoapConnection connectUsingCoap(String addr){
        try {
            CommSystemConfig.tracing = true;
            String ctxqakdest       = "ctxserver";
            String qakdestination 	= "guiserver";
            String path   = ctxqakdest+"/"+qakdestination;
            conn           = new CoapConnection(addr+":"+ctxtserverport, path);
            ((CoapConnection)conn).observeResource( new CoapObserver() );
            ColorsOut.out("RobotUtils | connect Tcp conn:" + conn );
            ColorsOut.outappl("RobotUtils | connect Coap conn:" + conn , ColorsOut.CYAN);
        }catch(Exception e){
            ColorsOut.outerr("RobotUtils | connectWithRobotUsingTcp ERROR:"+e.getMessage());
        }
        return (CoapConnection) conn;
    }


}
