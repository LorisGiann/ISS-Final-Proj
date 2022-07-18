package testSprint1;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import unibo.comm22.utils.ColorsOut;

import java.util.ArrayList;
import java.util.List;

public class TestObserver implements CoapHandler{
protected List<String> history = new ArrayList<String>();

    @Override
    public synchronized void onLoad(CoapResponse response) {
        history.add(response.getResponseText());
        ColorsOut.outappl("TrolleyPosObserver history=" + history, ColorsOut.MAGENTA);
    }

    public String getHistory(){
        return history.toString();
    }

    public String getIndexHistory(int i){
        return history.get(i);
    }

    @Override
    public void onError() {
        ColorsOut.outerr("CoapObserver observe error!");
    }


}
