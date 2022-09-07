package testSprint2

import alice.tuprolog.Struct
import alice.tuprolog.Term
import it.unibo.kactor.MsgUtil
import org.junit.jupiter.api.*
import testCommon.TestObserver
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.IOException
import java.time.Duration
import kotlin.test.Test
import testCommon.*

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestSprint2_integration_halt {
    private var to: TestObserver? = null
    private var processHandleServer: ProcessHandle? = null
    private var processHandleRobot: ProcessHandle? = null
	private var processHandleAlarm: ProcessHandle? = null
    private var prServer: Process? = null
    private var prRobot: Process? = null
	private var prAlarm: Process? = null

    /*
    Wait until all the connected machines are in the initial expected state
     */
    fun waitRegimeState(){
        val o = to!!
        o.waitUntilState("wasteservice","wait");
        o.waitUntilState("depositaction","wait");
        o.waitUntilState("transporttrolley","wait")
        o.waitUntilState("basicrobotwrapper","wait")
        o.waitUntilState("basicrobotlorisdavide","work")
        o.waitUntilState("pickupdropouthandler","wait")
        //o.waitUntilState("sonarlorisdavide","activateTheSonar")
        //o.waitUntilState("led","s0")
        CommUtils.delay(50)  //concede a little extra time to the actors in the system to communicate and do their transition, before going ahead with the rest of the test
    }

    @BeforeEach
    @Throws(IOException::class, InterruptedException::class)
    fun up() {
        CommSystemConfig.tracing = false
        try {
            TestUtils.terminateProcOnPort(8095) //making sure that the port is free
            TestUtils.terminateProcOnPort(8096) //making sure that the port is free
			TestUtils.terminateProcOnPort(8097) //making sure that the port is free

            val (prR, processHandleR) = TestUtils.runCtx("build/libs/it.unibo.ctxrobot.MainCtxrobotKt-1.0.jar")
            prRobot=prR; processHandleRobot=processHandleR
            val (prS, processHandleS) = TestUtils.runCtx("build/libs/it.unibo.ctxserver.MainCtxserverKt-1.0.jar")
            prServer=prS; processHandleServer=processHandleS
			val (prA, processHandleA) = TestUtils.runCtx("build/libs/it.unibo.ctxalarm.MainCtxalarmKt-1.0.jar")
            prAlarm=prA; processHandleAlarm=processHandleA

        } catch (e: IOException) {
            ColorsOut.outappl("Errore launch ", ColorsOut.RED)
            System.exit(1)
        }

        val to = TestObserver()
        to.establishCoapConn("wasteservice");
        to.establishCoapConn("depositaction");
        to.establishCoapConn("transporttrolley");
        to.establishCoapConn("mover")
        to.establishCoapConn("basicrobotwrapper")
        to.establishCoapConn("basicrobotlorisdavide")
        to.establishCoapConn("pickupdropouthandler")
        to.establishCoapConn("sonarlorisdavide")
        //to.establishCoapConn("led")
        to.establishCoapConn("alarmreceivertest")
        this.to = to
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {
        ColorsOut.outappl("history=" + to!!.getHistory(), ColorsOut.MAGENTA)

        goBackHome()

        try {
            //FIRSTLY, try to be nice and make the program exit "normally"
            processHandleRobot!!.destroy()
            prRobot!!.destroy()
            processHandleServer!!.destroy()
            prServer!!.destroy()
            processHandleAlarm!!.destroy()
            prAlarm!!.destroy()
            CommUtils.delay(1000)
            //since sometime this isn't enough, do it the heavy way...
            processHandleRobot!!.destroyForcibly()
            processHandleServer!!.destroyForcibly()
            processHandleAlarm!!.destroyForcibly()
        }catch(_:  NullPointerException){ }
        
        to!!.closeAllConnections()
    }

    fun goTo(pos : ws.Position) {
        val connTcp = ConnTcp("localhost", 8096)
        waitRegimeState()

        val requestStr = "msg(moveto, request,python,mover,moveto($pos),1)"
        val answer = connTcp.request(requestStr)
        ColorsOut.outappl("goTo answer=$answer", ColorsOut.GREEN)
        waitRegimeState()
        connTcp.close()
        to!!.clearHistory()
    }
    fun goBackHome() {
        goTo(ws.Position.HOME)
        val currentState = to!!.getCurrentCoapState("mover")
        val orientation  = (Term.createTerm(currentState) as Struct).getArg(3).toString()
        if(orientation=="CLK") {
            val connTcp = ConnTcp("localhost", 8096)
            val requestStr = "msg(cmdsync, request,python,basicrobotwrapper,cmdsync(r),1)"
            connTcp.request(requestStr)
        }
    }


    @Test
    fun dummy() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(5)) {
            CommUtils.delay(1000)
        }
    }



    @Test
    fun test_accepted() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(90)) {
            try {
                val connTcpServer = ConnTcp("localhost", 8095)
                val connTcpRobot = ConnTcp("localhost", 8096)
                val connTcpAlarm = ConnTcp("127.0.0.1", 8097)

                waitRegimeState()
                ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
                connTcpAlarm.forward(MsgUtil.buildDispatch("python","sonardeactivate","sonardeactivate(_)","sonarlorisdavide").toString())

                //REQUEST
                var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
                object : Thread(){
                    override fun run(){
                        val answer = connTcpServer.request(truckRequestStr)
                        ColorsOut.outappl("request answer=$answer", ColorsOut.GREEN)
                        Assertions.assertTrue(answer.contains("loadaccept"))
                    }
                }.start()

                //STOP WHILE IN FORWARD
                to!!.waitUntilState("basicrobotlorisdavide","basicrobotlorisdavide(execcmd,w)")
                CommUtils.delay(250)
                connTcpRobot.forward(MsgUtil.buildEvent("python","alarm","alarm(_)").toString())
                CommUtils.delay(1000)
                connTcpRobot.forward(MsgUtil.buildEvent("python","alarmceased","alarmceased(_)").toString())

                //STOP WHILE IN TURN
                to!!.waitUntilState("basicrobotlorisdavide","basicrobotlorisdavide(execcmd,l)")
                CommUtils.delay(50)
                connTcpRobot.forward(MsgUtil.buildEvent("python","alarm","alarm(_)").toString())
                CommUtils.delay(1000)
                connTcpRobot.forward(MsgUtil.buildEvent("python","alarmceased","alarmceased(_)").toString())

                //STOP WHILE IN PICKUP
                to!!.waitUntilState("pickupdropouthandler","pickupdropouthandler(do_pickup)")
                CommUtils.delay(250)
                connTcpRobot.forward(MsgUtil.buildEvent("python","alarm","alarm(_)").toString())
                CommUtils.delay(1000)
                connTcpRobot.forward(MsgUtil.buildEvent("python","alarmceased","alarmceased(_)").toString())

                //STOP WHILE IN DROPDOWN
                to!!.waitUntilState("pickupdropouthandler","pickupdropouthandler(do_dropout)")
                CommUtils.delay(250)
                connTcpRobot.forward(MsgUtil.buildEvent("python","alarm","alarm(_)").toString())
                CommUtils.delay(1000)
                connTcpRobot.forward(MsgUtil.buildEvent("python","alarmceased","alarmceased(_)").toString())

                waitRegimeState()
                connTcpServer.close()
                connTcpRobot.close()
                connTcpAlarm.close()

                // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
                Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(forward_cmd)") >= 0)
                Assertions.assertTrue(to!!.checkNextContents(arrayOf( //halt move forward
                    "alarmreceivertest(wait,alarm)",
                    "basicrobotwrapper(halt_forward)"
                )) >= 0)
                Assertions.assertTrue(to!!.checkNextContents(arrayOf( //resume move forward
                    "alarmreceivertest(wait,alarmceased)",
                    "basicrobotwrapper(forward_cmd)"
                )) >= 0)
                Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(wait)") >= 0)

                Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(other_cmd,l)") >= 0)
                Assertions.assertTrue(to!!.checkNextContents(arrayOf( //alarm activate, pickupdropouthandler in alarm, robot doesn't stop...
                    "alarmreceivertest(wait,alarm)",
                    "pickupdropouthandler(alarm)",
                )) >= 0)
                Assertions.assertTrue(to!!.checkNextContents(arrayOf(//basicrobotwrappre in analarm only after the turn. At this time a pickup will be also issued
                    /*"basicrobotlorisdavide(work)",*/       //in theory this happens after the alarm. In practise it doesent due to coap connection delays. A more meaningful test is done in unit testing
                    "basicrobotwrapper(alarm)",
                    "transporttrolley(req_pickup)",
                )) >= 0)
                Assertions.assertTrue(to!!.checkNextContents(arrayOf( //alarm ceased, pickupdropout passing almost immediately to pickup
                    "alarmreceivertest(wait,alarmceased)",
                    "basicrobotwrapper(wait)",
                    "pickupdropouthandler(do_pickup)"
                )) >= 0)

                Assertions.assertTrue(to!!.checkNextContents(arrayOf( //pickup halted
                    "alarmreceivertest(wait,alarm)",
                    "pickupdropouthandler(halt_pickup)",
                )) >= 0)
                Assertions.assertTrue(to!!.checkNextContents(arrayOf( //pickup resumed
                    "alarmreceivertest(wait,alarmceased)",
                    "pickupdropouthandler(resume_pickup)",
                )) >= 0)

                Assertions.assertTrue(to!!.checkNextContents(arrayOf( //dropout halted
                    "alarmreceivertest(wait,alarm)",
                    "pickupdropouthandler(halt_dropout)",
                )) >= 0)
                Assertions.assertTrue(to!!.checkNextContents(arrayOf( //dropout resumed
                    "alarmreceivertest(wait,alarmceased)",
                    "pickupdropouthandler(resume_dropout)",
                )) >= 0)


            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_accepted ERROR:" + e.message)
                Assertions.fail()
            }
        }
    }



}