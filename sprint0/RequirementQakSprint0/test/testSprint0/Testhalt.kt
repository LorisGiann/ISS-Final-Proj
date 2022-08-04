package testSprint0

import org.eclipse.californium.core.CoapHandler
import org.junit.Assert
import org.junit.jupiter.api.*
import unibo.comm22.coap.CoapConnection
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import ws.LedState
import java.io.IOException
import java.time.Duration
import kotlin.test.Test

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class Testhalt {
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
        to.establishCoapConn("led");
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
    fun test_halt() {
        assertTimeoutPreemptively<Unit>(Duration.ofSeconds(15)){
            try {
                val connTcp = ConnTcp("localhost", 8095)

                //REQUEST
                var RequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)"
                connTcp.forward(RequestStr)

                to!!.waitUntilState("basicrobot", "basicrobot(w*)") //wait until robot moves forward
                CommUtils.delay(250)

                RequestStr = "msg(obstacle, event,distanceFilter,wasteservice,obstacle(1),1)" //simulate a close object to the sonar
                connTcp.forward(RequestStr)
                CommUtils.delay(1000)
                RequestStr = "msg(obstacle, event,distanceFilter,wasteservice,obstacle(20),1)" //simulate a far object to the sonar
                connTcp.forward(RequestStr)

                to!!.waitUntilState("basicrobot", "basicrobot(l*)") //wait until robot turns left

                RequestStr = "msg(obstacle, event,distanceFilter,wasteservice,obstacle(1),1)" //simulate a close object to the sonar
                connTcp.forward(RequestStr)
                CommUtils.delay(1000)
                RequestStr = "msg(obstacle, event,distanceFilter,wasteservice,obstacle(20),1)" //simulate a far object to the sonar
                connTcp.forward(RequestStr)

                //the robot should stop if a close object is detected while moving forward. Then, when the object is far the forward move is resumed
                Assertions.assertTrue(to!!.checkNextSequence(arrayOf(
                    "basicrobot(execcmd,w)", //commanded forward move
                    "basicrobot(execcmd,h)", //close object while moving forward
                    "basicrobot(execcmd,w)", //forward move resumes
                )))

                //commanded turn left move. close object is detected while turning:
                //command terminates anyway, it should not be resumed
                //so the following sequence should not happen
                Assertions.assertFalse(to!!.checkNextSequence(arrayOf(
                    "basicrobot(execcmd,l)",
                    "basicrobot(execcmd,h)",
                    "basicrobot(execcmd,l)", //turn left should not be resumed!
                    "basicrobot(execcmd,w)",
                )))

                connTcp.close()
            } catch (e: java.lang.Exception) {
                ColorsOut.outerr("test_halt ERROR:" + e.message)
                Assertions.fail();
            }
        }
    }


    //---------------------------------------------------
}