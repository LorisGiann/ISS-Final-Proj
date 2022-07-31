package testSprint0;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import unibo.comm22.utils.ColorsOut;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestObserver implements CoapHandler{
    protected List<String> history = new ArrayList<String>();

    @Override
    public synchronized void onLoad(CoapResponse response) {
        history.add(response.getResponseText());
        ColorsOut.outappl("history=" + history, ColorsOut.MAGENTA);
    }

    public List<String> getHistory(){
        return history;
    }

    public String getIndexHistory(int i){
        return history.get(i);
    }

    /*
    Check wether or not the content of the history list at index i matches the specified pattern
     */
    public boolean checkContentAtIndex(int index, String pattern){
        return checkContentAtIndex(index, Pattern.compile(pattern));
    }
    public boolean checkContentAtIndex(int index, Pattern pattern){
        Matcher matcher = pattern.matcher(history.get(index));
        return matcher.matches();
    }



    private int nextCheckIndex = 0;
    /**
    Sequentially check the presence of events in the hystory
    The presence of the specified pattern is checked only on the events following the last found one.
    @Return the index of the found occurrence, -1 if no occurrence is found
     */
    public int checkNextContent(String patternstr){
        patternstr = patternstr.replace("(","\\(").replace(")","\\)").replace("*",".*");
        Pattern pattern = Pattern.compile(patternstr);
        int i = nextCheckIndex;
        while(i < history.size()){
            if(checkContentAtIndex(i, pattern)){
                nextCheckIndex = i+1;
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * calls repeatedly checkNextContent() to see if the sequence is respected
     * @Return true if is respected, false if not
     */
    public boolean checkNextSequence(String[] patternstr){
        for(String s : patternstr){
            if(checkNextContent(s) < 0){
                ColorsOut.outappl("Element \"" + s + "\" not found!", ColorsOut.RED);
                return false;
            }
        }
        return true;
    }

    /**
     * check for the presence of all the specified patterns at once. Return the index of the last pattern that still was to be found
     * The order between the elements is not relevant
     * @param patternstr
     * @return
     */
    public int checkNextContents(String[] patternstr) {
        List<Pattern> patterns = new ArrayList<Pattern>(patternstr.length);
        for (int i = 0; i < patternstr.length; i++) {
            String single_patternstr = patternstr[i].replace("(", "\\(").replace(")", "\\)").replace("*", ".*");
            patterns.add(Pattern.compile(single_patternstr));
        }
        return checkNextContents(patterns);
    }
    public int checkNextContents(List<Pattern> patterns){
        int i = nextCheckIndex;
        while(i < history.size()){
            boolean found = false;
            Pattern foundPattern = null;
            for(Pattern pattern : patterns){
                if(checkContentAtIndex(i, pattern)){
                    found=true;
                    foundPattern = pattern;
                    continue;
                }
            }
            if(found) patterns.remove(foundPattern);
            if(patterns.size()==0){
                nextCheckIndex = i+1;
                return i;
            }
            i++;
        }
        return -1;
    }
    /***
     * set the starting search point for checkNextContent() (set to zero to start from the beginning)
     */
    public void setStartPosition(int val){
        nextCheckIndex = val;
    }

    @Override
    public void onError() {
        ColorsOut.outerr("CoapObserver observe error!");
    }

    /*System.out.println(to.checkNextContent("transporttrolley\\(forward_robot,.*"));
    System.out.println(to.checkNextContent("transporttrolley\\(wait,HOME,HOME\\)"));
    to.setStartPosition(0);
    System.out.println(to.checkNextContents(new String[]{"transporttrolley\\(forward_robot,.*", "transporttrolley\\(wait,HOME,HOME\\)"}));*/

}
