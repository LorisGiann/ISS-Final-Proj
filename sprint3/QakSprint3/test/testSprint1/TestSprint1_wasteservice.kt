package testSprint1

import org.junit.jupiter.api.*
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.IOException
import java.time.Duration
import kotlin.test.Test

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class TestSprint1_wasteservice {
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

        val to = TestObserver()
        to.establishCoapConn("wasteservice")
        to.establishCoapConn("depositaction")
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


    @Test
    fun dummy() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(5)) {
            CommUtils.delay(1000)
        }
    }



    @Test
    fun test_2_accepted() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(90)) {
            try {
                val connTcp = ConnTcp("localhost", 8095)

                //FIRST REQUEST
                var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("first request answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("loadaccept"))
                to!!.waitUntilState("depositaction","depositaction(wait)") //wait for the robot to be back to HOME
                CommUtils.delay(50)  //concede a little extra time to the actors in the system to communicate and do their transition, before going ahead with the test requests

                //SECONDO REQUEST
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,7),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("second request answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("loadaccept"))
                to!!.waitUntilState("depositaction","depositaction(wait)") //wait for the robot to be back to HOME

                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_2_accepted ERROR:" + e.message)
                Assertions.fail()
            }
        }
    }

    @Test
    fun test_1_accepted_1_rejected() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(70)){
            try {
                val connTcp = ConnTcp("localhost", 8095)

                //FIRST REQUEST
                var truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
                var answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("first request answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("loadaccept"))
                to!!.waitUntilState("depositaction","depositaction(wait)") //wait for the robot to be back to HOME
                CommUtils.delay(50)  //concede a little extra time to the actors in the system to communicate and do their transition, before going ahead with the test requests

                //SECONDO REQUEST
                truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,9),1)"
                answer = connTcp.request(truckRequestStr)
                ColorsOut.outappl("second request answer=$answer", ColorsOut.GREEN)
                Assertions.assertTrue(answer.contains("loadrejected"))
                to!!.waitUntilState("depositaction","depositaction(wait)") //wait for the robot to be back to HOME

                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_1_accepted_1_rejected ERROR:" + e.message)
                Assertions.fail()
            }
        }
    }

}