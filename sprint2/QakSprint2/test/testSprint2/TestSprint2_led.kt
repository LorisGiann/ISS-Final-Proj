package testSprint2

import it.unibo.kactor.MsgUtil
import it.unibo.kactor.QakContext
import org.eclipse.californium.core.CoapHandler
import org.junit.Assert
import org.junit.jupiter.api.*
import unibo.comm22.coap.CoapConnection
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.IOException
import java.time.Duration
import kotlin.test.Test

internal class TestSprint2_led {
    private var connTransportTrolley: CoapConnection? = null
    private var connLed: CoapConnection? = null
    private var connAlarmControl: CoapConnection? = null
    private var connSonar : CoapConnection? = null
    private var to: TestObserver? = null
    private var processHandleServer: ProcessHandle? = null
    private var processHandleRobot: ProcessHandle? = null
	private var processHandleAlarm: ProcessHandle? = null
    private var prAlarm: Process? = null
    private var prServer: Process? = null
    private var prRobot: Process? = null

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
            TestUtils.terminateProcOnPort(8095); //making sure that the port is free
            TestUtils.terminateProcOnPort(8096); //making sure that the port is free
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
        ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
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
        connTransportTrolley!!.close()
        //connWasteService!!.close()
        connAlarmControl!!.close()
        connSonar!!.close()
        connLed!!.close()
    }



    @Test
    fun dummy() {
        Assert.assertTrue(true)
    }

    @Test
    fun test_led_at_home() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)){
            CommUtils.delay(1000)
            try {
                //val connTcpWasteService = ConnTcp("localhost", 8095)
                //val connTcpTrasportTrolley = ConnTcp("127.0.0.1", 8096)
                val connTcpAlarmControl = ConnTcp("127.0.0.1", 8097)

                //deactive sonar: this test will directly provide AlarmControl the distance events
                CommUtils.delay(100)
                connTcpAlarmControl.forward(MsgUtil.buildDispatch("alarmcontrol","sonardeactivate","info(ok)","alarmcontrol").toString())
                CommUtils.delay(100)
                //command an halt while in home

                //val answer = connTcpTrasportTrolley.request(MsgUtil.buildDispatch("transporttrolley","sonardeactivate","info(ok)","alarmcontrol").toString())
                //ColorsOut.outappl("test_accepted answer=$answer", ColorsOut.GREEN)
                //Assert.assertTrue(answer.contains("loadaccept"))

                while (!coapCheckTransportTrolley("transporttrolley(wait,HOME,HOME)")) {
                    CommUtils.delay(100)
                }

                ColorsOut.outappl("Disable robot", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(0)").toString())
                CommUtils.delay(1000)
                ColorsOut.outappl("Enable robot", ColorsOut.GREEN)
                connTcpAlarmControl.forward(MsgUtil.buildEvent("sonar","sonardata","distance(20)").toString())
                CommUtils.delay(1000)

                connTcpAlarmControl.close()
                //connTcpWasteService.close()
                //connTcpTrasportTrolley.close()

                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "sonar(end,*)",
                    "transporttrolley(halt,HOME,HOME)",
                    "transporttrolley(wait_check_disable,HOME,HOME)"
                )))
                to!!.setStartPosition(0);
                //during the whole test the led have to stay off:
                Assert.assertFalse( to!!.checkNextContent("led(*,${ws.LedState.ON})") >= 0);
                Assert.assertFalse( to!!.checkNextContent("led(*,${ws.LedState.BLINK})") >= 0);
                Assert.assertTrue( to!!.checkNextContent("led(*,${ws.LedState.OFF})") >= 0);
            } catch (e: Exception) {
                Assert.fail("test_led_at_home ERROR:" + e.message)
            }
        }
    }


    protected fun coapCheckTransportTrolley(check: String?): Boolean {
        val answer = connTransportTrolley!!.request("")
        ColorsOut.outappl("coapCheck answer=$answer", ColorsOut.CYAN)
        return answer.contains(check!!)
    }

    protected fun startObserverCoap(addr1: String, addr2: String, addr3: String, handler: CoapHandler?) {
        /*object : Thread() {
            override fun run() {*/
                try {
                    val qakdestination1 = "wasteservice"
                    val qakdestination2 = "transporttrolley"
                    val qakdestination3 = "alarmcontrol"
                    val qakdestination4 = "sonar"
                    val qakdestination5 = "led"
                    val ctxqakdest1 = "ctxserver"
                    val ctxqakdest2 = "ctxrobot"
                    val ctxqakdest3 = "ctxalarm"
                    val ctxqakdest4 = "ctxalarm"
                    val applPort1 = "8095"
                    val applPort2 = "8096"
                    val applPort3 = "8097"
                    //connWasteService = CoapConnection("$addr1:$applPort1", "$ctxqakdest1/$qakdestination1")
                    connTransportTrolley = CoapConnection("$addr2:$applPort2", "$ctxqakdest2/$qakdestination2")
                    CommUtils.delay(100)
                    connAlarmControl = CoapConnection("$addr3:$applPort3", "$ctxqakdest3/$qakdestination3")
                    CommUtils.delay(100)
                    connSonar = CoapConnection("$addr3:$applPort3", "$ctxqakdest3/$qakdestination4")
                    CommUtils.delay(100)
                    connLed = CoapConnection("$addr3:$applPort3", "$ctxqakdest3/$qakdestination5")
                    CommUtils.delay(100)
                    //connWasteService!!.observeResource(handler)
                    connTransportTrolley!!.observeResource(handler)
                    CommUtils.delay(100)
                    connAlarmControl!!.observeResource(handler)
                    CommUtils.delay(100)
                    connSonar!!.observeResource(handler)
                    CommUtils.delay(100)
                    connLed!!.observeResource(handler)
                    CommUtils.delay(100)
                    //ColorsOut.outappl("connecting via Coap conn:$connWasteService", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connTransportTrolley", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connAlarmControl", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connSonar", ColorsOut.CYAN)
                    ColorsOut.outappl("connecting via Coap conn:$connLed", ColorsOut.CYAN)
                    /*
                    while (connWasteService!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connWasteService", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }
                    */
                    while (connTransportTrolley!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connTransportTrolley", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }
                    while (connAlarmControl!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connAlarmControl", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }
                    while (connSonar!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connSonar", ColorsOut.CYAN)
                        CommUtils.delay(500)
                    }
                    while (connLed!!.request("") === "0") {
                        ColorsOut.outappl("waiting for conn $connLed", ColorsOut.CYAN)
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