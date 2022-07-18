package testSprint1;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import unibo.comm22.utils.ColorsOut;

import java.util.ArrayList;
import java.util.List;

public class TestObserver implements CoapHandler{
    protected List<String> history = new ArrayList<String>();
    protected List<String> historyPosition = new ArrayList<>();

    @Override
    public synchronized void onLoad(CoapResponse response) {
        if (response.getResponseText().contains("trolleyPos")){
            historyPosition.add(response.getResponseText());
        }
        history.add(response.getResponseText());
        ColorsOut.outappl("TrolleyPosObserver history=" + history, ColorsOut.MAGENTA);
    }

    public String getHistory(){
        return history.toString();
    }

    public String getIndexHistory(int i){
        return history.get(i);
    }

    public String getHistoryPosition(){
        return historyPosition.toString();
    }

    public String getIndexHistoryPosition(int i){
        return historyPosition.get(i);
    }

    @Override
    public void onError() {
        ColorsOut.outerr("CoapObserver observe error!");
    }


}
