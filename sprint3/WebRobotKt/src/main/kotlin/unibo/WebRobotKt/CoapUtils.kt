package unibo.WebRobotKt

import alice.tuprolog.Prolog
import alice.tuprolog.Theory
import org.eclipse.californium.core.CoapHandler
import unibo.comm22.utils.ColorsOut
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

object CoapUtils {
    private val pengine : Prolog = Prolog()
    init {
        loadTheory("demo0.pl")
        loadTheory("sysRules.pl")
    }

    /*fun initCoapObserver() {
        try {
            //CommUtils.delay(1000)
            coapObsserve("mover",positionObserver)
            coapObsserve("transporttrolleystate",trasportTrolleyObserver)
            coapObsserve("wasteservice",containerObserver)
            coapObsserve("led",ledObserver)
            ColorsOut.out("Initialized handler!", ColorsOut.BLUE)
        }catch (e: Exception){
            System.err.println(e.stackTrace)
        }
    }*/


    fun coapObsserve(actor : String, observer: CoapHandler): Future<unibo.WebRobotKt.CoapConnection?> {
        val ctx : String  = solve("qactor( $actor, CTX, CLASS)","CTX")!!
        val ctxHost : String  = solve("getCtxHost($ctx,H)","H")!!
        val ctxPort : String = solve("getCtxPort($ctx,P)","P")!!
        return startCoapConnection(ctxHost, ctxPort, ctx, actor, observer)
    }

    private fun startCoapConnection(addr: String, port: String, context: String, actor: String, observer: CoapHandler): Future<CoapConnection?> {
        val executor = Executors.newSingleThreadExecutor()
        val futureClient : Future<CoapConnection?> = executor.submit(
            Callable<CoapConnection?> {
                var retry = false
                var attempt = 0
                var conn : CoapConnection? = null
                do {
                    Timer().schedule(1000){}
                    retry = false
                    attempt++
                    try {
                        conn = CoapConnection(addr + ":" + port, context + "/" + actor)
                        conn.observeResource(observer)
                        var i = 0;
                        while (/*i<10 && */conn.request("") == "0") {
                            ColorsOut.outappl(
                                "waiting for Coap conn to $actor (conn: $conn, attempt $i)",
                                ColorsOut.CYAN
                            )
                            Timer().schedule(1000) {}
                            i++
                        }
                        ColorsOut.outappl(
                            "coap connected to: ${addr + ":" + port}/${context + "/" + actor}",
                            ColorsOut.BLUE
                        )
                    } catch (e: Exception) {
                        System.out.println("COAP CONNECTION ERROR:")
                        e.printStackTrace()
                        if (attempt<15){
                            retry = true
                            conn = null
                        }
                    }
                    if(retry) TimeUnit.SECONDS.sleep(1)
                }while (retry)
                return@Callable conn
            }
        )
        return futureClient
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