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
internal class TestSonar {
    private var connSonar: CoapConnection? = null
    private var to: TestObserver? = null
    private var processHandleSonar: ProcessHandle? = null
    private var prSonar: Process? = null

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
            val (prS, processHandleS) = TestUtils.runCtx("build/libs/it.unibo.ctxsonar.MainCtxsonarKt-1.0.jar")
            prSonar=prS; processHandleSonar=processHandleS
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
            processHandleSonar!!.destroy()
            prSonar!!.destroy()
        }catch(e :  NullPointerException){ }
        CommUtils.delay(1000)
        //since sometime this isn't enough, do it the heavy way...
        processHandleSonar!!.destroyForcibly()
        //val s = ServerSocket(8095)
        //s.close()
        connSonar!!.close()
    }


    @Test
    @Timeout(30)
    fun test_distance() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            CommUtils.delay(100)
            try {
                ColorsOut.outappl("Ready", ColorsOut.GREEN)
                val connTcp = ConnTcp("localhost", 8095)
                connTcp.forward(MsgUtil.buildDispatch("sonarqak22","sonaractivate","info(ok)","sonarqak22").toString())
                CommUtils.delay(10000)
                connTcp.forward(MsgUtil.buildDispatch("sonarqak22","sonardeactivate","info(ok)","sonarqak22").toString())
                CommUtils.delay(300)
                connTcp.close()
                Assert.assertTrue(to!!.checkNextSequence(arrayOf(
                    "sonaractivate",
                    "Distance 50",
                    "sonardeactivate"
                )))
                //ColorsOut.outappl(to!!.getHistory().toString(), ColorsOut.MAGENTA)

            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_on ERROR:" + e.message)
                Assert.fail();
            }
        }
    }

    protected fun startObserverCoap(addr: String, handler: CoapHandler?) {
        /*object : Thread() {
            override fun run() {*/
        try {
            val qakdestination1 = "sonarqak22"
            val ctxqakdest1 = "ctxsonar"
            val applPort1 = "8095"
            connSonar = CoapConnection("$addr:$applPort1", "$ctxqakdest1/$qakdestination1")
            connSonar!!.observeResource(handler)
            ColorsOut.outappl("connecting via Coap conn:$connSonar", ColorsOut.CYAN)
            while (connSonar!!.request("") === "0") {
                ColorsOut.outappl("waiting for conn $connSonar", ColorsOut.CYAN)
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