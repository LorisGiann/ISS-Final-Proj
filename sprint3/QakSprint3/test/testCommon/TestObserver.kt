package testCommon

import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import unibo.comm22.coap.CoapConnection
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommUtils
import java.util.ArrayList
import java.util.concurrent.locks.ReentrantLock
import java.util.regex.Pattern
import kotlin.concurrent.withLock

class TestObserver : CoapHandler {
    // ------------------------------------------------- COAP CONNECTION PART ----------------------------------------------------
    data class coapConnAddrPath(val address:String, val path:String){}

    companion object {
        // ------------------------------ ACTORS DEFINITIONS --------------------------
        val ctxMap = mapOf(
            "ctxserver" to Pair<String,Int>("localhost",8095),
            "ctxrobot" to Pair<String,Int>("127.0.0.1",8096),
            "ctxalarm" to Pair<String,Int>("127.0.0.1",8097),
        )
        val actorMap = mapOf(
            "wasteservice" to "ctxserver",
            "depositaction" to "ctxserver",
            "transporttrolley" to "ctxrobot",
            "transporttrolleystate" to "ctxrobot",
            "mover" to "ctxrobot",
            "moveruturn" to "ctxrobot",
            "mover180turn" to "ctxrobot",
            "pickupdropouthandler" to "ctxrobot",
            "basicrobotlorisdavide" to "ctxrobot",
            "commandissuerfortests" to "ctxrobot",
            "basicrobotwrapper" to "ctxrobot",
            "alarmreceivertest" to "ctxrobot",
            "led" to "ctxalarm",
            "sonarlorisdavide" to "ctxalarm",
        )
        // ----------------------------------------------------------------------------

        fun getCoapConnAddrPath(actorName : String) : coapConnAddrPath {
            val ctx = actorMap[actorName]
            if(ctx==null) throw Exception("not known context for actor '$actorName'")
            if(ctxMap[ctx]==null) throw Exception("not known address for context '$ctx'")
            return coapConnAddrPath("${ctxMap[ctx]!!.first}:${ctxMap[ctx]!!.second}", "$ctx/$actorName")
        }
    }

    private val establishedConnMap = HashMap<String, CoapConnection>()
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    /*
    Creates the connection and wait until it's established
     */
    fun establishCoapConn(actorName : String) {
        val addrPath = getCoapConnAddrPath(actorName)
        val conn = CoapConnection(addrPath.address, addrPath.path)
        conn.observeResource(this)
        ColorsOut.outappl("connecting via Coap to $actorName (conn: $conn)", ColorsOut.CYAN)
        while (conn.request("") == "0") {
            ColorsOut.outappl("waiting for Coap conn to $actorName (conn: $conn)", ColorsOut.CYAN)
            CommUtils.delay(100)
        }
        establishedConnMap[actorName] = conn
    }

    fun getCurrentCoapState(actorName: String): String {
        val conn = establishedConnMap[actorName]
        if(conn == null) throw Exception("The actor '$actorName' doesn't exist or is not connected")
        val answer = conn.request("")
        ColorsOut.outappl("actor $actorName current state is: $answer", ColorsOut.CYAN)
        return answer
    }

    /**
     * waits for an actor to reach a certain state
     * @param actorName name of the actor to observe
     * @param stateContent look for the presence of this string inside the new state. If it's present the desired state is considered reached and the method returns
     */
    fun waitUntilState(actorName: String, stateContent: String): String {
        lateinit var last : String
        //ColorsOut.outappl("waiting for $actorName($stateContent", ColorsOut.RED)
        lock.withLock {
            try {
                last = history.last { it.startsWith("$actorName(") }
                //ColorsOut.outappl("Last found:  '$last'", ColorsOut.CYAN)
            }catch (_ : NoSuchElementException){
                last = getCurrentCoapState(actorName)
            }
            if (last.startsWith("$actorName(") && last.contains(stateContent)) return last //automatically release the lock

            var lastCheckedIndex = history.size
            do{
                if(last.startsWith("$actorName(")) ColorsOut.outappl("actor $actorName current state is: '$last', waiting for it to contain '$stateContent'", ColorsOut.CYAN)
                condition.await() //automatically release the lock
                last = checkFrom(lastCheckedIndex, actorName, stateContent)
                lastCheckedIndex = history.size
            }while(! (last.startsWith("$actorName(") && last.contains(stateContent)))
        }
        ColorsOut.outappl("actor $actorName current state is: '$last', matching with content '$stateContent'", ColorsOut.CYAN)
        return last
    }
    private fun checkFrom(lastCheckedIndex : Int, actorName: String, stateContent: String) : String {
        var i = lastCheckedIndex
        while(i < history.size){
            if(history[i].startsWith("$actorName(") && history[i].contains(stateContent)){
                return history[i]
            }
            i++
        }
        return history[i-1]
    }

    fun closeAllConnections(){
        establishedConnMap.forEach{
                actorName: String, conn: CoapConnection ->
            run {
                ColorsOut.outappl("Closing connection $conn to actor $actorName", ColorsOut.CYAN)
                conn.close()
            }
        }
    }

    fun closeConnection(actorName: String){
        val conn = establishedConnMap[actorName]
        if(conn == null) throw Exception("Actor $actorName does not exist, or connection was already closed")
        conn.close()
    }

    //---------------------------------------------------------------------------------------------------------------------------

    //--------------------------------------------- HISTORY RECORDING and ANALYSIS ----------------------------------------------

    private var history: MutableList<String> = ArrayList()

    fun debugSetHistory(history : MutableList<String>){
        this.history = history
    }
    fun clearHistory() {
        lock.withLock {
            history.clear()
            nextCheckIndex=0
        }
    }

    override fun onLoad(response: CoapResponse) {
        //ColorsOut.outappl("history+=" + response.responseText, ColorsOut.MAGENTA);
        lock.withLock {
            history.add(response.responseText)
            condition.signalAll()
        }
    }

    fun getHistory() : List<String>{
        lateinit var copy : List<String>
        lock.withLock {
            copy=history.toList()
        }
        return copy
    }

    /*
    Check wether or not the content of the history list at index i matches the specified pattern
     */
    fun checkContentAtIndex(index: Int, patternstrIn: String): Boolean {
        val patternstr = patternstrIn.replace("(", "\\(").replace(")", "\\)").replace("*", ".*")
        return checkContentAtIndex(index, Pattern.compile(patternstr))
    }

    fun checkContentAtIndex(index: Int, pattern: Pattern): Boolean {
        lock.withLock {
            val matcher = pattern.matcher(history[index])
            return matcher.matches()
        }
    }

    //start search from this index included onwards
    public var nextCheckIndex = 0

    /**
     * Sequentially check the presence of events in the hystory
     * The presence of the specified pattern is checked only on the events following the last found one.
     * @Return the index of the found occurrence, -1 if no occurrence is found
     */
    fun checkNextContent(patternstrIn: String): Int {
        val patternstr = patternstrIn.replace("(", "\\(").replace(")", "\\)").replace("*", ".*")
        val pattern = Pattern.compile(patternstr)
        var i = nextCheckIndex
        while (i < history.size) {
            if (checkContentAtIndex(i, pattern)) {
                nextCheckIndex = i + 1
                return i
            }
            i++
        }
        return -1
    }

    /**
     * calls repeatedly checkNextContent() to see if the sequence is respected
     * @Return true if is respected, false if not
     */
    fun checkNextSequence(patternstr: Array<String>): Boolean {
        for (s in patternstr) {
            if (checkNextContent(s) < 0) {
                ColorsOut.outappl("Element \"$s\" not found!", ColorsOut.RED)
                return false
            }
        }
        return true
    }

    /**
     * check for the presence of all the specified patterns at once.
     * The order between the elements is not relevant
     * @param patternstr the array of patterns to seach
     * @return the index of the last pattern that still was to be found. -1 if not all the elemest has been found
     */
    fun checkNextContents(patternstr: Array<String>): Int {
        val patterns: MutableList<Pattern> = ArrayList(patternstr.size)
        for (i in patternstr.indices) {
            val single_patternstr = patternstr[i].replace("(", "\\(").replace(")", "\\)").replace("*", ".*")
            patterns.add(Pattern.compile(single_patternstr))
        }
        return checkNextContents(patterns)
    }

    /**
     * check for the presence of all the specified patterns at once.
     * The order between the elements is not relevant
     * @param patterns the array of patterns to seach
     * @return the index of the last pattern that still was to be found. -1 if not all the elemest has been found
     */
    fun checkNextContents(patterns: MutableList<Pattern>): Int {
        var i = nextCheckIndex
        while (i < history.size) {
            var found = false
            lateinit var foundPattern: Pattern
            for (pattern in patterns) {
                if (checkContentAtIndex(i, pattern)) {
                    found = true
                    foundPattern = pattern
                    continue
                }
            }
            if (found) patterns.remove(foundPattern)
            if (patterns.size == 0) {
                nextCheckIndex = i + 1
                return i
            }
            i++
        }
        ColorsOut.outappl("Elements \"$patterns\" not found!", ColorsOut.RED)
        return -1
    }

    //---------------------------------------------------------------------------------------------------------------------------

    override fun onError() {
        ColorsOut.outerr("CoapObserver observe error!")
    }
}