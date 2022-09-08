package unibo.WebRobotKt

import alice.tuprolog.Struct
import alice.tuprolog.Term
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import unibo.comm22.utils.ColorsOut

class LedObserver (private val webSocketList: ArrayList<WebSocketSession>, updateGuiG : UpdateGui) : CoapHandler {
    var ledState = "OFF"
    var updateGui=updateGuiG


    override fun onLoad(response: CoapResponse) {
        val content = response.responseText
        if (content.isNotBlank() && !(content.contains("ActorBasic(Resource)") && content.contains("created"))) {
            ColorsOut.outappl("LedObserver | content ${content}", ColorsOut.GREEN)
            try {
                val term = (Term.createTerm(content) as Struct)
                ledState = term.getArg(1).toString()
                ColorsOut.outappl("LedObserver | content ${content}, ledstate ${ledState}", ColorsOut.GREEN)
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
                System.err.println("Errore lettura coap led : " + e.toString())
            }
        } else return
    }

    override fun onError() {
        ColorsOut.outerr("error observe led")
    }
}
