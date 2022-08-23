package testSprint1

import alice.tuprolog.Struct
import alice.tuprolog.Term
import org.junit.Assert
import org.junit.jupiter.api.*
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.IOException
import java.time.Duration
import kotlin.test.Test

//@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestSprint1_integration_test {
    private var to: TestObserver? = null
    private var processHandleServer: ProcessHandle? = null
    private var processHandleRobot: ProcessHandle? = null
    private var prServer: Process? = null
    private var prRobot: Process? = null

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
            TestUtils.terminateProcOnPort(8096) //making sure that the port is free
            TestUtils.terminateProcOnPort(8095) //making sure that the port is free

            val (prR, processHandleR) = TestUtils.runCtx("build/libs/it.unibo.ctxrobot.MainCtxrobotKt-1.0.jar")
            prRobot=prR; processHandleRobot=processHandleR
            val (prS, processHandleS) = TestUtils.runCtx("build/libs/it.unibo.ctxserver.MainCtxserverKt-1.0.jar")
            prServer=prS; processHandleServer=processHandleS
        } catch (e: IOException) {
            ColorsOut.outappl("Errore launch ", ColorsOut.RED)
            System.exit(1)
        }

        val to = TestObserver()
        to.establishCoapConn("wasteservice");
        to.establishCoapConn("depositaction");
        to.establishCoapConn("transporttrolley");
        to.establishCoapConn("pickupdropouthandler");
        to.establishCoapConn("mover");
        this.to = to
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {
        ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)

        //reorientate the robot correctly in ACLK direction
        val currentState = to!!.getCurrentCoapState("mover")
        val orientation  = (Term.createTerm(currentState) as Struct).getArg(3).toString()
        if(orientation=="CLK") {
            val connTcp = ConnTcp("localhost", 8096)
            val requestStr = "msg(cmdsync, request,python,basicrobotwrapper,cmdsync(r),1)"
            connTcp.request(requestStr)
        }

        try {
            //FIRSTLY, try to be nice and make the program exit "normally"
            processHandleRobot!!.destroy()
            prRobot!!.destroy()
            processHandleServer!!.destroy()
            prServer!!.destroy()
            CommUtils.delay(1000)
            //since sometime this isn't enough, do it the heavy way...
            processHandleRobot!!.destroyForcibly()
            processHandleServer!!.destroyForcibly()
        }catch(_:  NullPointerException){ }

        to!!.closeAllConnections()
    }

    /*
    Wait until all the connected machines are in the initial expected state
     */
    fun waitRegimeState(){
        val o = to!!
        o.waitUntilState("wasteservice","wait");
        o.waitUntilState("depositaction","wait");
        o.waitUntilState("transporttrolley","wait");
        o.waitUntilState("pickupdropouthandler","wait");
        o.waitUntilState("mover","wait");
        CommUtils.delay(50)  //concede a little extra time to the actors in the system to communicate and do their transition, before going ahead with the test requests
    }




    @Test
    fun dummy() {
        Assert.assertTrue(true)
        to!!.debugSetHistory(
            listOf("wasteservice(wait,0.0,0.0)","depositaction(wait)","transporttrolley(wait)","pickupdropouthandler(wait)","mover(wait,HOME,HOME)","wasteservice(handle_req,0.0,0.0)","wasteservice(req_depositaction,0.0,2.0)","depositaction(req_move_indoor,GLASS)","transporttrolley(req_move,INDOOR)","mover(handle,HOME,INDOOR)","mover(req_forward,HOME,INDOOR)","mover(chk_forward,HOME,INDOOR)","mover(req_turn,HOME,INDOOR)","mover(chk_turn,HOME,INDOOR)","mover(reply,INDOOR,INDOOR)","transporttrolley(chk_move)","mover(handle,INDOOR,INDOOR)","transporttrolley(wait)","depositaction(chk_move_indoor,GLASS)","mover(wait,INDOOR,INDOOR)","depositaction(req_pickup,GLASS)","transporttrolley(req_pickup)","pickupdropouthandler(do_pickup)","transporttrolley(chk_pickup)","pickupdropouthandler(wait)","transporttrolley(wait)","depositaction(chk_pickup,GLASS)","depositaction(reply,GLASS)","wasteservice(chk_depositaction,0.0,2.0)","transporttrolley(req_move,GLASSBOX)","mover(handle,INDOOR,GLASSBOX)","mover(req_forward,INDOOR,GLASSBOX)","depositaction(req_move_container,GLASS)","wasteservice(wait,0.0,2.0)","mover(chk_forward,INDOOR,GLASSBOX)","mover(req_turn,INDOOR,GLASSBOX)","mover(chk_turn,INDOOR,GLASSBOX)","mover(reply,PLASTICBOX,GLASSBOX)","mover(handle,PLASTICBOX,GLASSBOX)","mover(req_forward,PLASTICBOX,GLASSBOX)","mover(chk_forward,PLASTICBOX,GLASSBOX)","mover(req_turn,PLASTICBOX,GLASSBOX)","mover(chk_turn,PLASTICBOX,GLASSBOX)","transporttrolley(chk_move)","mover(reply,GLASSBOX,GLASSBOX)","mover(handle,GLASSBOX,GLASSBOX)","depositaction(chk_pickup,GLASS)","depositaction(req_dropout,GLASS)","transporttrolley(wait)","mover(wait,GLASSBOX,GLASSBOX)","pickupdropouthandler(do_dropout)","transporttrolley(req_dropout)","transporttrolley(chk_dropout)","depositaction(chk_dropout,GLASS)","pickupdropouthandler(wait)","transporttrolley(wait)","depositaction(next_move,GLASS)","transporttrolley(req_move,HOME)","depositaction(move_home,GLASS)","mover(handle,GLASSBOX,HOME)","mover(req_forward,GLASSBOX,HOME)","mover(chk_forward,GLASSBOX,HOME)","mover(req_turn,GLASSBOX,HOME)","mover(chk_turn,GLASSBOX,HOME)","mover(reply,HOME,HOME)","transporttrolley(chk_move)","transporttrolley(wait)","mover(handle,HOME,HOME)","mover(wait,HOME,HOME)","depositaction(wait)").toMutableList()
        )

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
    fun test_accepted() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)){
            ColorsOut.outappl("test_accepted STARTS", ColorsOut.BLUE)
            val requestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"

            val connTcp = ConnTcp("localhost", 8095)
            waitRegimeState()

            val answer = connTcp.request(requestStr)
            ColorsOut.outappl("test_accepted answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("loadaccept"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //wasteservice receives the request and commands the depositaction: robot moves to indoor
                "wasteservice(req_depositaction,0.0,2.0)",
                "transporttrolley(req_move,INDOOR)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(req_forward_aclk,HOME,INDOOR,ACLK)") >= 0) //moving towards INDOOR
            Assertions.assertTrue(to!!.checkNextContent("mover(req_post_turn_aclk,HOME,INDOOR,ACLK)") >= 0) //moving towards INDOOR
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrives to INDOOR, and does the pickup
                "mover(wait,INDOOR,INDOOR,ACLK)",
                "pickupdropouthandler(do_pickup)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //depositaction reply to transporttrolley with pickupdone, wasteservice now ready to receive new request, robot heading to GLASSBOX
                "depositaction(reply,GLASS)",
                "wasteservice(wait,0.0,2.0)",
                "transporttrolley(req_move,GLASSBOX)",
                "mover(req_forward_aclk,INDOOR,GLASSBOX,ACLK)"
            )) >= 0)
            //Assertions.assertTrue(to!!.checkNextContent("mover(req_forward_aclk,INDOOR,GLASSBOX,ACLK)") >= 0) //moving towards GLASSBOX   -- HAS TO BE INCLUDED IN THE CASES ABOVE, as it happens simultaneously to the others
            Assertions.assertTrue(to!!.checkNextContent("mover(req_post_turn_aclk,INDOOR,GLASSBOX,ACLK)") >= 0) //moving towards GLASSBOX
            Assertions.assertTrue(to!!.checkNextContent("mover(req_forward_aclk,PLASTICBOX,GLASSBOX,ACLK)") >= 0) //moving towards GLASSBOX
            Assertions.assertTrue(to!!.checkNextContent("mover(req_post_turn_aclk,PLASTICBOX,GLASSBOX,ACLK)") >= 0) //moving towards GLASSBOX
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrival to GLASSBOX, robot does the dropout
                "mover(wait,GLASSBOX,GLASSBOX,ACLK)",
                "pickupdropouthandler(do_dropout)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContent("transporttrolley(req_move,HOME)") >= 0) //robot heading to HOME
            Assertions.assertTrue(to!!.checkNextContent("mover(wait,HOME,HOME,ACLK)") >= 0) //robot in HOME

        }
    }

    @Test
    fun test_rejected() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(15)) {
            ColorsOut.outappl("testLoadKo STARTS", ColorsOut.BLUE)

            val connTcp = ConnTcp("localhost", 8095)

            val requestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,11),1)"
            val answer = connTcp.request(requestStr)
            ColorsOut.outappl("test_rejected answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("loadrejected"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------- CHECKING HISTORY ---------------------------------------------
            Assertions.assertTrue(to!!.checkNextContents(arrayOf(
                "wasteservice(handle_req,0.0,0.0)",
                "wasteservice(rejected,0.0,0.0)",
                "wasteservice(wait,0.0,0.0)"
            )) >= 0)

        }
    }

    @Test
            /* the second request is made while the robot is still in operation, while still dropping down material.
                After dropout it whould be observed a command to go directly to INDOOR, rather than HOME
             */
    fun test_2_accepted_while_in_operation() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(45)){

            val connTcp = ConnTcp("localhost", 8095)

            //FIRST REQUEST
            var requestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)"
            var answer = connTcp.request(requestStr)
            ColorsOut.outappl("first request answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("loadaccept"))
            to!!.waitUntilState("pickupdropouthandler","pickupdropouthandler(do_dropout)") //wait for dropout command
            CommUtils.delay(50)  //concede a little extra time to the actors in the system to communicate and do their transition, before going ahead with the test requests

            //SECOND REQUEST
            ColorsOut.outappl("launch second request", ColorsOut.GREEN)
            requestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,7),1)"
            answer = connTcp.request(requestStr)
            ColorsOut.outappl("second request answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("loadaccept"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------

            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //wasteservice receives the request and commands the depositaction: robot moves to indoor
                "wasteservice(req_depositaction,2.0,0.0)",
                "transporttrolley(req_move,INDOOR)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrives to INDOOR, and does the pickup
                "mover(wait,INDOOR,INDOOR,ACLK)",
                "pickupdropouthandler(do_pickup)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //reply to first request after pickup, wasteservice ready for new requests, robot heading to PLASTICBOX
                "depositaction(reply,PLASTIC)",
                "wasteservice(wait,2.0,0.0)",
                "transporttrolley(req_move,PLASTICBOX)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrives at PLASTICBOX, and does the dropout
                "mover(wait,PLASTICBOX,PLASTICBOX,ACLK)",
                "pickupdropouthandler(do_dropout)",
            )) >= 0)
            val nextCheckIndexBeforeSecondReq = to!!.nextCheckIndex
            //CHECK SECOND REQUEST
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //new request is handled now, depositaction state machine goes directly in next_move and req_move_indoor, robot goes from PLASTICBOX to INDOOR
                "wasteservice(req_depositaction,2.0,7.0)",
                "depositaction(next_move*)",
                "depositaction(req_move_indoor*)",
                "mover(handle,PLASTICBOX,INDOOR,*)",
                "mover(req_forward_clk,*)",
            )) >= 0)
            to!!.nextCheckIndex = nextCheckIndexBeforeSecondReq
            Assertions.assertFalse(to!!.checkNextSequence(arrayOf( "depositaction(move_home*)", "depositaction(req_pickup*)"))) //depositaction should not pass through move_home state (should not be present before - for example - req_pickup of the second request)

        }
    }

    @Test
            /* the second request is made while the robot is still in operation, while still dropping down material
            After dropout it whould be observed a command to go to HOME, rather than INDOOR
             */
    fun test_1_accepted_1_rejected_while_in_operation() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(35)){

            val connTcp = ConnTcp("localhost", 8095)

            //FIRST REQUEST
            var requestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)"
            var answer = connTcp.request(requestStr)
            ColorsOut.outappl("first request answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("loadaccept"))
            to!!.waitUntilState("pickupdropouthandler","pickupdropouthandler(do_dropout)") //wait for dropout command
            CommUtils.delay(50)  //concede a little extra time to the actors in the system to communicate and do their transition, before going ahead with the test requests

            //SECOND REQUEST
            ColorsOut.outappl("launch second request", ColorsOut.GREEN)
            requestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,11),1)"
            answer = connTcp.request(requestStr)
            ColorsOut.outappl("second request answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("loadrejected"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------

            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //wasteservice receives the request and commands the depositaction: robot moves to indoor
                "wasteservice(req_depositaction,2.0,0.0)",
                "transporttrolley(req_move,INDOOR)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrives to INDOOR, and does the pickup
                "mover(wait,INDOOR,INDOOR,ACLK)",
                "pickupdropouthandler(do_pickup)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //reply to first request after pickup, wasteservice ready for new requests, robot heading to PLASTICBOX
                "depositaction(reply,PLASTIC)",
                "wasteservice(wait,2.0,0.0)",
                "transporttrolley(req_move,PLASTICBOX)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrives at PLASTICBOX, and does the dropout
                "mover(wait,PLASTICBOX,PLASTICBOX,ACLK)",
                "pickupdropouthandler(do_dropout)",
            )) >= 0)
            val nextCheckIndexBeforeSecondReq = to!!.nextCheckIndex
            //CHECK SECOND REQUEST
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //new request is rejected, ready for new requests. depositaction state machine goes to move_home (since depositrequest is rejected depositaction is not called), robot to directly to HOME
                "wasteservice(rejected,2.0,0.0)",
                "wasteservice(wait,2.0,0.0)",
                "depositaction(move_home*)",
                "mover(handle,PLASTICBOX,HOME,ACLK)",
            )) >= 0)

        }
    }

    @Test
            /* the second request is made while the robot is still in operation, while it's returning to home
            After dropout it whould be observed a command to go to HOME, rather than INDOOR
            */
    fun test_2_accepted_while_returning_home() { //the second request is made while the robot is still in operation, while returning to home. In the process a u turn is performed
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(45)){

            val connTcp = ConnTcp("localhost", 8095)

            //FIRST REQUEST
            var requestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)"
            var answer = connTcp.request(requestStr)
            ColorsOut.outappl("testFirstRequest answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("loadaccept"))
            to!!.waitUntilState("mover","mover(req_forward_aclk,PLASTICBOX,HOME,ACLK)") //wait for the robot to proceed from PLASTICBOX to HOME
            CommUtils.delay(250)  //wait to be roughly in the middle of the wall

            //SECOND REQUEST
            requestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,7),1)"
            answer = connTcp.request(requestStr)
            ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("loadaccept"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------

            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //wasteservice receives the request and commands the depositaction: robot moves to indoor
                "wasteservice(req_depositaction,2.0,0.0)",
                "transporttrolley(req_move,INDOOR)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrives to INDOOR, and does the pickup
                "mover(wait,INDOOR,INDOOR,ACLK)",
                "pickupdropouthandler(do_pickup)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //reply to first request after pickup, wasteservice ready for new requests, robot heading to PLASTICBOX
                "depositaction(reply,PLASTIC)",
                "wasteservice(wait,2.0,0.0)",
                "transporttrolley(req_move,PLASTICBOX)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrives at PLASTICBOX, and does the dropout
                "mover(wait,PLASTICBOX,PLASTICBOX,ACLK)",
                "pickupdropouthandler(do_dropout)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //depositaction commands move to HOME, robot heading to HOME
                "depositaction(move_home*)",
                "transporttrolley(req_move,HOME)",
                "mover(handle,PLASTICBOX,HOME,ACLK)",
            )) >= 0) //robot is heading to HOME
            val nextCheckIndexBeforeSecondReq = to!!.nextCheckIndex
            //CHECK SECOND REQUEST
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //new request is handled now: wasteservice request depositaction, depositaction request move indoor while robot was still going on, robot redirects towards INDOOR
                "wasteservice(req_depositaction,2.0,7.0)",
                "depositaction(req_move_indoor*)",
                "transporttrolley(req_move,INDOOR)",
                "mover(set_new_dest_aclk,PLASTICBOX,INDOOR,ACLK)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(handle,PLASTICBOX,INDOOR,CLK)") >= 0)
            Assertions.assertTrue(to!!.checkNextContent("mover(wait,INDOOR,INDOOR,CLK)") >= 0)
            to!!.nextCheckIndex = nextCheckIndexBeforeSecondReq
            Assertions.assertFalse(to!!.checkNextSequence(arrayOf("depositaction(wait*)", "depositaction(req_pickup*)"))) //depositaction should not trasit through wait, it should not go directly to req_move_indoor (should not be present before - for example - req_pickup of the second request)

        }
    }

    @Test
    fun test_1_accepted_1_rejected_while_returning_home() { //the second request is made while the robot is still in operation, while returning home
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(35)){

            val connTcp = ConnTcp("localhost", 8095)

            //FIRST REQUEST
            var requestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)"
            var answer = connTcp.request(requestStr)
            ColorsOut.outappl("testFirstRequest answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("loadaccept"))
            to!!.waitUntilState("mover","mover(req_forward_aclk,PLASTICBOX,HOME,ACLK)") //wait for the robot to proceed from PLASTICBOX to HOME
            CommUtils.delay(250)  //wait to be roughly in the middle of the wall

            //SECOND REQUEST
            requestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,11),1)"
            answer = connTcp.request(requestStr)
            ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
            Assertions.assertTrue(answer.contains("loadrejected"))

            waitRegimeState()
            connTcp.close()

            // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------

            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //wasteservice receives the request and commands the depositaction: robot moves to indoor
                "wasteservice(req_depositaction,2.0,0.0)",
                "transporttrolley(req_move,INDOOR)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrives to INDOOR, and does the pickup
                "mover(wait,INDOOR,INDOOR,ACLK)",
                "pickupdropouthandler(do_pickup)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //reply to first request after pickup, wasteservice ready for new requests, robot heading to PLASTICBOX
                "depositaction(reply,PLASTIC)",
                "wasteservice(wait,2.0,0.0)",
                "transporttrolley(req_move,PLASTICBOX)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //robot arrives at PLASTICBOX, and does the dropout
                "mover(wait,PLASTICBOX,PLASTICBOX,ACLK)",
                "pickupdropouthandler(do_dropout)",
            )) >= 0)
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //depositaction commands move to HOME, robot heading to HOME
                "depositaction(move_home*)",
                "transporttrolley(req_move,HOME)",
                "mover(handle,PLASTICBOX,HOME,ACLK)",
            )) >= 0) //robot is heading to HOME
            val nextCheckIndexBeforeSecondReq = to!!.nextCheckIndex
            //CHECK SECOND REQUEST
            Assertions.assertTrue(to!!.checkNextContents(arrayOf( //new request is handled now: wasteservice rejects request, and returns to wait
                "wasteservice(rejected,2.0,0.0)",
                "wasteservice(wait,2.0,0.0)"
            )) >= 0)
            to!!.nextCheckIndex = nextCheckIndexBeforeSecondReq
            Assertions.assertFalse(to!!.checkNextContent("depositaction(req_move_indoor*)") >= 0) //depositaction should not trasit through wait, it should not go directly to req_move_indoor (should not be present before - for example - req_pickup of the second request)

        }
    }
}