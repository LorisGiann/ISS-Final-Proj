package testSprint1

import org.junit.Assert
import org.junit.jupiter.api.*
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.IOException
import java.time.Duration
import kotlin.test.Test

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestTransporttrolley {
    private var to: TestObserver? = null
    private var processHandlRobot: ProcessHandle? = null
    private var prRobot: Process? = null

    companion object {
        @JvmStatic
        @BeforeAll
        @Throws(IOException::class, InterruptedException::class)
        fun compileCtxs() {
            /*TestUtils.compileCtx("it.unibo.ctxtest.MainCtxtestKt")*/
        }
    }

    @BeforeEach
    @Throws(IOException::class, InterruptedException::class)
    fun up() {
        CommSystemConfig.tracing = false
        try {
            TestUtils.terminateProcOnPort(8095); //making sure that the port is free
            val (prR, processHandleR) = TestUtils.runCtx("build/libs/it.unibo.ctxrobot.MainCtxrobotKt-1.0.jar")
            prRobot=prR; processHandlRobot=processHandleR
        } catch (e: IOException) {
            ColorsOut.outappl("Errore launch ", ColorsOut.RED)
            System.exit(1)
        }

        val to = TestObserver()
        to.establishCoapConn("transporttrolley");
        this.to = to
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {
        ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
        try {
            //FIRSTLY, try to be nice and make the program exit "normally"
            processHandlRobot!!.destroy()
            prRobot!!.destroy()
            CommUtils.delay(1000)
            //since sometime this isn't enough, do it the heavy way...
            processHandlRobot!!.destroyForcibly()
        }catch(_:  NullPointerException){ }

        to!!.closeAllConnections()
    }


    @Test
    fun dummy() {
        Assertions.assertTrue(true)
    }



    @Test
    @Timeout(30)
    fun test_1_move() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            try {
                val connTcp = ConnTcp("localhost", 8095)
                var RequestStr = "msg(move, request,python,transporttrolley,move(INDOOR),1)"
                var answer = connTcp.request(RequestStr)
                ColorsOut.outappl("answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("moveanswer(OK)"))
                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_1_move ERROR:" + e.message)
                Assertions.fail();
            }
        }
    }

    @Test
    @Timeout(30)
    fun test_2_move() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            try {
                val connTcp = ConnTcp("localhost", 8095)
                to!!.waitUntilState("transporttrolley","handle_cmd")
                
                //FIRST REQUEST
                val RequestStr1 = "msg(move, request,python,transporttrolley,move(INDOOR),1)"
                connTcp.forward(RequestStr1) //do non wait for a reply
                ColorsOut.outappl("FIRST REQUEST SENT", ColorsOut.GREEN)
                CommUtils.delay(100)
                
                //SECOND REQUEST
                ColorsOut.outappl("SENDING SECOND REQUEST", ColorsOut.GREEN)
                val RequestStr2 = "msg(move, request,python,transporttrolley,move(PLASTICBOX),1)"
                val answer = connTcp.request(RequestStr2)
                
                ColorsOut.outappl("answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("moveanswer(OK)"))
                connTcp.close()

                Assertions.assertTrue(to!!.checkNextSequence(arrayOf(
                    "transporttrolley(moving_indoor)",
                    "transporttrolley(moving_plasticbox)",
                    "transporttrolley(moved_plasticbox)"
                )))
                to!!.nextCheckIndex = 0
                Assertions.assertFalse(to!!.checkNextContent("transporttrolley(moved_indoor)") >= 0)

            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_2_move ERROR:" + e.message)
                Assertions.fail();
            }
        }
    }

    @Test
    @Timeout(30)
    fun test_pickup() {
			CommUtils.delay(1000)
            assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            try {
                val connTcp = ConnTcp("localhost", 8095)
                
                val RequestStr = "msg(pickup, request,python,transporttrolley,pickup(_),1)"
                val answer = connTcp.request(RequestStr)
                ColorsOut.outappl("answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("pickupanswer(OK)"))
                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_pickup ERROR:" + e.message)
                Assertions.fail();
            }
        }
    }

    @Test
    @Timeout(30)
    fun test_dropout() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            try {
                val connTcp = ConnTcp("localhost", 8095)
                
                val RequestStr = "msg(dropout, request,python,transporttrolley,dropout(_),1)"
                val answer = connTcp.request(RequestStr)
                ColorsOut.outappl("answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("dropoutanswer(OK)"))
                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_dropout ERROR:" + e.message)
                Assertions.fail();
            }
        }
    }

}