package unibo.testqakexample

import org.junit.jupiter.api.*
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.IOException
import java.time.Duration
import kotlin.test.Test

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestMultipleCtx {
    private var to: TestObserver? = null
    private var processHandleServer: ProcessHandle? = null
    private var processHandleRobot: ProcessHandle? = null
	private var processHandleCC: ProcessHandle? = null
    private var prServer: Process? = null
    private var prRobot: Process? = null
	private var prCC: Process? = null

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

            val (prR, processHandleR) = TestUtils.runCtx("build/libs/it.unibo.ctxtesta.MainCtxtestaKt-1.0.jar")
            prRobot=prR; processHandleRobot=processHandleR
            val (prS, processHandleS) = TestUtils.runCtx("build/libs/it.unibo.ctxtestb.MainCtxtestbKt-1.0.jar")
            prServer=prS; processHandleServer=processHandleS
			val (prC, processHandleC) = TestUtils.runCtx("build/libs/it.unibo.ctxtestc.MainCtxtestcKt-1.0.jar")
            prCC=prC; processHandleCC=processHandleC

        } catch (e: IOException) {
            ColorsOut.outappl("Errore launch ", ColorsOut.RED)
            System.exit(1)
        }

        val to = TestObserver()
        to.establishCoapConn("componenta")
        to.establishCoapConn("componentb")
		to.establishCoapConn("componentc")
        this.to = to
        ColorsOut.outappl("INITIALIZATION DONE", ColorsOut.BLUE)
    }

    @AfterEach
    fun down() {
        ColorsOut.outappl("history=" + to!!.getHistory(), ColorsOut.MAGENTA)

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


    @Test
    fun dummy() {
        Assertions.assertTrue(true)
    }



    @Test
    @Timeout(30)
    fun test() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(45)) {
            try {
                val connTcp = ConnTcp("localhost", 8095)

                //FIRST REQUEST
                var truckRequestStr = "msg(start, request,python,componenta,start(_),1)"
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("request answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("done"))

                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test ERROR:" + e.message)
                Assertions.fail()
            }
        }
    }

}