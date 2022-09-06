package unibo.WebRobotKt

import org.eclipse.californium.core.CoapHandler
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import unibo.comm22.coap.CoapConnection
import unibo.comm22.utils.ColorsOut
import java.util.concurrent.CopyOnWriteArrayList


@Component
class WebSocketHandler : TextWebSocketHandler() {
    private val sessions= ArrayList<WebSocketSession>()
    private final var updateGui = UpdateGui();
    private final var positionObserver = PositionObserver(sessions,updateGui)
    private final var containerObserver = ContainerObserver(sessions,updateGui)
    private final var ledObserver = LedObserver(sessions,updateGui)
    private final var trasportTrolleyObserver = TrasportTrolleyObserver(sessions,updateGui)


    init {
        initCoapObserver()
    }

    fun initCoapObserver() {
        try {
            //CommUtils.delay(1000)
            startCoapConnection("127.0.0.1", "8096", "ctxrobot", "mover",positionObserver)
            startCoapConnection("127.0.0.1", "8096", "ctxrobot", "transporttrolley",trasportTrolleyObserver)
            startCoapConnection("127.0.0.1", "8095", "ctxserver", "wasteservice",containerObserver)
            startCoapConnection("127.0.0.1", "8097", "ctxalarm", "led",ledObserver)
            ColorsOut.out("Initialized handler!", ColorsOut.BLUE)
        }catch (e: Exception){
            System.err.println(e.stackTrace)
        }
    }



    public override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        println("WebSocketHandler | handleTextMessage Received: $message")
        //val cmd = message.payload
        session.sendMessage(message)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)
        sessions.add(session)
        ColorsOut.out("New session started", ColorsOut.CYAN)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        super.afterConnectionClosed(session, status)
        sessions.remove(session)
        ColorsOut.out("Session closed", ColorsOut.CYAN)
    }

    private fun startCoapConnection(addr: String, port: String, context: String, actor: String, observer: CoapHandler) {
        Thread {
            val conn = CoapConnection(addr+ ":" + port,context + "/" + actor)
            conn.observeResource(observer)
            ColorsOut.outappl("connected via Coap conn: ${addr + ":" + port}/${context + "/" + actor}", ColorsOut.BLUE)
        }.start()
    }
}
