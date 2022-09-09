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

class LedObserver (private val webSocketList: ArrayList<WebSocketSession>, updateGuiG : UpdateGui) : CoapHandler {
    var ledState = "OFF"
    var updateGui=updateGuiG
    var futureClient : Future<CoapConnection?>

    init{
        futureClient = coapObsserve("led",this)
    }

    override fun onLoad(response: CoapResponse) {
        val content = response.responseText
        if (content.isBlank()){
            reconnect()
        }
        if (content.isNotBlank() && !(content.contains("ActorBasic(Resource)") && content.contains("created"))) {
            ColorsOut.outappl("LedObserver | content ${content}", ColorsOut.GREEN)
            try {
                val term = (Term.createTerm(content) as Struct)
                ledState = term.getArg(1).toString()
                //ColorsOut.outappl("LedObserver | content ${content}, ledstate ${ledState}", ColorsOut.GREEN)
                synchronized(updateGui) {
                    updateGui.stateled = ledState
                }

                var json = updateGui.toString();
                for (webSocket in webSocketList) {
                    synchronized(webSocket) {
                        webSocket.sendMessage(TextMessage("${json}"))
                    }
                }
            } catch (e: Exception) {
                System.err.println("ERRORE lettura coap led: ")
                e.printStackTrace()
            }
        } else return
    }

    override fun onError() {
        ColorsOut.outerr("error observe led")
        reconnect()
    }

    fun reconnect(){
        ColorsOut.outappl("LedObserver | RECONNECTING to led", ColorsOut.GREEN)
        futureClient.get()?.removeObserve()
        futureClient = CoapUtils.coapObsserve("led",this)
    }
}
