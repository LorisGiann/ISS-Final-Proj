package testSprint2

import okhttp3.internal.notify
import okhttp3.internal.notifyAll
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
internal class TestLed {
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
            TestUtils.terminateProcOnPort(8095); //making sure that the port is free7
            val (prS, processHandleS) = TestUtils.runCtx("build/libs/it.unibo.ctxled.MainCtxledKt-1.0.jar")
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

    @Timeout(30)
    fun test_on() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            CommUtils.delay(100)
            try {

            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_on ERROR:" + e.message)
                Assert.fail();
            }
        }
    }


}