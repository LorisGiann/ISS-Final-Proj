package unibo.WebRobotKt

import alice.tuprolog.Struct
import alice.tuprolog.Term
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import unibo.WebRobotKt.CoapUtils.coapObsserve
import unibo.comm22.utils.ColorsOut
import java.util.concurrent.Future

class TrasportTrolleyObserver (private val webSocketList: ArrayList<WebSocketSession>, updateGuiG : UpdateGui) : CoapHandler {
    var statett = "wait"
    var updateGui=updateGuiG
    var futureClient : Future<CoapConnection?>

    init{
        futureClient = coapObsserve("transporttrolleystate",this)
    }

    override fun onLoad(response: CoapResponse) {
        val content = response.responseText
        if (content.isBlank()){
            reconnect()
        }
        if (content.isNotBlank() && !(content.contains("ActorBasic(Resource)") && content.contains("created"))) {
            ColorsOut.outappl("TrasportTrolleyObserver | content ${content}", ColorsOut.GREEN)
            try {
                val term = (Term.createTerm(content) as Struct)
                statett = term.getArg(0).toString()
                //ColorsOut.outappl("TrasportTrolleyObserver | content ${content}, state transporttrolley ${statett}",ColorsOut.GREEN)
                //updateGui.statett = statett;
                synchronized(updateGui) {
                    updateGui.statett = statett
                }

                var json = updateGui.toString();
                for (webSocket in webSocketList) {
                    synchronized(webSocket) {
                        webSocket.sendMessage(TextMessage("${json}"))
                    }
                }
            } catch (e: Exception) {
                System.err.println("ERRORE lettura coap transporttrolley: ")
                e.printStackTrace()
            }
        } else return
    }

    override fun onError() {
        ColorsOut.outerr("error observe trasporttrolley")
        reconnect()
    }

    fun reconnect(){
        ColorsOut.outappl("TrasportTrolleyObserver | RECONNECTING to transporttrolleystate", ColorsOut.GREEN)
        futureClient.get()?.removeObserve()
        futureClient = CoapUtils.coapObsserve("transporttrolleystate",this)
    }

    fun updatePosition(position : String){
        synchronized(updateGui) {
            updateGui.position = position;
            var json = updateGui.toString();
            for (webSocket in webSocketList) {
                webSocket.sendMessage(TextMessage("${json}"))
            }
        }
    }
}
