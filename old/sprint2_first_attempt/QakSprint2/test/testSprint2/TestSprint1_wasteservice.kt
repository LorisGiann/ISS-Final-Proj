package testSprint2

import org.eclipse.californium.core.CoapHandler
import org.junit.Assert
import org.junit.jupiter.api.*
import testSprint2.TestUtils
import unibo.comm22.coap.CoapConnection
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.IOException
import java.time.Duration
import kotlin.test.Test

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestSprint1_wasteservice {
    private var connTransportTrolley: CoapConnection? = null
    private var connWasteService: CoapConnection? = null
    private var connMover: CoapConnection? = null
    private var to: TestObserver? = null
    private var processHandleServer: ProcessHandle? = null
    private var processHandleRobot: ProcessHandle? = null
    private var processHandleAlarm: ProcessHandle? = null
    private var prServer: Process? = null
    private var prRobot: Process? = null
    private var prAlarm: Process? = null

    companion object {
        @JvmStatic
        @BeforeAll
        @Throws(IOException::class, InterruptedException::class)
        fun compileCtxs() {
            /*TestUtils.compileCtx("it.unibo.ctxrobot.MainCtxrobotCustomKt")
            TestUtils.compileCtx("it.unibo.ctxserver.MainCtxserverCustomKt")
            TestUtils.compileCtx("it.unibo.ctxserver.MainCtxalarmCustomKt") */
        }
    }

    @BeforeEach
    @Throws(IOException::class, InterruptedException::class)
    fun up() {
        CommSystemConfig.tracing = false
        try {
            TestUtils.terminateProcOnPort(8095); //making sure that the port is free
            TestUtils.terminateProcOnPort(8096); //making sure that the port is free
            TestUtils.terminateProcOnPort(8097); //making sure that the port is free

            val (prR, processHandleR) = TestUtils.runCtx("build/libs/it.unibo.ctxrobot.MainCtxrobotCustomKt-1.0.jar")
            prRobot=prR; processHandleRobot=processHandleR
            val (prS, processHandleS) = TestUtils.runCtx("build/libs/it.unibo.ctxserver.MainCtxserverCustomKt-1.0.jar")
            prServer=prS; processHandleServer=processHandleS
            val (prA, processHandleA) = TestUtils.runCtx("build/libs/it.unibo.ctxalarm.MainCtxalarmCustomKt-1.0.jar")
            prAlarm=prA; processHandleAlarm=processHandleA

/*
gradle -PmainClass=it.unibo.ctxrobot.MainCtxrobotCustomKt jar
gradle -PmainClass=it.unibo.ctxserver.MainCtxserverCustomKt jar
gradle -PmainClass=it.unibo.ctxalarm.MainCtxalarmCustomKt jar
java -jar build/libs/it.unibo.ctxrobot.MainCtxrobotCustomKt-1.0.jar
java -jar build/libs/it.unibo.ctxserver.MainCtxserverCustomKt-1.0.jar
java -jar build/libs/it.unibo.ctxalarm.MainCtxalarmCustomKt-1.0.jar
 */
        } catch (e: IOException) {
            ColorsOut.outappl("Errore launch ", ColorsOut.RED)
            System.exit(1)
        }

        to = TestObserver()
        startObserverCoap("localhost", to)
        CommUtils.delay(2000)
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {
        ColorsOut.outappl("history=" + to!!.getHistory(), ColorsOut.MAGENTA);
        //FIRSTLY, try to be nice and make the program exit "normally"
        try {
            processHandleRobot!!.destroy()
            prRobot!!.destroy()
            processHandleServer!!.destroy()
            prServer!!.destroy()
        }catch(e :  NullPointerException){ }

        CommUtils.delay(1000)
        //since sometime this isn't enough, do it the heavy way...
        //processHandleRobot!!.destroyForcibly()
        //processHandleServer!!.destroyForcibly()

        connTransportTrolley!!.close()
        connWasteService!!.close()
        connMover!!.close()
    }


    @Test
    fun dummy() {
        Assert.assertTrue(true)
    }



    @Test
    @Timeout(45)
    fun test_2_accepted() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(45)) {
            CommUtils.delay(100)
            var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
            try {
                //FIRST REQUEST
                val connTcp = ConnTcp("localhost", 8095)
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("test_2_accepted answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))
                while (!coapCheckMover("mover(wait,HOME,HOME)")) {
                    CommUtils.delay(1000)
                }
                //SECONDO REQUEST
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,7),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))
                while (!coapCheckMover("mover(wait,HOME,HOME)")) {
                    CommUtils.delay(1000)
                }
                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_2_accepted ERROR:" + e.message)
                Assert.fail();
            }
        }
    }

    @Test
    fun test_1_accepted_1_rejected() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(15)){
            CommUtils.delay(100)
            var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
            try {
                //FIRST REQUEST
                val connTcp = ConnTcp("localhost", 8095)
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testFirstRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))
                while (!coapCheckMover("mover(wait,HOME,HOME)")) {
                    CommUtils.delay(1000)
                }
                //SECONDO REQUEST
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,9),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadrejected"))
                while (!coapCheckMover("mover(wait,HOME,HOME)")) {
                    CommUtils.delay(1000)
                }
                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_1_accepted_1_rejected ERROR:" + e.message)
                Assert.fail();
            }
        }
    }


    //---------------------------------------------------
    protected fun coapCheckWasteService(check: String?): Boolean {
        val answer = connWasteService!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun coapCheckMover(check: String?): Boolean {
        val answer = connMover!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun startObserverCoap(addr: String, handler: CoapHandler?) {
        /*object : Thread() {
            override fun run() {*/
        try {
            val qakdestination1 = "wasteservice"
            val qakdestination2 = "transporttrolley"
            val qakdestination3 = "mover"
            val ctxqakdest1 = "ctxserver"
            val ctxqakdest2 = "ctxrobot"
            val applPort1 = "8095"
            val applPort2 = "8096"
            connWasteService = CoapConnection("$addr:$applPort1", "$ctxqakdest1/$qakdestination1")
            connTransportTrolley = CoapConnection("$addr:$applPort2", "$ctxqakdest2/$qakdestination2")
            connMover = CoapConnection("$addr:$applPort2", "$ctxqakdest2/$qakdestination3")
            connWasteService!!.observeResource(handler)
            connTransportTrolley!!.observeResource(handler)
            connMover!!.observeResource(handler)
            ColorsOut.outappl("connecting via Coap conn:$connWasteService", ColorsOut.CYAN)
            ColorsOut.outappl("connecting via Coap conn:$connTransportTrolley", ColorsOut.CYAN)
            ColorsOut.outappl("connecting via Coap conn:$connMover", ColorsOut.CYAN)
            while (connWasteService!!.request("") === "0") {
                ColorsOut.outappl("waiting for conn $connWasteService", ColorsOut.CYAN)
                CommUtils.delay(500)
            }
            while (connTransportTrolley!!.request("") === "0") {
                ColorsOut.outappl("waiting for conn $connTransportTrolley", ColorsOut.CYAN)
                CommUtils.delay(500)
            }
            while (connMover!!.request("") === "0") {
                ColorsOut.outappl("waiting for conn $connMover", ColorsOut.CYAN)
                CommUtils.delay(500)
            }
        } catch (e: Exception) {
            ColorsOut.outerr("connectUsingCoap ERROR:" + e.message)
            System.exit(2);
        }
        /*}
    }.start()*/
    }
}