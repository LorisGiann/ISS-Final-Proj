package unibo.WebRobotKt

import alice.tuprolog.Prolog
import alice.tuprolog.Theory
import org.eclipse.californium.core.CoapHandler
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import unibo.comm22.coap.CoapConnection
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommUtils
import unibo.comm22.utils.CommUtils.delay
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

@Component
class WebSocketHandler : TextWebSocketHandler() {
    private val sessions= ArrayList<WebSocketSession>()
    private final var updateGui = UpdateGui();
    private final var positionObserver = PositionObserver(sessions,updateGui)
    private final var containerObserver = ContainerObserver(sessions,updateGui)
    private final var ledObserver = LedObserver(sessions,updateGui)
    private final var trasportTrolleyObserver = TrasportTrolleyObserver(sessions,updateGui)

    /*init {
        initCoapObserver()
    }*/

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


}
