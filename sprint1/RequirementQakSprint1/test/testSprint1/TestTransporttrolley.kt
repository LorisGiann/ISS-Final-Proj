package testSprint1

import org.junit.Assert
import org.junit.jupiter.api.*
import unibo.comm22.coap.CoapConnection
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.time.Duration
import kotlin.test.Test

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestTransporttrolley {
    private var processHandleServer: ProcessHandle? = null
    private var prServer: Process? = null

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
            val (prS, processHandleS) = TestUtils.runCtx("build/libs/it.unibo.ctxtransporttrolley.MainCtxtransporttrolleyKt-1.0.jar")
            prServer=prS; processHandleServer=processHandleS
        } catch (e: IOException) {
            ColorsOut.outappl("Errore launch ", ColorsOut.RED)
            System.exit(1)
        }

        CommUtils.delay(2000)
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {

    }


    @Test
    fun dummy() {
        Assert.assertTrue(true)
    }



    @Test
    @Timeout(30)
    fun test_1_move() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            CommUtils.delay(100)
            try {
                //FIRST REQUEST
                val connTcp = ConnTcp("localhost", 8095)
                var RequestStr = "msg(move, request,python,transporttrolley,move(INDOOR),1)"
                var answer = connTcp.request(RequestStr)
                ColorsOut.outappl("answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("moveanswer(OK)"))

                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_1_move ERROR:" + e.message)
                Assert.fail();
            }
        }
    }



}