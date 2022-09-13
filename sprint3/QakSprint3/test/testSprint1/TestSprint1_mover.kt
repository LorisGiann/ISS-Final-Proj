package testSprint1

import alice.tuprolog.Struct
import alice.tuprolog.Term
import org.junit.Assert
import org.junit.jupiter.api.*
import testCommon.TestObserver
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.IOException
import java.time.Duration
import kotlin.test.Test
import testCommon.*

//@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestSprint1_mover {
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
		//CommUtils.delay(1000)

        val to = TestObserver()
        to.establishCoapConn("mover")
        //to.establishCoapConn("mover180turn")
        to.establishCoapConn("moveruturn")
        to.establishCoapConn("basicrobotwrapper")
        this.to = to
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {
        ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)

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

    /*
    Wait until all the connected machines are in the initial expected state
     */
    fun waitRegimeState(){
        val o = to!!
        o.waitUntilState("mover","wait")
        //o.waitUntilState("mover180turn","wait")
        o.waitUntilState("moveruturn","wait")
        o.waitUntilState("basicrobotwrapper","wait")
        CommUtils.delay(50)  //concede a little extra time to the actors in the system to communicate and do their transition, before going ahead with the rest of the test
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
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(10)) {
            while (true) {
                println(to!!.getCurrentCoapState("mover"))
                println(to!!.getCurrentCoapState("moveruturn"))
                println(to!!.getCurrentCoapState("basicrobotwrapper"))
                CommUtils.delay(1000)
            }
        }
        Assert.assertTrue(true)
        /*to!!.debugSetHistory(
            listOf("wasteservice(wait,0.0,0.0)","depositaction(wait)","transporttrolley(wait)","pickupdropouthandler(wait)","mover(wait,HOME,HOME)","wasteservice(handle_req,0.0,0.0)","wasteservice(req_depositaction,0.0,2.0)","depositaction(req_move_indoor,GLASS)","transporttrolley(req_move,INDOOR)","mover(handle,HOME,INDOOR)","mover(req_forward,HOME,INDOOR)","mover(chk_forward,HOME,INDOOR)","mover(req_turn,HOME,INDOOR)","mover(chk_turn,HOME,INDOOR)","mover(reply,INDOOR,INDOOR)","transporttrolley(chk_move)","mover(handle,INDOOR,INDOOR)","transporttrolley(wait)","depositaction(chk_move_indoor,GLASS)","mover(wait,INDOOR,INDOOR)","depositaction(req_pickup,GLASS)","transporttrolley(req_pickup)","pickupdropouthandler(do_pickup)","transporttrolley(chk_pickup)","pickupdropouthandler(wait)","transporttrolley(wait)","depositaction(chk_pickup,GLASS)","depositaction(reply,GLASS)","wasteservice(chk_depositaction,0.0,2.0)","transporttrolley(req_move,GLASSBOX)","mover(handle,INDOOR,GLASSBOX)","mover(req_forward,INDOOR,GLASSBOX)","depositaction(req_move_container,GLASS)","wasteservice(wait,0.0,2.0)","mover(chk_forward,INDOOR,GLASSBOX)","mover(req_turn,INDOOR,GLASSBOX)","mover(chk_turn,INDOOR,GLASSBOX)","mover(reply,PLASTICBOX,GLASSBOX)","mover(handle,PLASTICBOX,GLASSBOX)","mover(req_forward,PLASTICBOX,GLASSBOX)","mover(chk_forward,PLASTICBOX,GLASSBOX)","mover(req_turn,PLASTICBOX,GLASSBOX)","mover(chk_turn,PLASTICBOX,GLASSBOX)","transporttrolley(chk_move)","mover(reply,GLASSBOX,GLASSBOX)","mover(handle,GLASSBOX,GLASSBOX)","depositaction(chk_pickup,GLASS)","depositaction(req_dropout,GLASS)","transporttrolley(wait)","mover(wait,GLASSBOX,GLASSBOX)","pickupdropouthandler(do_dropout)","transporttrolley(req_dropout)","transporttrolley(chk_dropout)","depositaction(chk_dropout,GLASS)","pickupdropouthandler(wait)","transporttrolley(wait)","depositaction(next_move,GLASS)","transporttrolley(req_move,HOME)","depositaction(move_home,GLASS)","mover(handle,GLASSBOX,HOME)","mover(req_forward,GLASSBOX,HOME)","mover(chk_forward,GLASSBOX,HOME)","mover(req_turn,GLASSBOX,HOME)","mover(chk_turn,GLASSBOX,HOME)","mover(reply,HOME,HOME)","transporttrolley(chk_move)","transporttrolley(wait)","mover(handle,HOME,HOME)","mover(wait,HOME,HOME)","depositaction(wait)").toMutableList()
        )*/

        /*//CHECKING HISTORY
        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //wasteservice receives the request and commands the depositaction: robot moves to indoor
            "wasteservice(req_depositaction,0.0,2.0)",
            "transporttrolley(req_move,INDOOR)",
        )) >= 0)
        Assertions.assertTrue(to!!.checkNextContent("mover(req_forward,HOME,INDOOR)") >= 0) //moving towards INDOOR
        Assertions.assertTrue(to!!.checkNextContent("mover(req_turn,HOME,INDOOR)") >= 0) //moving towards INDOOR
        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrives to INDOOR, and does the pickup
            "mover(wait,INDOOR,INDOOR)",
            "pickupdropouthandler(do_pickup)",
        )) >= 0)
        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //depositaction reply to transporttrolley with pickupdone, wasteservice now ready to receive new request, robot heading to GLASSBOX
            "depositaction(reply,GLASS)",
            "wasteservice(wait,0.0,2.0)",
            "transporttrolley(req_move,GLASSBOX)",
            "mover(req_forward,INDOOR,GLASSBOX)"
        )).also {println(it)} >= 0)
        //Assertions.assertTrue(to!!.checkNextContent("mover(req_forward,INDOOR,GLASSBOX)") >= 0) //moving towards GLASSBOX
        Assertions.assertTrue(to!!.checkNextContent("mover(req_turn,INDOOR,GLASSBOX)") >= 0) //moving towards GLASSBOX
        Assertions.assertTrue(to!!.checkNextContent("mover(req_forward,PLASTICBOX,GLASSBOX)") >= 0) //moving towards GLASSBOX
        Assertions.assertTrue(to!!.checkNextContent("mover(req_turn,PLASTICBOX,GLASSBOX)") >= 0) //moving towards GLASSBOX
        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrival to GLASSBOX, robot does the dropout
            "mover(wait,GLASSBOX,GLASSBOX)",
            "pickupdropouthandler(do_dropout)",
        )) >= 0)
        Assertions.assertTrue(to!!.checkNextContent("transporttrolley(req_move,HOME)") >= 0) //robot heading to HOME
        Assertions.assertTrue(to!!.checkNextContent("mover(wait,HOME,HOME)") >= 0) //robot in HOME
*/
    }

    @Test
    fun test_from_H_to_H() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(10)){
            ColorsOut.outappl("test_from_H_to_H STARTS", ColorsOut.BLUE)

            val connTcp = ConnTcp("localhost", 8096)
            waitRegimeState()

            val requestStr = "msg(moveto, request,python,mover,moveto(HOME),1)"
            val answer = connTcp.request(requestStr)
            ColorsOut.outappl("test_from_H_to_H answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("movetoanswer(OK)"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            //no real action should happen
            Assertions.assertTrue(to!!.checkNextContent("mover(handle,HOME,HOME,ACLK)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(wait,HOME,HOME,ACLK)") >= 0)
            Assertions.assertFalse(to!!.checkNextContent("mover(*)") >= 0)

            to!!.nextCheckIndex=0
            Assertions.assertFalse(to!!.checkNextContent("basicrobotwrapper(handle,*)") >= 0)
        }
    }

    @Test
    fun test_from_H_to_I() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(30)){
            ColorsOut.outappl("test_from_H_to_I STARTS", ColorsOut.BLUE)

            val connTcp = ConnTcp("localhost", 8096)
            waitRegimeState()

            val requestStr = "msg(moveto, request,python,mover,moveto(INDOOR),1)"
            val answer = connTcp.request(requestStr)
            ColorsOut.outappl("test_accepted answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("movetoanswer(OK)"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertTrue(to!!.checkNextContent("mover(handle,HOME,INDOOR,ACLK)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(req_forward_aclk,HOME,INDOOR,ACLK)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(wait,INDOOR,INDOOR,ACLK)") >= 0)

            to!!.nextCheckIndex=0
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,w)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,l)") >= 0)

        }
    }

    @Test
    fun test_from_PB_to_I() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(40)){
            ColorsOut.outappl("test_from_PB_to_I STARTS", ColorsOut.BLUE)

            goTo(ws.Position.PLASTICBOX)

            val connTcp = ConnTcp("localhost", 8096)
            waitRegimeState()

            val requestStr = "msg(moveto, request,python,mover,moveto(INDOOR),1)"
            val answer = connTcp.request(requestStr)
            ColorsOut.outappl("test_accepted answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("movetoanswer(OK)"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertTrue(to!!.checkNextContent("mover(handle,PLASTICBOX,INDOOR,*)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(req_forward_clk,PLASTICBOX,INDOOR,CLK)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(wait,INDOOR,INDOOR,CLK)") >= 0)

            to!!.nextCheckIndex=0
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,l)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,w)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,r)") >= 0)

        }
    }

    @Test
    fun test_from_I_to_GB() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(40)){
            ColorsOut.outappl("test_from_I_to_GB STARTS", ColorsOut.BLUE)

            goTo(ws.Position.INDOOR)

            val connTcp = ConnTcp("localhost", 8096)
            waitRegimeState()

            val requestStr = "msg(moveto, request,python,mover,moveto(GLASSBOX),1)"
            val answer = connTcp.request(requestStr)
            ColorsOut.outappl("test_accepted answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("movetoanswer(OK)"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertTrue(to!!.checkNextContent("mover(handle,INDOOR,GLASSBOX,*)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(handle,PLASTICBOX,GLASSBOX,ACLK)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(wait,GLASSBOX,GLASSBOX,ACLK)") >= 0)

        }
    }

    @Test
    fun test_new_pos_no_chenage_route_from_GB_to_H_then_I() { //initially a GB->H move is commanded, but halfway through the nel position I is commanded: route should not change (no u turns)
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(50)){
            ColorsOut.outappl("test_new_pos_no_chenage_route_from_GB_to_H_then_I STARTS", ColorsOut.BLUE)

            goTo(ws.Position.GLASSBOX)

            val connTcp = ConnTcp("localhost", 8096)
            waitRegimeState()

            //FIRST REQUEST
            var requestStr = "msg(moveto, request,python,mover,moveto(HOME),1)"
            connTcp.forward(requestStr)
            to!!.waitUntilState("mover","mover(req_forward_aclk,GLASSBOX,HOME,ACLK)") //wait for the robot to proceed from PLASTICBOX to HOME
            CommUtils.delay(250)  //wait to be roughly in the middle of the wall

            //SECOND REQUEST
            requestStr = "msg(moveto, request,python,mover,moveto(INDOOR),1)"
            val answer = connTcp.request(requestStr)
            ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("movetoanswer(OK)"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertTrue(to!!.checkNextContent("mover(handle,GLASSBOX,HOME,*)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(set_new_dest_aclk,GLASSBOX,INDOOR,ACLK)") >= 0)
            val currIndex = to!!.nextCheckIndex
            Assertions.assertTrue(to!!.checkNextContent("mover(handle,HOME,INDOOR,ACLK)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(wait,INDOOR,INDOOR,ACLK)") >= 0)
            to!!.nextCheckIndex = currIndex
            Assertions.assertFalse(to!!.checkNextContent("mover(handle,HOME,HOME,*)") >= 0)

        }
    }

    @Test
    fun test_new_pos_chenage_route_from_PB_to_H_then_I() { //initially a PB->H move is commanded, but halfway through the nel position I is commanded: route should change (u turn)
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(50)){
            ColorsOut.outappl("test_new_pos_no_chenage_route_from_GB_to_H_then_I STARTS", ColorsOut.BLUE)

            goTo(ws.Position.PLASTICBOX)

            val connTcp = ConnTcp("localhost", 8096)
            waitRegimeState()

            //FIRST REQUEST
            var requestStr = "msg(moveto, request,python,mover,moveto(HOME),1)"
            connTcp.forward(requestStr)
            to!!.waitUntilState("mover","mover(req_forward_aclk,PLASTICBOX,HOME,ACLK)") //wait for the robot to proceed from PLASTICBOX to HOME
            CommUtils.delay(250)  //wait to be roughly in the middle of the wall

            //SECOND REQUEST
            requestStr = "msg(moveto, request,python,mover,moveto(INDOOR),1)"
            val answer = connTcp.request(requestStr)
            ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("movetoanswer(OK)"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertTrue(to!!.checkNextContent("mover(handle,PLASTICBOX,HOME,*)") >= 0)
            val currIndex = to!!.nextCheckIndex
            Assertions.assertTrue(to!!.checkNextContent("mover(set_new_dest_aclk,PLASTICBOX,INDOOR,ACLK)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(handle,PLASTICBOX,INDOOR,CLK)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(handle,INDOOR,INDOOR,CLK)") >= 0)
            to!!.nextCheckIndex = currIndex
            Assertions.assertFalse(to!!.checkNextContent("mover(handle,PLASTICBOX,PLASTICBOX,*)") >= 0)

            to!!.nextCheckIndex=0
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,w)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,l)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,l)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,w)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,r)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,w)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(handle,r)") >= 0)

        }
    }
}