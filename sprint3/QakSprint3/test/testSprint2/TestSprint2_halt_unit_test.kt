package testSprint2

import it.unibo.kactor.MsgUtil
import org.junit.Assert
import org.junit.jupiter.api.*
import testCommon.*
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.IOException
import java.time.Duration
import kotlin.test.Test

//@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestSprint2_halt_unit_test {
    private var to: TestObserver? = null
    private var processHandleServer: ProcessHandle? = null
    private var processHandleRobot: ProcessHandle? = null
	private var processHandleAlarm: ProcessHandle? = null
    private var prServer: Process? = null
    private var prRobot: Process? = null
	private var prAlarm: Process? = null

    companion object {
        /*@JvmStatic
        @BeforeAll
        @Throws(IOException::class, InterruptedException::class)
        fun compileCtxs() {
            TestUtils.compileCtx("it.unibo.ctxrobot.MainCtxrobotKt")
            TestUtils.compileCtx("it.unibo.ctxserver.MainCtxserverKt")
        }*/
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
        to.establishCoapConn("basicrobotwrapper")
        to.establishCoapConn("basicrobotlorisdavide")
        to.establishCoapConn("pickupdropouthandler")
        to.establishCoapConn("sonarlorisdavide")
        //to.establishCoapConn("led")
        to.establishCoapConn("commandissuerfortests")
        to.establishCoapConn("alarmreceivertest")
        this.to = to
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {
        ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
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

    /*
    Wait until all the connected machines are in the initial expected state
     */
    fun waitRegimeState(){
        val o = to!!
        o.waitUntilState("basicrobotwrapper","wait")
        o.waitUntilState("basicrobotlorisdavide","work")
        o.waitUntilState("pickupdropouthandler","wait")
        //o.waitUntilState("sonarlorisdavide","activateTheSonar")
        //o.waitUntilState("led","s0")
        o.waitUntilState("commandissuerfortests","wait")
        CommUtils.delay(50)  //concede a little extra time to the actors in the system to communicate and do their transition, before going ahead with the rest of the test
    }

    private fun homeAfterForward(connTcpRobot: ConnTcp) {
        connTcpRobot.request(MsgUtil.buildRequest("python","cmdsync","cmdsync(r)","commandissuerfortests").toString())
        connTcpRobot.request(MsgUtil.buildRequest("python","cmdsync","cmdsync(r)","commandissuerfortests").toString())
        connTcpRobot.request(MsgUtil.buildRequest("python","cmdsync","cmdsync(w)","commandissuerfortests").toString())
        connTcpRobot.request(MsgUtil.buildRequest("python","cmdsync","cmdsync(r)","commandissuerfortests").toString())
        connTcpRobot.request(MsgUtil.buildRequest("python","cmdsync","cmdsync(r)","commandissuerfortests").toString())
    }

    @Test
    fun dummy() {
        /*assertTimeoutPreemptively<Unit>(Duration.ofSeconds(10)) {
            while (true) {
                println(to!!.getCurrentCoapState("mover"))
                println(to!!.getCurrentCoapState("basicrobotwrapper"))
                CommUtils.delay(1000)
            }
        }*/
        Assert.assertTrue(true)
        to!!.debugSetHistory(
            listOf("wasteservice(wait,0.0,0.0)","depositaction(wait)","transporttrolley(wait)","mover(wait,HOME,HOME,ACLK)","basicrobotwrapper(wait)","basicrobotlorisdavide(work)","pickupdropouthandler(wait)","sonarlorisdavide(activateTheSonar,simulate)","       ActorBasic(Resource) alarmreceivertest  | created  , sonarlorisdavide(handleSonarData,simulate,20)","sonarlorisdavide(handleSonarData,simulate,20)","sonarlorisdavide(deactivateTheSonar,simulate)","sonarlorisdavide(end,simulate)","wasteservice(handle_req,0.0,0.0)","wasteservice(req_depositaction,0.0,2.0)","depositaction(req_move_indoor,GLASS)","transporttrolley(req_move,INDOOR)","mover(handle,HOME,INDOOR,ACLK)","mover(aclk_or_clk,HOME,INDOOR,ACLK)","mover(prepare_aclk,HOME,INDOOR,ACLK)","mover(req_forward_aclk,HOME,INDOOR,ACLK)","basicrobotwrapper(handle,w)","basicrobotwrapper(forward_cmd)","basicrobotlorisdavide(execcmd,w)","basicrobotlorisdavide(work)","alarmreceivertest(wait,alarm)","pickupdropouthandler(alarm)","basicrobotwrapper(halt_forward)","basicrobotlorisdavide(execcmd,h)","basicrobotlorisdavide(work)","alarmreceivertest(wait,alarmceased)","pickupdropouthandler(handle_prio)","basicrobotwrapper(forward_cmd)","basicrobotlorisdavide(execcmd,w)","basicrobotlorisdavide(work)","pickupdropouthandler(wait)","basicrobotlorisdavide(handleObstacle,unkknown)","basicrobotwrapper(collision)","basicrobotlorisdavide(work)","mover(chk_forward_aclk,HOME,INDOOR,ACLK)","basicrobotwrapper(handle_prio)","mover(req_post_turn_aclk,HOME,INDOOR,ACLK)","basicrobotwrapper(wait)","basicrobotwrapper(handle,l)","basicrobotwrapper(other_cmd,l)","basicrobotlorisdavide(execcmd,l)","basicrobotlorisdavide(work)","alarmreceivertest(wait,alarm)","pickupdropouthandler(alarm)","basicrobotwrapper(handle_prio)","basicrobotwrapper(alarm)","mover(chk_post_turn_aclk,HOME,INDOOR,ACLK)","mover(update_aclk,INDOOR,INDOOR,ACLK)","mover(reply,INDOOR,INDOOR,ACLK)","transporttrolley(chk_move)","depositaction(chk_move_indoor,GLASS)","transporttrolley(wait)","mover(handle,INDOOR,INDOOR,ACLK)","depositaction(req_pickup,GLASS)","mover(wait,INDOOR,INDOOR,ACLK)","transporttrolley(req_pickup)","alarmreceivertest(wait,alarmceased)","pickupdropouthandler(handle_prio)","basicrobotwrapper(handle_prio)","pickupdropouthandler(wait)","pickupdropouthandler(do_pickup)","basicrobotwrapper(wait)","alarmreceivertest(wait,alarm)","pickupdropouthandler(halt_pickup)","basicrobotwrapper(alarm)","alarmreceivertest(wait,alarmceased)","pickupdropouthandler(resume_pickup)","basicrobotwrapper(handle_prio)","basicrobotwrapper(wait)","transporttrolley(chk_pickup)","pickupdropouthandler(done_pickup)","pickupdropouthandler(wait)","transporttrolley(wait)","depositaction(chk_pickup,GLASS)","wasteservice(chk_depositaction,0.0,2.0)","depositaction(reply,GLASS)","wasteservice(wait,0.0,2.0)","transporttrolley(req_move,GLASSBOX)","depositaction(req_move_container,GLASS)","mover(handle,INDOOR,GLASSBOX,ACLK)","basicrobotwrapper(handle,w)","mover(aclk_or_clk,INDOOR,GLASSBOX,ACLK)","basicrobotlorisdavide(execcmd,w)","basicrobotwrapper(forward_cmd)","mover(prepare_aclk,INDOOR,GLASSBOX,ACLK)","basicrobotlorisdavide(work)","mover(req_forward_aclk,INDOOR,GLASSBOX,ACLK)","basicrobotwrapper(collision)","basicrobotlorisdavide(handleObstacle,unkknown)","basicrobotlorisdavide(work)","mover(chk_forward_aclk,INDOOR,GLASSBOX,ACLK)","basicrobotwrapper(handle_prio)","mover(req_post_turn_aclk,INDOOR,GLASSBOX,ACLK)","basicrobotwrapper(wait)","basicrobotwrapper(handle,l)","basicrobotwrapper(other_cmd,l)","basicrobotlorisdavide(execcmd,l)","basicrobotlorisdavide(work)","mover(chk_post_turn_aclk,INDOOR,GLASSBOX,ACLK)","basicrobotwrapper(handle_prio)","mover(update_aclk,PLASTICBOX,GLASSBOX,ACLK)","mover(reply,PLASTICBOX,GLASSBOX,ACLK)","basicrobotwrapper(wait)","mover(handle,PLASTICBOX,GLASSBOX,ACLK)","mover(aclk_or_clk,PLASTICBOX,GLASSBOX,ACLK)","mover(prepare_aclk,PLASTICBOX,GLASSBOX,ACLK)","mover(req_forward_aclk,PLASTICBOX,GLASSBOX,ACLK)","basicrobotwrapper(handle,w)","basicrobotwrapper(forward_cmd)","basicrobotlorisdavide(execcmd,w)","basicrobotlorisdavide(work)","basicrobotlorisdavide(handleObstacle,unkknown)","basicrobotwrapper(collision)","basicrobotlorisdavide(work)","basicrobotwrapper(handle_prio)","mover(chk_forward_aclk,PLASTICBOX,GLASSBOX,ACLK)","mover(req_post_turn_aclk,PLASTICBOX,GLASSBOX,ACLK)","basicrobotwrapper(wait)","basicrobotwrapper(handle,l)","basicrobotwrapper(other_cmd,l)","basicrobotlorisdavide(execcmd,l)","basicrobotlorisdavide(work)","mover(chk_post_turn_aclk,PLASTICBOX,GLASSBOX,ACLK)","basicrobotwrapper(handle_prio)","mover(update_aclk,GLASSBOX,GLASSBOX,ACLK)","mover(reply,GLASSBOX,GLASSBOX,ACLK)","transporttrolley(chk_move)","transporttrolley(wait)","depositaction(chk_pickup,GLASS)","depositaction(req_dropout,GLASS)","transporttrolley(req_dropout)","pickupdropouthandler(do_dropout)","basicrobotwrapper(wait)","mover(handle,GLASSBOX,GLASSBOX,ACLK)","mover(wait,GLASSBOX,GLASSBOX,ACLK)","pickupdropouthandler(halt_dropout)","basicrobotwrapper(alarm)","alarmreceivertest(wait,alarm)","alarmreceivertest(wait,alarmceased)","pickupdropouthandler(resume_dropout)","basicrobotwrapper(handle_prio)","basicrobotwrapper(wait)","pickupdropouthandler(done_dropout)","transporttrolley(chk_dropout)","pickupdropouthandler(wait)","transporttrolley(wait)","depositaction(chk_dropout,GLASS)","depositaction(next_move,GLASS)","transporttrolley(req_move,HOME)","mover(handle,GLASSBOX,HOME,ACLK)","depositaction(move_home,GLASS)","mover(aclk_or_clk,GLASSBOX,HOME,ACLK)","mover(prepare_aclk,GLASSBOX,HOME,ACLK)","mover(req_forward_aclk,GLASSBOX,HOME,ACLK)","basicrobotwrapper(handle,w)","basicrobotwrapper(forward_cmd)","basicrobotlorisdavide(execcmd,w)","basicrobotlorisdavide(work)","basicrobotlorisdavide(handleObstacle,unkknown)","basicrobotwrapper(collision)","basicrobotlorisdavide(work)","mover(chk_forward_aclk,GLASSBOX,HOME,ACLK)","basicrobotwrapper(handle_prio)","mover(req_post_turn_aclk,GLASSBOX,HOME,ACLK)","basicrobotwrapper(wait)","basicrobotwrapper(handle,l)","basicrobotwrapper(other_cmd,l)","basicrobotlorisdavide(execcmd,l)","basicrobotlorisdavide(work)","basicrobotwrapper(handle_prio)","mover(chk_post_turn_aclk,GLASSBOX,HOME,ACLK)","mover(update_aclk,HOME,HOME,ACLK)","depositaction(wait)","mover(reply,HOME,HOME,ACLK)","transporttrolley(chk_move)","transporttrolley(wait)","basicrobotwrapper(wait)","mover(handle,HOME,HOME,ACLK)","mover(wait,HOME,HOME,ACLK)") as MutableList<String>
        )

        //CHECKING HISTORY
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
        Assertions.assertTrue(to!!.checkNextContents(arrayOf(//basicrobotwrapprer in analarm only after the turn. At this time a pickup will be also issued
            "basicrobotlorisdavide(work)",
            "basicrobotwrapper(alarm)",
            "transporttrolley(req_pickup)",
        )).also {println(it)} >= 0)
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

        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //dropdown halted
            "alarmreceivertest(wait,alarm)",
            "pickupdropouthandler(halt_dropout)",
        )) >= 0)
        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //dropdown halted
            "alarmreceivertest(wait,alarmceased)",
            "pickupdropouthandler(resume_dropout)",
        )) >= 0)


    }

    @Test
    fun test_halt_while_forward() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(20)){
            ColorsOut.outappl("test_halt_while_forward STARTS", ColorsOut.BLUE)

            val connTcpRobot = ConnTcp("localhost", 8096)
            val connTcpAlarm = ConnTcp("127.0.0.1", 8097)
            waitRegimeState()
            ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
            connTcpAlarm.forward(MsgUtil.buildDispatch("python","sonardeactivate","sonardeactivate(_)","sonarlorisdavide").toString())

            val requestStr = MsgUtil.buildRequest("python","cmdsync","cmdsync(w)","commandissuerfortests").toString()
            object : Thread(){
                override fun run(){
                    val answer = connTcpRobot.request(requestStr)
                    ColorsOut.outappl("test_halt_while_forward answer=$answer", ColorsOut.GREEN)
                    Assertions.assertTrue(answer.contains("cmdanswer(OK)"))
                }
            }.start()

            to!!.waitUntilState("basicrobotlorisdavide","basicrobotlorisdavide(execcmd,w)")
            CommUtils.delay(250)
            connTcpRobot.forward(MsgUtil.buildEvent("python","alarm","alarm(_)").toString())
            CommUtils.delay(500)
            connTcpRobot.forward(MsgUtil.buildEvent("python","alarmceased","alarmceased(_)").toString())

            to!!.waitUntilState("basicrobotwrapper","basicrobotwrapper(wait)")
            val beforeReturnHome = to!!.getHistory().size

            //return back home
            homeAfterForward(connTcpRobot)

            waitRegimeState()
            connTcpRobot.close()
            connTcpAlarm.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertTrue(to!!.checkNextContent("basicrobotlorisdavide(execcmd,w)") >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf(
                "alarmreceivertest(wait,alarm)",
                "basicrobotlorisdavide(execcmd,h)"
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //resume
                "alarmreceivertest(wait,alarmceased)",
                "basicrobotlorisdavide(execcmd,w)"
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContent("commandissuerfortests(chk_cmdsync)") >= 0) //reply arrives at the end
            Assertions.assertTrue(to!!.nextCheckIndex <= beforeReturnHome) //all of this should be before return home
        }
    }

    @Test
    fun test_halt_before_forward() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(20)){
            ColorsOut.outappl("test_halt_before_forward STARTS", ColorsOut.BLUE)

            val connTcpRobot = ConnTcp("localhost", 8096)
            val connTcpAlarm = ConnTcp("127.0.0.1", 8097)
            waitRegimeState()
            ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
            connTcpAlarm.forward(MsgUtil.buildDispatch("python","sonardeactivate","sonardeactivate(_)","sonarlorisdavide").toString())

            connTcpRobot.forward(MsgUtil.buildEvent("python","alarm","alarm(_)").toString())
            CommUtils.delay(100) //give time to register the alarm in the history
            val requestStr = MsgUtil.buildRequest("python","cmdsync","cmdsync(w)","commandissuerfortests").toString()
            object : Thread(){
                override fun run(){
                    val answer = connTcpRobot.request(requestStr)
                    ColorsOut.outappl("test_halt_before_forward answer=$answer", ColorsOut.GREEN)
                    Assertions.assertTrue(answer.contains("cmdanswer(OK)"))
                }
            }.start()

            CommUtils.delay(500)
            connTcpRobot.forward(MsgUtil.buildEvent("python","alarmceased","alarmceased(_)").toString())

            to!!.waitUntilState("basicrobotwrapper","basicrobotwrapper(handle,w)") //robot should now move forward
            to!!.waitUntilState("basicrobotwrapper","basicrobotwrapper(wait)")
            val beforeReturnHome = to!!.getHistory().size

            //return back home
            homeAfterForward(connTcpRobot)

            waitRegimeState()
            connTcpRobot.close()
            connTcpAlarm.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertFalse(to!!.checkNextSequence(arrayOf( //no resume forward
                "basicrobotlorisdavide(execcmd,w)",
                "basicrobotlorisdavide(execcmd,h)",
                "basicrobotlorisdavide(execcmd,w)",
            )))

            to!!.nextCheckIndex=0
            Assertions.assertTrue(to!!.checkNextContent("alarmreceivertest(wait,alarm)") >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf(
                "alarmreceivertest(wait,alarmceased)",
                "basicrobotlorisdavide(execcmd,w)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContent("commandissuerfortests(chk_cmdsync)") >= 0) //reply arrives at the end
            Assertions.assertTrue(to!!.nextCheckIndex <= beforeReturnHome) //all of this should be before return home
        }
    }

    @Test
    fun test_halt_while_turn() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(10)){
            ColorsOut.outappl("test_halt_while_turn STARTS", ColorsOut.BLUE)

            val connTcpRobot = ConnTcp("localhost", 8096)
            val connTcpAlarm = ConnTcp("127.0.0.1", 8097)
            waitRegimeState()
            ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
            connTcpAlarm.forward(MsgUtil.buildDispatch("python","sonardeactivate","sonardeactivate(_)","sonarlorisdavide").toString())

            val requestStr = MsgUtil.buildRequest("python","cmdsync","cmdsync(r)","commandissuerfortests").toString()
            object : Thread(){
                override fun run(){
                    val answer = connTcpRobot.request(requestStr)
                    ColorsOut.outappl("test_halt_while_turn answer=$answer", ColorsOut.GREEN)
                    Assertions.assertTrue(answer.contains("cmdanswer(OK)"))
                }
            }.start()

            to!!.waitUntilState("basicrobotlorisdavide","basicrobotlorisdavide(execcmd,r)")
            CommUtils.delay(10)
            connTcpRobot.forward(MsgUtil.buildEvent("python","alarm","alarm(_)").toString())
            CommUtils.delay(500)
            connTcpRobot.forward(MsgUtil.buildEvent("python","alarmceased","alarmceased(_)").toString())

            to!!.waitUntilState("basicrobotwrapper","basicrobotwrapper(wait)")
            val beforeReturnHome = to!!.getHistory().size

            //return back
            connTcpRobot.request(MsgUtil.buildRequest("python","cmdsync","cmdsync(l)","commandissuerfortests").toString())

            waitRegimeState()
            connTcpRobot.close()
            connTcpAlarm.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertFalse(to!!.checkNextSequence(arrayOf( //no resume turn
                "basicrobotlorisdavide(execcmd,r)",
                "basicrobotlorisdavide(execcmd,h)",
                "basicrobotlorisdavide(execcmd,r)",
            )))

            to!!.nextCheckIndex=0
            Assertions.assertTrue(to!!.checkNextContent("basicrobotlorisdavide(execcmd,r)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("alarmreceivertest(wait,alarm)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("commandissuerfortests(chk_cmdsync)") >= 0) //reply arrives before the alarm is ceased:
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(alarm)") >= 0) //the next command will infact be blocked if the alarm is still active
            Assertions.assertTrue(to!!.checkNextContents(arrayOf(
                "alarmreceivertest(wait,alarmceased)",
                "basicrobotwrapper(wait)",
            )) >= 0)
            Assertions.assertTrue(to!!.nextCheckIndex <= beforeReturnHome) //all of this should be before return home

        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ATTENTION @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        //unfortunately the basicrobot actually completes the move before the anarm signal arrives, since it only last about 300ms
        //however the basicrobotwrapper lenghen this time to about 600 ms (see basicrobotwrapper's other_cmd state), so if we do the
        //checks against the basicrobotwrapper everithing should be fine.
        //here an extract of the logs when the turn is done (look at the timestamps):
        //            2022-08-27 00:31:22,266 INFO  jar:93 - basicrobotlorisdavide in execcmd | msg(cmd,dispatch,basicrobotwrapper,basicrobotlorisdavide,cmd(r),35)
        //            2022-08-27 00:31:22,267 INFO  jar:93 - basicrobot | executing 'r'
        //            2022-08-27 00:31:22,692 INFO  jar:93 - 		[34mWsHttpConnection | forward answer:{"endmove":true,"move":"turnRight"} DISCARDED since HTTP[0m
        //            2022-08-27 00:31:22,695 INFO  jar:93 - basicrobot | waiting ..............
        //            2022-08-27 00:31:22,717 INFO  jar:93 - alarmreceiverpickupdropdown in wait | msg(alarm,event,python,none,alarm(_161281565),3)
        //            2022-08-27 00:31:22,722 INFO  jar:93 - alarmreceiverbasicrobot in wait | msg(alarm,event,python,none,alarm(_161281565),3)
        //            2022-08-27 00:31:22,722 INFO  jar:93 - alarmreceivertest in wait | msg(alarm,event,python,none,alarm(_161281565),3)
        //            2022-08-27 00:31:22,727 INFO  jar:93 - pickupdropouthandler in alarm | msg(alarm,dispatch,alarmreceiverpickupdropdown,pickupdropouthandler,alarm(_1917784974),36)
        //            2022-08-27 00:31:22,865 INFO  jar:93 - basicrobotwrapper in handle_prio | msg(local_noMsg,event,basicrobotwrapper,none,noMsg,23)
        //            2022-08-27 00:31:22,894 INFO  jar:93 - commandissuerfortests in chk_cmdsync | msg(cmdanswer,reply,basicrobotwrapper,commandissuerfortests,cmdanswer(OK),38)
        }
    }

    @Test
    fun test_halt_before_turn() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(10)){
            ColorsOut.outappl("test_halt_before_turn STARTS", ColorsOut.BLUE)

            val connTcpRobot = ConnTcp("localhost", 8096)
            val connTcpAlarm = ConnTcp("127.0.0.1", 8097)
            waitRegimeState()
            ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
            connTcpAlarm.forward(MsgUtil.buildDispatch("python","sonardeactivate","sonardeactivate(_)","sonarlorisdavide").toString())

            connTcpRobot.forward(MsgUtil.buildEvent("python","alarm","alarm(_)").toString())
            CommUtils.delay(100) //give time to register the alarm in the history
            val requestStr = MsgUtil.buildRequest("python","cmdsync","cmdsync(r)","commandissuerfortests").toString()
            object : Thread(){
                override fun run(){
                    val answer = connTcpRobot.request(requestStr)
                    ColorsOut.outappl("test_halt_before_turn answer=$answer", ColorsOut.GREEN)
                    Assertions.assertTrue(answer.contains("cmdanswer(OK)"))
                }
            }.start()

            CommUtils.delay(500)
            connTcpRobot.forward(MsgUtil.buildEvent("python","alarmceased","alarmceased(_)").toString())

            to!!.waitUntilState("basicrobotwrapper","basicrobotwrapper(handle,r)") //robot should now turn
            to!!.waitUntilState("basicrobotwrapper","basicrobotwrapper(wait)")
            val beforeReturnHome = to!!.getHistory().size

            //return back
            connTcpRobot.request(MsgUtil.buildRequest("python","cmdsync","cmdsync(l)","commandissuerfortests").toString())

            waitRegimeState()
            connTcpRobot.close()
            connTcpAlarm.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertFalse(to!!.checkNextSequence(arrayOf( //no resume turn
                "basicrobotlorisdavide(execcmd,r)",
                "basicrobotlorisdavide(execcmd,h)",
                "basicrobotlorisdavide(execcmd,r)",
            )))

            to!!.nextCheckIndex=0
            Assertions.assertTrue(to!!.checkNextContent("alarmreceivertest(wait,alarm)") >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf(
                "alarmreceivertest(wait,alarmceased)",
                "basicrobotlorisdavide(execcmd,r)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContent("commandissuerfortests(chk_cmdsync)") >= 0) //reply arrives at the end
            Assertions.assertTrue(to!!.nextCheckIndex <= beforeReturnHome) //all of this should be before return home
        }
    }

    @Test
    fun test_halt_while_pickup() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(10)){
            ColorsOut.outappl("test_halt_while_pickup STARTS", ColorsOut.BLUE)

            val connTcpRobot = ConnTcp("localhost", 8096)
            val connTcpAlarm = ConnTcp("127.0.0.1", 8097)
            waitRegimeState()
            ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
            connTcpAlarm.forward(MsgUtil.buildDispatch("python","sonardeactivate","sonardeactivate(_)","sonarlorisdavide").toString())

            val requestStr = MsgUtil.buildRequest("python","pickup","pickup(_)","commandissuerfortests").toString()
            object : Thread(){
                override fun run(){
                    val answer = connTcpRobot.request(requestStr)
                    ColorsOut.outappl("test_halt_while_pickup answer=$answer", ColorsOut.GREEN)
                    Assertions.assertTrue(answer.contains("pickupanswer(OK)"))
                }
            }.start()

            CommUtils.delay(100)
            connTcpRobot.forward(MsgUtil.buildEvent("python","alarm","alarm(_)").toString())
            CommUtils.delay(500)
            connTcpRobot.forward(MsgUtil.buildEvent("python","alarmceased","alarmceased(_)").toString())

            waitRegimeState()
            connTcpRobot.close()
            connTcpAlarm.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertTrue(to!!.checkNextContent("pickupdropouthandler(do_pickup)") >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf(
                "alarmreceivertest(wait,alarm)",
                "pickupdropouthandler(halt_pickup)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf(
                "alarmreceivertest(wait,alarmceased)",
                "pickupdropouthandler(resume_pickup)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContent("commandissuerfortests(chk_pickup)") >= 0) //reply arrives at the end
        }
    }

    @Test
    fun test_halt_before_pickup() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(10)){
            ColorsOut.outappl("test_halt_before_pickup STARTS", ColorsOut.BLUE)

            val connTcpRobot = ConnTcp("localhost", 8096)
            val connTcpAlarm = ConnTcp("127.0.0.1", 8097)
            waitRegimeState()
            ColorsOut.outappl("Deactivate the sonar", ColorsOut.BLUE)
            connTcpAlarm.forward(MsgUtil.buildDispatch("python","sonardeactivate","sonardeactivate(_)","sonarlorisdavide").toString())

            connTcpRobot.forward(MsgUtil.buildEvent("python","alarm","alarm(_)").toString())
            CommUtils.delay(100) //give time to register the alarm in the history
            val requestStr = MsgUtil.buildRequest("python","pickup","pickup(_)","commandissuerfortests").toString()
            object : Thread(){
                override fun run(){
                    val answer = connTcpRobot.request(requestStr)
                    ColorsOut.outappl("test_halt_before_pickup answer=$answer", ColorsOut.GREEN)
                    Assertions.assertTrue(answer.contains("pickupanswer(OK)"))
                }
            }.start()

            CommUtils.delay(500)
            connTcpRobot.forward(MsgUtil.buildEvent("python","alarmceased","alarmceased(_)").toString())

            to!!.waitUntilState("pickupdropouthandler","pickupdropouthandler(do_pickup)") //tt should now pickup

            waitRegimeState()
            connTcpRobot.close()
            connTcpAlarm.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertFalse(to!!.checkNextSequence(arrayOf( //no resume turn
                "pickupdropouthandler(do_pickup)",
                "pickupdropouthandler(halt_pickup)",
                "pickupdropouthandler(resume_pickup)",
            )))

            to!!.nextCheckIndex=0
            Assertions.assertTrue(to!!.checkNextContent("alarmreceivertest(wait,alarm)") >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf(
                "alarmreceivertest(wait,alarmceased)",
                "pickupdropouthandler(do_pickup)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContent("commandissuerfortests(chk_pickup)") >= 0) //reply arrives at the end
        }
    }

}