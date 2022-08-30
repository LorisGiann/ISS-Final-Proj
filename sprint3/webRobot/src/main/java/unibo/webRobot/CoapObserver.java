package unibo.webRobot;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import unibo.comm22.utils.ColorsOut;

public class CoapObserver implements CoapHandler{

    @Override
    public void onLoad(CoapResponse response) {
        ColorsOut.outappl("Info changed!",ColorsOut.GREEN);
        ColorsOut.outappl(response.getResponseText(),ColorsOut.GREEN);
        //ColorsOut.outappl("Position changed!" + response.getResponseText(), ColorsOut.GREEN);
        //send info over the websocket
        //WebSocketConfiguration.wshandler.sendToAll("" + response.getResponseText());
    }

    @Override
    public void onError() {
        ColorsOut.outerr("CoapObserver observe error!");
    }
}
