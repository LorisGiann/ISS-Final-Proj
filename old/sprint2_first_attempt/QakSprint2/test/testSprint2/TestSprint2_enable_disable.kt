package testSprint2

import it.unibo.kactor.MsgUtil
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
internal class TestSprint2_enable_disable {
    private var connTransportTrolley: CoapConnection? = null
    private var connWasteService: CoapConnection? = null
    private var connPickupdropouthandler: CoapConnection? = null
    private var connSonar : CoapConnection? = null
    private var connMover: CoapConnection? = null
    private var connBasicrobotwrapper: CoapConnection? = null
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
            TestUtils.terminateProcOnPort(8097); //making sure that the port is free

            val (prS, processHandleS) = TestUtils.runCtx("build/libs/it.unibo.ctxserver.MainCtxserverCustomKt-1.0.jar")
            prServer=prS; processHandleServer=processHandleS
            val (prR, processHandleR) = TestUtils.runCtx("build/libs/it.unibo.ctxrobot.MainCtxrobotCustomKt-1.0.jar")
            prRobot=prR; processHandleRobot=processHandleR
            val (prA, processHandleA) = TestUtils.runCtx("build/libs/it.unibo.ctxalarm.MainCtxalarmCustomKt-1.0.jar")
            prAlarm=prA; processHandleAlarm=processHandleA
        } catch (e: IOException) {
            ColorsOut.outappl("Errore launch ", ColorsOut.RED)
            System.exit(1)
        }
        //waitForApplStarted()
        to = TestObserver()
        startObserverCoap("localhost", "127.0.0.1","127.0.0.1", to)
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
			processHandleAlarm!!.destroy()
            prAlarm!!.destroy()
        }catch(e :  NullPointerException){ }
        CommUtils.delay(1000)
        //since sometime this isn't enough, do it the heavy way...
        processHandleRobot!!.destroyForcibly()
        processHandleServer!!.destroyForcibly()
		processHandleAlarm!!.destroyForcibly()
        //val s = ServerSocket(8095)
        //s.close()
        connTransportTrolley!!.close()
        //connWasteService!!.close()
        connPickupdropouthandler!!.close()
        connSonar!!.close()
        connMover!!.close()
        connBasicrobotwrapper!!.close()
    }

    protected fun waitForApplStarted() {
        var wasteservice = QakContext.getActor("wasteservice")
        while (wasteservice == null) {
            ColorsOut.outappl("TestSprint1 waits for appl ... ", ColorsOut.GREEN)
            CommUtils.delay(200)
            wasteservice = QakContext.getActor("wasteservice")
        }
        CommUtils.delay(100)
    }



    @Test
    fun dummy() {
        Assert.assertTrue(true)
    }

    @Test
    //@Order(1)
    fun test_halt_forward() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)){
            CommUtils.delay(1000)
            ColorsOut.outappl("test_halt_forward STARTS", ColorsOut.BLUE)
            val truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
            try {
                val connTcpWasteService = ConnTcp("localhost", 8095)
                val connTcpAlarmControl = ConnTcp("127.0.0.1", 8097)

                //deactive sonar and launch manually value
                CommUtils.delay(100)
                ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
                connTcpAlarmControl.forward(MsgUtil.buildDispatch("sonar","sonardeactivate","info(ok)","sonar").toString())
                CommUtils.delay(1000)
                ColorsOut.outappl("Truck request", ColorsOut.BLUE)
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                //truck request
                val answer = connTcpWasteService.request(truckRequestStr)
                ColorsOut.outappl("test_accepted answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))

                while (!coapCheckMover("mover(req_forward,GLASSBOX,HOME)" )) {
                    CommUtils.delay(500)
                }
                ColorsOut.outappl("Disable", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(0)").toString())
                CommUtils.delay(1000)
                ColorsOut.outappl("Enable", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(20)").toString())
                while (!coapCheckMover("mover(wait,HOME,HOME)" )) {
                    CommUtils.delay(500)
                }
                connTcpAlarmControl.close()
                connTcpWasteService.close()

                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "sonar(deactivateTheSonar,true)",
                    "basicrobotwrapper(forward_halt)",
                    "basicrobotwrapper(forward_cmd)"
                )))
                ColorsOut.outappl("FINISH", ColorsOut.GREEN)
                //to.setStartPosition(0);*/
            } catch (e: Exception) {
                Assert.fail("test_halt_forward ERROR:" + e.message)
            }
        }
    }

    @Test
    //@Order(1)
    fun test_halt_while_in_forward() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)){
            CommUtils.delay(1000)
            ColorsOut.outappl("test_halt_while_in_forward STARTS", ColorsOut.BLUE)
            val truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
            try {
                val connTcpWasteService = ConnTcp("localhost", 8095)
                val connTcpAlarmControl = ConnTcp("127.0.0.1", 8097)

                //deactive sonar and launch manually value
                CommUtils.delay(100)
                ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
                connTcpAlarmControl.forward(MsgUtil.buildDispatch("sonar","sonardeactivate","info(ok)","sonar").toString())
                CommUtils.delay(1000)
                ColorsOut.outappl("Truck request", ColorsOut.BLUE)
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                //truck request
                val answer = connTcpWasteService.request(truckRequestStr)
                ColorsOut.outappl("test_accepted answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))

                while (!coapCheckBasicrobotwrapper("basicrobotwrapper(forward_cmd)" )) {
                    CommUtils.delay(500)
                }
                ColorsOut.outappl("Disable", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(0)").toString())
                CommUtils.delay(1000)
                ColorsOut.outappl("Enable", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(20)").toString())
                while (!coapCheckMover("mover(wait,HOME,HOME)" )) {
                    CommUtils.delay(500)
                }
                connTcpAlarmControl.close()
                connTcpWasteService.close()

                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "sonar(deactivateTheSonar,true)",
                    "basicrobotwrapper(forward_cmd)",
                    "basicrobotwrapper(forward_halt)",
                    "basicrobotwrapper(forward_cmd)",
                    "mover(handle,PLASTICBOX,*)"
                )))
                ColorsOut.outappl("FINISH", ColorsOut.GREEN)
                //to.setStartPosition(0);*/
            } catch (e: Exception) {
                Assert.fail("test_halt_while_in_forward ERROR:" + e.message)
            }
        }
    }

    @Test
    //@Order(1)
    fun test_halt_pickup() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)){
            CommUtils.delay(1000)
            ColorsOut.outappl("test_halt_pickup STARTS", ColorsOut.BLUE)
            val truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
            try {
                val connTcpWasteService = ConnTcp("localhost", 8095)
                val connTcpAlarmControl = ConnTcp("127.0.0.1", 8097)

                //deactive sonar and launch manually value
                CommUtils.delay(100)
                ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
                connTcpAlarmControl.forward(MsgUtil.buildDispatch("sonar","sonardeactivate","info(ok)","sonar").toString())
                CommUtils.delay(1000)
                ColorsOut.outappl("Truck request", ColorsOut.BLUE)
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                //truck request
                object : Thread() {
                    override fun run(){
                        val answer = connTcpWasteService.request(truckRequestStr)
                        ColorsOut.outappl("test_accepted answer=$answer", ColorsOut.GREEN)
                        Assert.assertTrue(answer.contains("loadaccept"))
                    }
                }.start()
                while (!coapCheckPickupdropouthandler("pickupdropouthandler(do_pickup)" )) {
                    CommUtils.delay(500)
                }
                ColorsOut.outappl("Disable", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(0)").toString())
                CommUtils.delay(1000)
                ColorsOut.outappl("Enable", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(20)").toString())
                while (!coapCheckMover("mover(wait,HOME,HOME)" )) {
                    CommUtils.delay(500)
                }
                connTcpAlarmControl.close()
                connTcpWasteService.close()

                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "sonar(deactivateTheSonar,true)",
                    "pickupdropouthandler(do_pickup)",
                    "pickupdropouthandler(halt)",
                    "mover(req_forward,*,*)"
                )))
                ColorsOut.outappl("FINISH", ColorsOut.GREEN)
                //to.setStartPosition(0);*/
            } catch (e: Exception) {
                Assert.fail("test_halt_pickup ERROR:" + e.message)
            }
        }
    }

    @Test
    //@Order(1)
    fun test_halt_dropout() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)){
            CommUtils.delay(1000)
            ColorsOut.outappl("test_halt_dropout STARTS", ColorsOut.BLUE)
            val truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
            try {
                val connTcpWasteService = ConnTcp("localhost", 8095)
                val connTcpAlarmControl = ConnTcp("127.0.0.1", 8097)

                //deactive sonar and launch manually value
                CommUtils.delay(100)
                ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
                connTcpAlarmControl.forward(MsgUtil.buildDispatch("sonar","sonardeactivate","info(ok)","sonar").toString())
                CommUtils.delay(1000)
                ColorsOut.outappl("Truck request", ColorsOut.BLUE)
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                //truck request
                val answer = connTcpWasteService.request(truckRequestStr)
                ColorsOut.outappl("test_accepted answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))

                while (!coapCheckPickupdropouthandler("pickupdropouthandler(do_dropout)" )) {
                    CommUtils.delay(500)
                }
                ColorsOut.outappl("Disable", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(0)").toString())
                CommUtils.delay(1000)
                ColorsOut.outappl("Enable", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(20)").toString())
                while (!coapCheckMover("mover(wait,HOME,HOME)" )) {
                    CommUtils.delay(500)
                }
                connTcpAlarmControl.close()
                connTcpWasteService.close()

                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "sonar(deactivateTheSonar,true)",
                    "pickupdropouthandler(do_dropout)",
                    "pickupdropouthandler(halt)",
                    "mover(req_forward,*,*)"
                )))
                ColorsOut.outappl("FINISH", ColorsOut.GREEN)
                //to.setStartPosition(0);*/
            } catch (e: Exception) {
                Assert.fail("test_halt_dropout ERROR:" + e.message)
            }
        }
    }

    @Test
    fun test_halt_turn() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)){
            CommUtils.delay(1000)
            ColorsOut.outappl("test_halt_turn STARTS", ColorsOut.BLUE)
            val truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
            try {
                val connTcpWasteService = ConnTcp("localhost", 8095)
                val connTcpAlarmControl = ConnTcp("127.0.0.1", 8097)

                //deactive sonar and launch manually value
                CommUtils.delay(100)
                ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
                connTcpAlarmControl.forward(MsgUtil.buildDispatch("sonar","sonardeactivate","info(ok)","sonar").toString())
                CommUtils.delay(1000)
                ColorsOut.outappl("Truck request", ColorsOut.BLUE)
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                //truck request
                object : Thread() {
                    override fun run(){
                        val answer = connTcpWasteService.request(truckRequestStr)
                        ColorsOut.outappl("test_accepted answer=$answer", ColorsOut.GREEN)
                        Assert.assertTrue(answer.contains("loadaccept"))
                    }
                }.start()
                while (!coapCheckMover("mover(req_turn,HOME,INDOOR)" )) {
                    CommUtils.delay(100)
                }
                ColorsOut.outappl("Disable", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(0)").toString())
                CommUtils.delay(1000)
                ColorsOut.outappl("Enable", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(20)").toString())
                while (!coapCheckMover("mover(wait,HOME,HOME)" )) {
                    CommUtils.delay(500)
                }
                connTcpAlarmControl.close()
                connTcpWasteService.close()

                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "sonar(deactivateTheSonar,true)",
                    "basicrobotwrapper(halt)",
                    "pickupdropouthandler(do_pickup)"
                )))
                ColorsOut.outappl("FINISH", ColorsOut.GREEN)
                //to.setStartPosition(0);*/
            } catch (e: Exception) {
                Assert.fail("test_halt_turn ERROR:" + e.message)
            }
        }
    }

    protected fun coapCheckTransportTrolley(check: String?): Boolean {
        val answer = connTransportTrolley!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun coapCheckBasicrobotwrapper(check: String?): Boolean {
        val answer = connBasicrobotwrapper!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }


    protected fun coapCheckMover(check: String?): Boolean {
        val answer = connMover!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun coapCheckPickupdropouthandler(check: String?): Boolean {
        val answer = connPickupdropouthandler!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun startObserverCoap(addr1: String, addr2: String, addr3: String, handler: CoapHandler?) {
        /*object : Thread() {
            override fun run() {*/
                try {
                    val qakdestination1 = "wasteservice"
                    val qakdestination2 = "transporttrolley"
                    val qakdestination3 = "pickupdropouthandler"
                    //val qakdestination4 = "alarmreceiverpickupdropdown"
                    val qakdestination5 = "sonar"
                    val qakdestination6 = "mover"
                    val qakdestination7 = "basicrobotwrapper"
                    val ctxqakdest1 = "ctxserver"
                    val ctxqakdest2 = "ctxrobot"
                    val ctxqakdest3 = "ctxalarm"
                    val applPort1 = "8095"
                    val applPort2 = "8096"
                    val applPort3 = "8097"
                    connWasteService = CoapConnection("$addr1:$applPort1", "$ctxqakdest1/$qakdestination1")
                    CommUtils.delay(100)
                    connTransportTrolley = CoapConnection("$addr2:$applPort2", "$ctxqakdest2/$qakdestination2")
                    CommUtils.delay(100)
                    connPickupdropouthandler = CoapConnection("$addr2:$applPort2", "$ctxqakdest2/$qakdestination3")
                    CommUtils.delay(100)
                    connSonar = CoapConnection("$addr3:$applPort3", "$ctxqakdest3/$qakdestination5")
                    CommUtils.delay(100)
                    connMover = CoapConnection("$addr2:$applPort2", "$ctxqakdest2/$qakdestination6")
                    CommUtils.delay(100)
                    connBasicrobotwrapper = CoapConnection("$addr2:$applPort2", "$ctxqakdest2/$qakdestination7")
                    CommUtils.delay(100)
                    connWasteService!!.observeResource(handler)
                    CommUtils.delay(100)
                    connTransportTrolley!!.observeResource(handler)
                    CommUtils.delay(100)
                    connPickupdropouthandler!!.observeResource(handler)
                    CommUtils.delay(100)
                    connSonar!!.observeResource(handler)
                    CommUtils.delay(100)
                    connMover!!.observeResource(handler)
                    CommUtils.delay(100)
                    connBasicrobotwrapper!!.observeResource(handler)
                    CommUtils.delay(100)

                    ColorsOut.outappl("connecting via Coap conn:$connWasteService", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connTransportTrolley", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connPickupdropouthandler", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connSonar", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connMover", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connBasicrobotwrapper", ColorsOut.CYAN)

                    while (connWasteService!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connWasteService", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }

                    while (connTransportTrolley!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connTransportTrolley", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }

                    while (connPickupdropouthandler!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connPickupdropouthandler", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }

                    while (connSonar!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connSonar", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }

                    while (connMover!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connMover", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }

                    while (connBasicrobotwrapper!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connBasicrobotwrapper", ColorsOut.CYAN)
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