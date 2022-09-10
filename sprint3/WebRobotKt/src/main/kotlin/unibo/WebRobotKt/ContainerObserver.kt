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

class ContainerObserver (private val webSocketList: ArrayList<WebSocketSession>, updateGuiG : UpdateGui) : CoapHandler {
    var pb = "0.0"
    var gb = "0.0"
    var updateGui=updateGuiG
    var futureClient : Future<CoapConnection?>

    init{
        futureClient = coapObsserve("wasteservice",this)
    }

    override fun onLoad(response: CoapResponse) {
        val content = response.responseText
        if (content.isBlank()){
            reconnect()
        }
        if (content.isNotBlank() && !(content.contains("ActorBasic(Resource)") && content.contains("created"))) {
            ColorsOut.outappl("ContainerObserver | content ${content}", ColorsOut.GREEN)
            try {
                val term = (Term.createTerm(content) as Struct)
                pb = term.getArg(1).toString()
                gb = term.getArg(2).toString()
                //ColorsOut.outappl("ContainerObserver | content ${content}, plasticbox ${pb}, glassbox ${gb}",ColorsOut.GREEN)
                updateContainer(pb,gb)
            } catch (e: Exception) {
                System.err.println("ERRORE lettura coap container: ")
                e.printStackTrace()
                reconnect()
            }
        } else return
    }

    private fun reconnect(){
        ColorsOut.outappl("ContainerObserver | RECONNECTING to wasteservice", ColorsOut.GREEN)
        futureClient.get()?.removeObserve()
        futureClient = CoapUtils.coapObsserve("wasteservice",this)
    }

    override fun onError() {
        ColorsOut.outerr("error observe container")
    }

    private fun updateContainer(pb : String, gb : String){
        synchronized(updateGui) {
            updateGui.pb = pb
            updateGui.gb = gb
            var json = updateGui.toString();
            for (webSocket in webSocketList) {
                webSocket.sendMessage(TextMessage("${json}"))
            }
        }
    }
}
