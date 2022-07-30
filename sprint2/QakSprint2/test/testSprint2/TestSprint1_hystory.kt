package testSprint2

import it.unibo.kactor.QakContext
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

//@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestSprint1_hystory {
    private var connTransportTrolley: CoapConnection? = null
    private var connWasteService: CoapConnection? = null
    private var connDepositAction: CoapConnection? = null
    private var connMover: CoapConnection? = null
    private var connPickupdropouthandler: CoapConnection? = null
    //private var connBasicrobotwrapper: CoapConnection? = null
    private var to: TestObserver? = null
    private var processHandleServer: ProcessHandle? = null
    private var processHandleRobot: ProcessHandle? = null
    private var prServer: Process? = null
    private var prRobot: Process? = null
    private var serverThread: Thread? = null

    companion object {
        @JvmStatic
        @BeforeAll
        @Throws(IOException::class, InterruptedException::class)
        fun compileCtxs() {
            /*TestUtils.compileCtx("it.unibo.ctxrobot.MainCtxrobotKt")
            TestUtils.compileCtx("it.unibo.ctxserver.MainCtxserverKt")*/
        }
    }

    @BeforeEach
    @Throws(IOException::class, InterruptedException::class)
    fun up() {
        CommSystemConfig.tracing = false
        try {
            TestUtils.terminateProcOnPort(8096); //making sure that the port is free
            TestUtils.terminateProcOnPort(8095); //making sure that the port is free

            val (prR, processHandleR) = TestUtils.runCtx("build/libs/it.unibo.ctxrobot.MainCtxrobotCustomKt-1.0.jar")
            prRobot=prR; processHandleRobot=processHandleR
            val (prS, processHandleS) = TestUtils.runCtx("build/libs/it.unibo.ctxserver.MainCtxserverCustomKt-1.0.jar")
            prServer=prS; processHandleServer=processHandleS
        } catch (e: IOException) {
            ColorsOut.outappl("Errore launch ", ColorsOut.RED)
            System.exit(1)
        }
        /*serverThread =  object : Thread() {
            override fun run() {
                main();
            }
        }
        serverThread!!.start()*/

        //waitForApplStarted()
        to = TestObserver()
        startObserverCoap("localhost", to)
        CommUtils.delay(2000)
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {
        //FIRSTLY, try to be nice and make the program exit "normally"
        //serverThread!!.stop();
        try {
            processHandleRobot!!.destroy()
            prRobot!!.destroy()
            processHandleServer!!.destroy()
            prServer!!.destroy()
        }catch(e :  NullPointerException){ }
        CommUtils.delay(1000)
        //since sometime this isn't enough, do it the heavy way...
        processHandleRobot!!.destroyForcibly()
        processHandleServer!!.destroyForcibly()
        //val s = ServerSocket(8095)
        //s.close()
        connTransportTrolley!!.close()
        connWasteService!!.close()
        connDepositAction!!.close()
        //connBasicrobotwrapper!!.close()
        connMover!!.close()
        connPickupdropouthandler!!.close()
    }




    @Test
    fun dummy() {
        Assert.assertTrue(true)
    }

    @Test
    //@Order(1)
    fun test_accepted() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(15)){
            ColorsOut.outappl("test_accepted STARTS", ColorsOut.BLUE)
            val truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
            try {
                val connTcp = ConnTcp("localhost", 8095)
                val answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("test_accepted answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))
                connTcp.close()
                while (!coapCheckMover("mover(wait,HOME,HOME)")) {
                    CommUtils.delay(1000)
                }
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                //POSITION INITIAL
                Assert.assertTrue(to!!.checkNextContents(arrayOf(
                    "mover(wait,HOME,HOME)",
                    "transporttrolley(wait)",
                    "pickupdropouthandler(wait)",
                    "wasteservice(wait,0.0,0.0)",
                    "depositaction(wait)"))>0)
                //PICKUP
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "wasteservice(handle_req,0.0,0.0)",
                    "wasteservice(req_depositaction,0.0,2.0)",
                    "transporttrolley(req_move,INDOOR)",
                    "transporttrolley(req_pickup)",
                    "depositaction(reply,GLASS)"
                )))
                //DROPOUT
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "transporttrolley(req_move,GLASSBOX)",
                    "transporttrolley(req_dropout)"
                )))
                //RETURN IN HOME
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "transporttrolley(req_move,HOME)",
                )))
                //POSITION FINAL
                Assert.assertTrue(to!!.checkNextContents(arrayOf(
                    "transporttrolley(wait)",
                    "mover(wait,HOME,HOME)"
                ))>0)
                //to.setStartPosition(0);*/
            } catch (e: Exception) {
                Assert.fail("test_accepted ERROR:" + e.message)
            }
        }
    }

    @Test
    fun test_rejected() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(15)) {
            ColorsOut.outappl("testLoadKo STARTS", ColorsOut.BLUE)
            val truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,11),1)"
            try {
                val connTcp = ConnTcp("localhost", 8095)
                val answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("test_rejected answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadrejected"))
                CommUtils.delay(1000)
                connTcp.close()
                while (!coapCheckMover("mover(wait,HOME,HOME)")) {
                    CommUtils.delay(1000)
                }
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)

                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                        "wasteservice(handle_req,0.0,0.0)",
                        "wasteservice(rejected,0.0,0.0)",
                        "wasteservice(wait,0.0,0.0)"
                )))

            } catch (e: java.lang.Exception) {
                Assert.fail("test_rejected ERROR:" + e.message)
            }
        }
    }

    @Test
    fun test_2_accepted_while_in_operation() { //the second request is made while the robot is still in operation, while still dropping down material
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(35)){
            CommUtils.delay(100)
            var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)"
            try {
                //FIRST REQUEST
                val connTcp = ConnTcp("localhost", 8095)
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testFirstRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))
                while (!coapCheckMover("mover(req_forward,PLASTICBOX,HOME)")) {
                    CommUtils.delay(800)
                }
                CommUtils.delay(100)
                //SECONDO REQUEST
                ColorsOut.outappl("launch second request", ColorsOut.GREEN)
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,7),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))
                connTcp.close()
                while (!coapCheckMover("mover(wait,HOME,HOME)")) {
                    CommUtils.delay(1000)
                }
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                //POSITION INITIAL
                Assert.assertTrue(to!!.checkNextContents(arrayOf(
                    "mover(wait,HOME,HOME)",
                    "transporttrolley(wait)",
                    "pickupdropouthandler(wait)",
                    "wasteservice(wait,0.0,0.0)",
                    "depositaction(wait)"))>0)
                //PICKUP
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "wasteservice(handle_req,0.0,0.0)",
                    "wasteservice(req_depositaction,2.0,0.0)",
                    "transporttrolley(req_move,INDOOR)",
                    "transporttrolley(req_pickup)",
                    "depositaction(reply,PLASTIC)"
                )))
                //DROPOUT
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "transporttrolley(req_move,PLASTICBOX)",
                    "transporttrolley(req_dropout)"
                )))
                //CHECK SECOND REQUEST
                Assert.assertTrue(to!!.checkNextContent("wasteservice(wait,2.0,7.0)")>0)
                //POSITION FINAL
                Assert.assertTrue(to!!.checkNextContents(arrayOf(
                    "transporttrolley(wait)",
                    "mover(wait,HOME,HOME)")) > 0)
            } catch (e: java.lang.Exception) {
                Assert.fail("test_2_accepted_while_in_operation ERROR:" + e.message)
            }
        }
    }

    @Test
    fun test_1_accepted_1_rejected_while_in_operation() { //the second request is made while the robot is still in operation, while still dropping down material
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)){
            CommUtils.delay(100)
            var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)"
            try {
                //FIRST REQUEST
                val connTcp = ConnTcp("localhost", 8095)
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testFirstRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))
                while (!coapCheckMover("mover(req_forward,PLASTICBOX,HOME)")) {
                    CommUtils.delay(800)
                }
                CommUtils.delay(100)
                //SECONDO REQUEST
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,11),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadrejected"))
                connTcp.close()
                while (!coapCheckMover("mover(wait,HOME,HOME)")) {
                    CommUtils.delay(1000)
                }
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)

                //POSITION INITIAL
                Assert.assertTrue(to!!.checkNextContents(arrayOf(
                    "mover(wait,HOME,HOME)",
                    "transporttrolley(wait)",
                    "pickupdropouthandler(wait)",
                    "wasteservice(wait,0.0,0.0)",
                    "depositaction(wait)"))>0)
                //PICKUP
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "wasteservice(handle_req,0.0,0.0)",
                    "wasteservice(req_depositaction,2.0,0.0)",
                    "transporttrolley(req_move,INDOOR)",
                    "transporttrolley(req_pickup)",
                    "depositaction(reply,PLASTIC)"
                )))
                //DROPOUT
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "transporttrolley(req_move,PLASTICBOX)",
                    "transporttrolley(req_dropout)"
                )))
                //CHECK SECOND REQUEST
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "wasteservice(handle_req,2.0,0.0)",
                    "wasteservice(rejected,2.0,0.0)",
                    "wasteservice(wait,2.0,0.0)"
                )))
                //POSITION FINAL
                Assert.assertTrue(to!!.checkNextContents(arrayOf(
                    "transporttrolley(wait)",
                    "mover(wait,HOME,HOME)")) > 0)
            } catch (e: java.lang.Exception) {
                Assert.fail("test_1_accepted_1_rejected_while_in_operation ERROR:" + e.message)
            }
        }
    }

    @Test
    fun test_2_accepted_while_returning_home() { //the second request is made while the robot is still in operation, while returning to home
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(35)){
            CommUtils.delay(100)
            var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)"
            try {
                //FIRST REQUEST
                val connTcp = ConnTcp("localhost", 8095)
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testFirstRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))
                while (!coapCheckDepositAction("depositaction(move_home,PLASTIC)")) {
                    CommUtils.delay(800)
                }
                CommUtils.delay(100)
                //SECONDO REQUEST
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,7),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))
                connTcp.close()
                while (!coapCheckMover("mover(wait,HOME,HOME)")) {
                    CommUtils.delay(1000)
                }
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                //POSITION INITIAL
                Assert.assertTrue(to!!.checkNextContents(arrayOf(
                    "mover(wait,HOME,HOME)",
                    "transporttrolley(wait)",
                    "pickupdropouthandler(wait)",
                    "wasteservice(wait,0.0,0.0)",
                    "depositaction(wait)"))>0)
                //PICKUP
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "wasteservice(handle_req,0.0,0.0)",
                    "wasteservice(req_depositaction,2.0,0.0)",
                    "transporttrolley(req_move,INDOOR)",
                    "transporttrolley(req_pickup)",
                    "depositaction(reply,PLASTIC)"
                )))
                //DROPOUT
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "transporttrolley(req_move,PLASTICBOX)",
                    "transporttrolley(req_dropout)"
                )))
                //CHECK SECOND REQUEST
                Assert.assertTrue(to!!.checkNextContent("wasteservice(wait,2.0,7.0)")>0)
                //POSITION FINAL
                Assert.assertTrue(to!!.checkNextContents(arrayOf(
                    "transporttrolley(wait)",
                    "mover(wait,HOME,HOME)")) > 0)
            } catch (e: java.lang.Exception) {
                Assert.fail("test_2_accepted_while_returning_home ERROR:" + e.message)
            }
        }
    }

    @Test
    fun test_1_accepted_1_rejected_while_returning_home() { //the second request is made while the robot is still in operation, while returning home
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)){
            CommUtils.delay(100)
            var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)"
            try {
                //FIRST REQUEST
                val connTcp = ConnTcp("localhost", 8095)
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testFirstRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))
                while (!coapCheckDepositAction("depositaction(move_home,PLASTIC)")) {
                    CommUtils.delay(800)
                }
                CommUtils.delay(100)
                //SECONDO REQUEST
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,11),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadrejected"))
                connTcp.close()
                while (!coapCheckMover("mover(wait,HOME,HOME)")) {
                    CommUtils.delay(1000)
                }
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)

                //POSITION INITIAL
                Assert.assertTrue(to!!.checkNextContents(arrayOf(
                    "mover(wait,HOME,HOME)",
                    "transporttrolley(wait)",
                    "pickupdropouthandler(wait)",
                    "wasteservice(wait,0.0,0.0)",
                    "depositaction(wait)"))>0)
                //PICKUP
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "wasteservice(handle_req,0.0,0.0)",
                    "wasteservice(req_depositaction,2.0,0.0)",
                    "transporttrolley(req_move,INDOOR)",
                    "transporttrolley(req_pickup)",
                    "depositaction(reply,PLASTIC)"
                )))
                //DROPOUT
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "transporttrolley(req_move,PLASTICBOX)",
                    "transporttrolley(req_dropout)"
                )))
                //CHECK SECOND REQUEST
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "wasteservice(handle_req,2.0,0.0)",
                    "wasteservice(rejected,2.0,0.0)",
                    "wasteservice(wait,2.0,0.0)"
                )))
                //POSITION FINAL
                Assert.assertTrue(to!!.checkNextContents(arrayOf(
                    "transporttrolley(wait)",
                    "mover(wait,HOME,HOME)")) > 0)
            } catch (e: java.lang.Exception) {
                Assert.fail("test_1_accepted_1_rejected_while_returning_home ERROR:" + e.message)
            }
        }
    }

    //this test is important, because an
    /*
    @Test
    fun test_1_accepted_1_rejected_while_returning_home_1_accepted() { //the second request is made while the robot is still in operation, while returning home
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(45)){
            CommUtils.delay(100)
            var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)"
            try {
                //FIRST REQUEST
                val connTcp = ConnTcp("localhost", 8095)
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testFirstRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))
                while (!coapCheckWasteService("handle_move_home")) {
                    CommUtils.delay(500)
                }
                CommUtils.delay(100)

                //SECONDO REQUEST
                println("MAKING SECOND REQUEST")
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,11),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadrejected"))
                //WasteService is in wait, but robot is still moving towards home (actually since this is critical let's check that)
                if (!coapCheckTransportTrolley("forward")) {
                    fail("Unlucky test: Robot did reached home before wasteservice was in wait. This test is meaningful only if the robot is still moving while the wasteservice is in wait");
                }
                to!!.getHistory().clear()  //jump directly to the messages that will arrive from now onwards     //to!!.setStartPosition(to!!.getHistory().size);
                while (!coapCheckTransportTrolley("wait")) { //now wait for the robot to reach home (move_answer received)
                    CommUtils.delay(1000)
                }
                CommUtils.delay(100)

                //THIRD REQUEST
                println("MAKING THIRD REQUEST")
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,1),1)"
                answer = connTcp.request(truckRequestStr)      //ACTUALLY WHAT HAPPENS IS A DEADLOCK BETWEEN ACTORS: THIS REPLY WILL NEVER ARRIVE AND THE TEST WILL FAIL BECAUSE OF THE TIMEOUT!
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))

                while(to!!.checkNextContent("wasteservice(handle_req,2.0,0.0)") > 0){} //jump to the last few messages, after the request has been made

                //check wether the previous move_answer interfears with the one we now have to receive from the robot
                //if the previous remained in the queue, the transition of wasteservice from handle_req->handle_move_indoor happens before the robot actually arrives (and this is bad)
                Assert.assertFalse(
                        to!!.checkNextSequence(arrayOf("wasteservice(handle_move_indoor,3.0,0.0)", "transporttrolley(forward_robot,HOME,INDOOR)"))
                );

                //stop the test aafter bringing the robot home
                while (!coapCheckWasteService("wait")) { //wait for the robot to reach home
                    CommUtils.delay(1000)
                }

                connTcp.close()
            } catch (e: java.lang.Exception) {
                Assert.fail("test_1_accepted_1_rejected_while_returning_home_1_accepted ERROR:" + e.message)
            }
        }
    }
    */
    //---------------------------------------------------
    protected fun coapCheckWasteService(check: String?): Boolean {
        val answer = connWasteService!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun coapCheckTransportTrolley(check: String?): Boolean {
        val answer = connTransportTrolley!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun coapCheckDepositAction(check: String?): Boolean {
        val answer = connDepositAction!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun coapCheckMover(check: String?): Boolean {
        val answer = connMover!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun coapCheckBasicrobotwrapper(check: String?): Boolean {
        val answer = connDepositAction!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun coapCheckconnPickupdropouthandler(check: String?): Boolean {
        val answer = connDepositAction!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun startObserverCoap(addr: String, handler: CoapHandler?) {
        /*object : Thread() {
            override fun run() {*/
                try {
                    val qakdestination1 = "wasteservice"
                    val qakdestination2 = "transporttrolley"
                    val qakdestination3 = "depositaction"
                    val qakdestination4 = "mover"
                    val qakdestination5 = "pickupdropouthandler"
                    val qakdestination6 = "basicrobotwrapper"
                    val ctxqakdest1 = "ctxserver"
                    val ctxqakdest2 = "ctxrobot"
                    val applPort1 = "8095"
                    val applPort2 = "8096"
                    connWasteService = CoapConnection("$addr:$applPort1", "$ctxqakdest1/$qakdestination1")
                    connTransportTrolley = CoapConnection("$addr:$applPort2", "$ctxqakdest2/$qakdestination2")
                    connDepositAction = CoapConnection("$addr:$applPort1", "$ctxqakdest1/$qakdestination3")
                    //connBasicrobotwrapper = CoapConnection("$addr:$applPort2", "$ctxqakdest2/$qakdestination6")
                    connMover = CoapConnection("$addr:$applPort2", "$ctxqakdest2/$qakdestination4")
                    connPickupdropouthandler = CoapConnection("$addr:$applPort2", "$ctxqakdest2/$qakdestination5")
                    connWasteService!!.observeResource(handler)
                    connTransportTrolley!!.observeResource(handler)
                    connDepositAction!!.observeResource(handler)
                    //connBasicrobotwrapper!!.observeResource(handler)
                    connMover!!.observeResource(handler)
                    connPickupdropouthandler!!.observeResource(handler)
                    ColorsOut.outappl("connecting via Coap conn:$connWasteService", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connTransportTrolley", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connDepositAction", ColorsOut.CYAN)
                    //ColorsOut.outappl("connecting via Coap conn:$connBasicrobotwrapper", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connMover", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connPickupdropouthandler", ColorsOut.CYAN)
                    while (connWasteService!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connWasteService", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }
                    while (connTransportTrolley!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connTransportTrolley", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }
                    while (connDepositAction!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connDepositAction", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }
                    /*
                    while (connBasicrobotwrapper!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connBasicrobotwrapper", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }
                    */
                    while (connMover!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connMover", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }
                    while (connPickupdropouthandler!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connPickupdropouthandler", ColorsOut.CYAN)
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