package unibo.WebRobotKt

import alice.tuprolog.Struct
import alice.tuprolog.Term
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import unibo.WebRobotKt.CoapUtils.coapObsserve
import unibo.comm22.utils.ColorsOut
import java.util.concurrent.Future

class PositionObserver (private val webSocketList: ArrayList<WebSocketSession>, updateGuiG : UpdateGui) : CoapHandler {
    var position = "home"
    var updateGui=updateGuiG
    var futureClient : Future<CoapConnection?>

    init{
        futureClient = coapObsserve("mover",this)
    }

    override fun onLoad(response: CoapResponse) {
        val content = response.responseText
        if (content.isBlank()){
            reconnect()
        }
        if (content.isNotBlank() && !(content.contains("ActorBasic(Resource)") && content.contains("created"))) {
            ColorsOut.outappl("PositionObserver | content ${content}", ColorsOut.GREEN)
            try {
                val term = (Term.createTerm(content) as Struct)
                position = term.getArg(1).toString()
                //ColorsOut.outappl("PositionObserver | content ${content}, position ${position}", ColorsOut.GREEN)
                updatePosition(position)
            } catch (e: Exception) {
                System.err.println("ERRORE lettura coap position: ")
                e.printStackTrace()
            }
        }else return
    }

    override fun onError() {
        ColorsOut.outerr("error observe position")
        reconnect()
    }

    private fun reconnect(){
        ColorsOut.outappl("PositionObserver | RECONNECTING to mover", ColorsOut.GREEN)
        futureClient.get()?.removeObserve()
        futureClient = CoapUtils.coapObsserve("mover",this)
    }

    private fun updatePosition(position : String){
        synchronized(updateGui) {
            updateGui.position = position;
            var json = updateGui.toString();
            for (webSocket in webSocketList) {
                webSocket.sendMessage(TextMessage("${json}"))
            }
        }
    }
}
