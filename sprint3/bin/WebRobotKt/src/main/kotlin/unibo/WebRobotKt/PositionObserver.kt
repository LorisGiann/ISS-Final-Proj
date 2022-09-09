package unibo.WebRobotKt

import alice.tuprolog.Struct
import alice.tuprolog.Term
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import unibo.comm22.utils.ColorsOut

class PositionObserver (private val webSocketList: ArrayList<WebSocketSession>, updateGuiG : UpdateGui) : CoapHandler {
    var position = "home"
    var updateGui=updateGuiG


    override fun onLoad(response: CoapResponse) {
        val content = response.responseText
        if (content.isNotBlank() && !(content.contains("ActorBasic(Resource)") && content.contains("created"))) {
            ColorsOut.outappl("PositionObserver | content ${content}", ColorsOut.GREEN)
            try {
                val term = (Term.createTerm(content) as Struct)
                position = term.getArg(1).toString()
                ColorsOut.outappl("PositionObserver | content ${content}, position ${position}", ColorsOut.GREEN)
                synchronized(updateGui) {
                    updateGui.position = position;
                }

                var json = updateGui.toString();
                for (webSocket in webSocketList) {
                    synchronized(webSocket) {
                        webSocket.sendMessage(TextMessage("${json}"))
                    }
                }
            } catch (e: Exception) {
                System.err.println("Errore lettura coap position : " + e.toString())
            }
        }else return
    }

    override fun onError() {
        ColorsOut.outerr("error observe position")
    }
}
