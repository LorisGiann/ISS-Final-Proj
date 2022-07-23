package testSprint1

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

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestWasteservice {
    private var connWasteService: CoapConnection? = null
    private var to: TestObserver? = null
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
            val (prS, processHandleS) = TestUtils.runCtx("build/libs/it.unibo.ctxwasteservice.MainCtxwasteserviceKt-1.0.jar")
            prServer=prS; processHandleServer=processHandleS
        } catch (e: IOException) {
            ColorsOut.outappl("Errore launch ", ColorsOut.RED)
            System.exit(1)
        }

        to = TestObserver()
        //startObserverCoap("localhost", to)
        CommUtils.delay(2000)
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {
        //FIRSTLY, try to be nice and make the program exit "normally"
        try {
            processHandleServer!!.destroy()
            prServer!!.destroy()
        }catch(e :  NullPointerException){ }
        CommUtils.delay(1000)
        //since sometime this isn't enough, do it the heavy way...
        processHandleServer!!.destroyForcibly()

        //connWasteService!!.close()
    }


    @Test
    fun dummy() {
        Assert.assertTrue(true)
    }



    @Test
    @Timeout(30)
    fun test_2_accepted() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            CommUtils.delay(100)
            try {
                //FIRST REQUEST
                val connTcp = ConnTcp("localhost", 8095)
                var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("test_2_accepted answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))

                //SECONDO REQUEST
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,7),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))

                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_2_accepted ERROR:" + e.message)
                Assert.fail();
            }
        }
    }

    @Test
    fun test_1_accepted_1_rejected() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(15)){
            CommUtils.delay(100)
            try {
                //FIRST REQUEST
                val connTcp = ConnTcp("localhost", 8095)
                var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testFirstRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadaccept"))

                //SECONDO REQUEST
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,9),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assert.assertTrue(answer.contains("loadrejected"))

                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_1_accepted_1_rejected ERROR:" + e.message)
                Assert.fail();
            }
        }
    }


    //---------------------------------------------------

    protected fun startObserverCoap(addr: String, handler: CoapHandler?) {
        /*object : Thread() {
            override fun run() {*/
        try {
            val qakdestination1 = "wasteservice"
            val ctxqakdest1 = "ctxRequestWasteService"
            val applPort1 = "8095"
            connWasteService = CoapConnection("$addr:$applPort1", "$ctxqakdest1/$qakdestination1")
            connWasteService!!.observeResource(handler)
            ColorsOut.outappl("connecting via Coap conn:$connWasteService", ColorsOut.CYAN)
            while (connWasteService!!.request("") === "0") {
                ColorsOut.outappl("waiting for conn $connWasteService", ColorsOut.CYAN)
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