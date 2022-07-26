package testSprint2

import it.unibo.kactor.MsgUtil
import okhttp3.internal.notify
import okhttp3.internal.notifyAll
import org.eclipse.californium.core.CoapHandler
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
    private var connLed: CoapConnection? = null
    private var to: TestObserver? = null
    private var processHandleLed: ProcessHandle? = null
    private var prLed: Process? = null

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
            ColorsOut.outappl("Start launch ", ColorsOut.GREEN)
            TestUtils.terminateProcOnPort(8095); //making sure that the port is free7
            val (prS, processHandleS) = TestUtils.runCtx("build/libs/it.unibo.ctxled.MainCtxledKt-1.0.jar")
            prLed=prS; processHandleLed=processHandleS
            ColorsOut.outappl("Finish launch ", ColorsOut.GREEN)
        } catch (e: IOException) {
            ColorsOut.outappl("Errore launch ", ColorsOut.RED)
            System.exit(1)
        }
        to = TestObserver()
        startObserverCoap("localhost", to)
        CommUtils.delay(2000)
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {
        try {
            processHandleLed!!.destroy()
            prLed!!.destroy()
        }catch(e :  NullPointerException){ }
        CommUtils.delay(1000)
        //since sometime this isn't enough, do it the heavy way...
        processHandleLed!!.destroyForcibly()
        //val s = ServerSocket(8095)
        //s.close()
        connLed!!.close()
    }


    @Test
    @Timeout(30)
    fun test_on() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            CommUtils.delay(100)
            try {
                ColorsOut.outappl("Ready", ColorsOut.GREEN)
                val RequestStr: String=MsgUtil.buildEvent("led","update_led","update_led(on)").toString();
                val connTcp = ConnTcp("localhost", 8095)
                connTcp.forward(RequestStr)
                CommUtils.delay(1000)
                connTcp.close()
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "led(off,false)",
                    "led(on,true)",
                )))

            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_on ERROR:" + e.message)
                Assert.fail();
            }
        }
    }

    @Test
    @Timeout(30)
    fun test_off() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            CommUtils.delay(100)
            try {
                ColorsOut.outappl("Ready", ColorsOut.GREEN)
                val RequestStr: String=MsgUtil.buildEvent("led","update_led","update_led(off)").toString();
                val connTcp = ConnTcp("localhost", 8095)
                connTcp.forward(RequestStr)
                CommUtils.delay(1000)
                connTcp.close()
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "led(off,false)",
                    "led(off,false)",
                )))

            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_off ERROR:" + e.message)
                Assert.fail();
            }
        }
    }

    @Test
    @Timeout(30)
    fun test_blink() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            CommUtils.delay(100)
            try {
                ColorsOut.outappl("Ready", ColorsOut.GREEN)
                val RequestStr: String=MsgUtil.buildEvent("led","update_led","update_led(blink)").toString();
                val connTcp = ConnTcp("localhost", 8095)
                connTcp.forward(RequestStr)
                CommUtils.delay(1000)
                connTcp.close()
                ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "led(off,false)",
                    "led(blink,true)",
                )))

            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_blink ERROR:" + e.message)
                Assert.fail();
            }
        }
    }

    @Test
    @Timeout(30)
    fun test_on_off_blink() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            CommUtils.delay(100)
            try {
                ColorsOut.outappl("Ready", ColorsOut.GREEN)
                val connTcp = ConnTcp("localhost", 8095)
                connTcp.forward(MsgUtil.buildEvent("led","update_led","update_led(on)").toString())
                CommUtils.delay(300)
                connTcp.forward(MsgUtil.buildEvent("led","update_led","update_led(off)").toString())
                CommUtils.delay(300)
                connTcp.forward(MsgUtil.buildEvent("led","update_led","update_led(blink)").toString());
                CommUtils.delay(300)
                connTcp.close()
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "led(off,false)",
                    "led(on,true)",
                    "led(off,false)",
                    "led(blink,true)"
                )))

            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_on_off_blink ERROR:" + e.message)
                Assert.fail();
            }
        }
    }

    protected fun startObserverCoap(addr: String, handler: CoapHandler?) {
        /*object : Thread() {
            override fun run() {*/
        try {
            val qakdestination1 = "led"
            val ctxqakdest1 = "ctxled"
            val applPort1 = "8095"
            connLed = CoapConnection("$addr:$applPort1", "$ctxqakdest1/$qakdestination1")
            connLed!!.observeResource(handler)
            ColorsOut.outappl("connecting via Coap conn:$connLed", ColorsOut.CYAN)
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