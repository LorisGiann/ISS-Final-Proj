package unibo.webRobot;

import it.unibo.kactor.ApplMessage;
import it.unibo.kactor.IApplMessage;
import unibo.comm22.ApplMsgHandler;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.utils.ColorsOut;

public class TcpHandler extends ApplMsgHandler {

    public TcpHandler(String name) {
        super(name);
    }

    @Override
    public void elaborate(String message, Interaction2021 conn) {
        IApplMessage msg = new ApplMessage(message);
        elaborate(msg,conn);
    }

    @Override
    public void elaborate(IApplMessage iApplMessage, Interaction2021 interaction2021) {
        ColorsOut.outappl("TcpHandler | receive message "+iApplMessage.msgContent(),ColorsOut.YELLOW);
        if( iApplMessage.isEvent() && iApplMessage.msgContent().equals("positionguiupdate")) {

        }
    }
}