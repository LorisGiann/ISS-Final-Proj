package Robots.common;
/*
import unibo.actor22comm.coap.CoapConnection;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.tcp.TcpClientSupport;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
*/
import unibo.comm22.coap.CoapConnection;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.tcp.TcpClientSupport;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommSystemConfig;
import unibo.webRobot.CoapObserver;

public class RobotUtils {
    public static final int ctxtserverport           = 8095;
    private static Interaction2021 conn;


    public static void connectWithRobotUsingTcp(String addr){
         try {
             CommSystemConfig.tracing = true;
             conn = TcpClientSupport.connect(addr, ctxtserverport, 10);
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
