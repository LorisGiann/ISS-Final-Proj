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

    private val pengine : Prolog = Prolog()

    init {
        initCoapObserver()
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





    fun initCoapObserver() {
        try {
            //CommUtils.delay(1000)
            loadTheory("demo0.pl")
            loadTheory("sysRules.pl")
            coapObsserve("mover",positionObserver)
            coapObsserve("transporttrolleystate",trasportTrolleyObserver)
            coapObsserve("wasteservice",containerObserver)
            coapObsserve("led",ledObserver)
            ColorsOut.out("Initialized handler!", ColorsOut.BLUE)
        }catch (e: Exception){
            System.err.println(e.stackTrace)
        }
    }


    fun coapObsserve(actor : String, observer: CoapHandler){
        val ctx : String  = solve("qactor( $actor, CTX, CLASS)","CTX")!!
        val ctxHost : String  = solve("getCtxHost($ctx,H)","H")!!
        val ctxPort : String = solve("getCtxPort($ctx,P)","P")!!
        startCoapConnection(ctxHost, ctxPort, ctx, actor, observer)
    }

    private fun startCoapConnection(addr: String, port: String, context: String, actor: String, observer: CoapHandler) {
        Thread {
            val conn = CoapConnection(addr+ ":" + port,context + "/" + actor)
            conn.observeResource(observer)
            var i=0;
            while (/*i<10 && */conn.request("") == "0") {
                ColorsOut.outappl("waiting for Coap conn to $actor (conn: $conn, attempt $i)", ColorsOut.CYAN)
                Timer().schedule(1000){}
                i++
            }
            ColorsOut.outappl("coap connected to: ${addr + ":" + port}/${context + "/" + actor}", ColorsOut.BLUE)
        }.start()
    }


    fun solve( goal: String, resVar: String  ) : String? {
        //println("sysUtil  | solveGoal ${goal}" );
        val sol = pengine.solve( "$goal.")
        if( sol.isSuccess ) {
            if( resVar.length == 0 ) return "success"
            val result = sol.getVarValue(resVar)  //Term
            var resStr = result.toString()
            return  strCleaned( resStr )
        }
        else return null
    }

    fun loadTheory( path: String ) {
        try {
            val worldTh = Theory( FileInputStream(path) )
            pengine.addTheory(worldTh)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun strCleaned( s : String) : String{
        if( s.startsWith("'")) return s.replace("'","")
        else return s
    }

}
