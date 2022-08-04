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
    private var to: TestObserver? = null
    private var processHandleServer: ProcessHandle? = null
    private var prServer: Process? = null

    companion object {
        /*@JvmStatic
        @BeforeAll
        @Throws(IOException::class, InterruptedException::class)
        fun compileCtxs() {
            /*TestUtils.compileCtx("it.unibo.ctxtest.MainCtxtestKt")*/
        }*/
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

        val to = TestObserver()
        to.establishCoapConn("wasteservice");
        this.to = to
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {
        try {
            //FIRSTLY, try to be nice and make the program exit "normally"
            processHandleServer!!.destroy()
            prServer!!.destroy()
            CommUtils.delay(1000)
            //since sometime this isn't enough, do it the heavy way...
            processHandleServer!!.destroyForcibly()
        }catch(_:  NullPointerException){ }

        to!!.closeAllConnections()
    }


    @Test
    fun dummy() {
        Assertions.assertTrue(true)
    }



    @Test
    @Timeout(30)
    fun test_2_accepted() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(25)) {
            CommUtils.delay(100)
            try {
                val connTcp = ConnTcp("localhost", 8095)

                //FIRST REQUEST
                var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("test_2_accepted answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("loadaccept"))

                //SECONDO REQUEST
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,7),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("loadaccept"))

                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_2_accepted ERROR:" + e.message)
                Assertions.fail();
            }
        }
    }

    @Test
    fun test_1_accepted_1_rejected() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(15)){
            CommUtils.delay(100)
            try {
                val connTcp = ConnTcp("localhost", 8095)

                //FIRST REQUEST
                var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testFirstRequest answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("loadaccept"))

                //SECONDO REQUEST
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,9),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("testSecondRequest answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("loadrejected"))

                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_1_accepted_1_rejected ERROR:" + e.message)
                Assertions.fail();
            }
        }
    }


    //---------------------------------------------------
}