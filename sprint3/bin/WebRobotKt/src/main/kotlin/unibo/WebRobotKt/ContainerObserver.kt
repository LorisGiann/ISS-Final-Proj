package unibo.WebRobotKt

import alice.tuprolog.Struct
import alice.tuprolog.Term
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import unibo.comm22.utils.ColorsOut

class ContainerObserver (private val webSocketList: ArrayList<WebSocketSession>, updateGuiG : UpdateGui) : CoapHandler {
    var pb = "0.0"
    var gb = "0.0"
    var updateGui=updateGuiG


    override fun onLoad(response: CoapResponse) {
        val content = response.responseText
        if (content.isNotBlank() && !(content.contains("ActorBasic(Resource)") && content.contains("created"))) {
            ColorsOut.outappl("ContainerObserver | content ${content}", ColorsOut.GREEN)
            try {
                val term = (Term.createTerm(content) as Struct)
                pb = term.getArg(1).toString()
                gb = term.getArg(2).toString()
                ColorsOut.outappl(
                    "ContainerObserver | content ${content}, plasticbox ${pb}, glassbox ${gb}",
                    ColorsOut.GREEN
                )
                synchronized(updateGui) {
                    updateGui.pb = pb
                    updateGui.gb = gb
                }

                var json = updateGui.toString();
                for (webSocket in webSocketList) {
                    synchronized(webSocket) {
                        webSocket.sendMessage(TextMessage("${json}"))
                    }
                }
            } catch (e: Exception) {
                System.err.println("Errore lettura coap container : " + e.toString())
            }
        } else return
    }

    override fun onError() {
        ColorsOut.outerr("error observe container")
    }
}
