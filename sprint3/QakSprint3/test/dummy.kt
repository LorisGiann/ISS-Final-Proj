package test

import it.unibo.kactor.MsgUtil
import org.junit.Assert
import org.junit.jupiter.api.*
import testCommon.*
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.IOException
import java.time.Duration
import kotlin.test.Test

//@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class Dummy {

    @Test
    fun dummy() {
        val to = TestObserver()
        Assert.assertTrue(true)
        to!!.debugSetHistory(
            listOf("wasteservice(wait,0.0,0.0)",
                "depositaction(wait)",
                "transporttrolley(wait)",
                "mover(wait,HOME,HOME,ACLK)",
                "basicrobotwrapper(wait)",
                "       ActorBasic(Resource) basicrobotlorisdavide  | created  , pickupdropouthandler(wait)",
                "       ActorBasic(Resource) sonarlorisdavide  | created  , led(initial,OFF)",
                "led(handle_update,OFF)",
                "sonarlorisdavide(activateTheSonar,simulate)",
                "led(off,OFF)",
                "       ActorBasic(Resource) alarmreceivertest  | created  , basicrobotlorisdavide(s0)",
                "basicrobotlorisdavide(work)",
                "sonarlorisdavide(deactivateTheSonar,simulate)",
                "sonarlorisdavide(end,simulate)",
                "wasteservice(handle_req,0.0,0.0)",
                "wasteservice(req_depositaction,0.0,2.0)",
                "depositaction(req_move_indoor,GLASS)",
                "transporttrolley(req_move,INDOOR)",
                "mover(handle,HOME,INDOOR,ACLK)",
                "mover(aclk_or_clk,HOME,INDOOR,ACLK)",
                "led(handle_update,BLINK)",
                "led(blink_on,BLINK)",
                "mover(prepare_aclk,HOME,INDOOR,ACLK)",
                "basicrobotwrapper(handle,w)",
                "mover(req_forward_aclk,HOME,INDOOR,ACLK)",
                "basicrobotlorisdavide(execcmd,w)",
                "basicrobotwrapper(forward_cmd)",
                "basicrobotlorisdavide(work)",
                "led(blink_off,BLINK)",
                "alarmreceivertest(wait,alarm)",
                "basicrobotwrapper(halt_forward)",
                "pickupdropouthandler(alarm)",
                "basicrobotlorisdavide(execcmd,h)",
                "led(handle_update,ON)",
                "basicrobotlorisdavide(work)",
                "led(on,ON)",
                "pickupdropouthandler(handle_prio)",
                "alarmreceivertest(wait,alarmceased)",
                "basicrobotwrapper(forward_cmd)",
                "basicrobotlorisdavide(execcmd,w)",
                "led(handle_update,BLINK)",
                "basicrobotlorisdavide(work)",
                "pickupdropouthandler(wait)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "basicrobotlorisdavide(handleObstacle,unkknown)",
                "basicrobotwrapper(collision)",
                "led(blink_on,BLINK)",
                "basicrobotlorisdavide(work)",
                "mover(chk_forward_aclk,HOME,INDOOR,ACLK)",
                "basicrobotwrapper(handle_prio)",
                "mover(req_post_turn_aclk,HOME,INDOOR,ACLK)",
                "basicrobotwrapper(wait)",
                "basicrobotwrapper(handle,l)",
                "basicrobotwrapper(other_cmd,l)",
                "led(blink_off,BLINK)",
                "basicrobotlorisdavide(execcmd,l)",
                "basicrobotlorisdavide(work)",
                "alarmreceivertest(wait,alarm)",
                "pickupdropouthandler(alarm)",
                "led(blink_on,BLINK)",
                "mover(chk_post_turn_aclk,HOME,INDOOR,ACLK)",
                "basicrobotwrapper(handle_prio)",
                "mover(update_aclk,INDOOR,INDOOR,ACLK)",
                "led(handle_update,ON)",
                "mover(reply,INDOOR,INDOOR,ACLK)",
                "transporttrolley(chk_move)",
                "depositaction(chk_move_indoor,GLASS)",
                "depositaction(req_pickup,GLASS)",
                "transporttrolley(wait)",
                "basicrobotwrapper(alarm)",
                "transporttrolley(req_pickup)",
                "mover(handle,INDOOR,INDOOR,ACLK)",
                "mover(wait,INDOOR,INDOOR,ACLK)",
                "led(on,ON)",
                "alarmreceivertest(wait,alarmceased)",
                "pickupdropouthandler(handle_prio)",
                "basicrobotwrapper(handle_prio)",
                "led(handle_update,BLINK)",
                "led(blink_on,BLINK)",
                "pickupdropouthandler(wait)",
                "basicrobotwrapper(wait)",
                "pickupdropouthandler(do_pickup)",
                "led(blink_off,BLINK)",
                "pickupdropouthandler(halt_pickup)",
                "alarmreceivertest(wait,alarm)",
                "basicrobotwrapper(alarm)",
                "led(handle_update,ON)",
                "led(on,ON)",
                "alarmreceivertest(wait,alarmceased)",
                "pickupdropouthandler(resume_pickup)",
                "basicrobotwrapper(handle_prio)",
                "led(handle_update,BLINK)",
                "basicrobotwrapper(wait)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "pickupdropouthandler(done_pickup)",
                "transporttrolley(chk_pickup)",
                "depositaction(chk_pickup,GLASS)",
                "depositaction(reply,GLASS)",
                "wasteservice(chk_depositaction,0.0,2.0)",
                "depositaction(req_move_container,GLASS)",
                "wasteservice(wait,0.0,2.0)",
                "pickupdropouthandler(wait)",
                "transporttrolley(wait)",
                "mover(handle,INDOOR,GLASSBOX,ACLK)",
                "led(blink_on,BLINK)",
                "basicrobotwrapper(handle,w)",
                "transporttrolley(req_move,GLASSBOX)",
                "basicrobotlorisdavide(execcmd,w)",
                "mover(aclk_or_clk,INDOOR,GLASSBOX,ACLK)",
                "basicrobotlorisdavide(work)",
                "basicrobotwrapper(forward_cmd)",
                "mover(prepare_aclk,INDOOR,GLASSBOX,ACLK)",
                "mover(req_forward_aclk,INDOOR,GLASSBOX,ACLK)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "basicrobotlorisdavide(handleObstacle,unkknown)",
                "basicrobotwrapper(collision)",
                "basicrobotlorisdavide(work)",
                "led(blink_off,BLINK)",
                "basicrobotwrapper(handle_prio)",
                "mover(chk_forward_aclk,INDOOR,GLASSBOX,ACLK)",
                "mover(req_post_turn_aclk,INDOOR,GLASSBOX,ACLK)",
                "basicrobotwrapper(wait)",
                "basicrobotwrapper(handle,l)",
                "basicrobotwrapper(other_cmd,l)",
                "led(blink_on,BLINK)",
                "basicrobotlorisdavide(execcmd,l)",
                "basicrobotlorisdavide(work)",
                "led(blink_off,BLINK)",
                "mover(chk_post_turn_aclk,INDOOR,GLASSBOX,ACLK)",
                "mover(update_aclk,PLASTICBOX,GLASSBOX,ACLK)",
                "mover(reply,PLASTICBOX,GLASSBOX,ACLK)",
                "basicrobotwrapper(handle_prio)",
                "mover(handle,PLASTICBOX,GLASSBOX,ACLK)",
                "mover(aclk_or_clk,PLASTICBOX,GLASSBOX,ACLK)",
                "basicrobotwrapper(wait)",
                "mover(prepare_aclk,PLASTICBOX,GLASSBOX,ACLK)",
                "mover(req_forward_aclk,PLASTICBOX,GLASSBOX,ACLK)",
                "basicrobotwrapper(handle,w)",
                "basicrobotwrapper(forward_cmd)",
                "basicrobotlorisdavide(execcmd,w)",
                "basicrobotlorisdavide(work)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "basicrobotlorisdavide(handleObstacle,unkknown)",
                "basicrobotwrapper(collision)",
                "mover(chk_forward_aclk,PLASTICBOX,GLASSBOX,ACLK)",
                "basicrobotwrapper(handle_prio)",
                "basicrobotlorisdavide(work)",
                "mover(req_post_turn_aclk,PLASTICBOX,GLASSBOX,ACLK)",
                "basicrobotwrapper(wait)",
                "basicrobotwrapper(handle,l)",
                "basicrobotwrapper(other_cmd,l)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "basicrobotlorisdavide(execcmd,l)",
                "basicrobotlorisdavide(work)",
                "led(blink_off,BLINK)",
                "mover(chk_post_turn_aclk,PLASTICBOX,GLASSBOX,ACLK)",
                "basicrobotwrapper(handle_prio)",
                "mover(update_aclk,GLASSBOX,GLASSBOX,ACLK)",
                "mover(reply,GLASSBOX,GLASSBOX,ACLK)",
                "depositaction(chk_pickup,GLASS)",
                "depositaction(req_dropout,GLASS)",
                "transporttrolley(chk_move)",
                "basicrobotwrapper(wait)",
                "transporttrolley(wait)",
                "pickupdropouthandler(do_dropout)",
                "mover(handle,GLASSBOX,GLASSBOX,ACLK)",
                "mover(wait,GLASSBOX,GLASSBOX,ACLK)",
                "transporttrolley(req_dropout)",
                "led(blink_on,BLINK)",
                "alarmreceivertest(wait,alarm)",
                "pickupdropouthandler(halt_dropout)",
                "basicrobotwrapper(alarm)",
                "led(handle_update,ON)",
                "led(on,ON)",
                "alarmreceivertest(wait,alarmceased)",
                "pickupdropouthandler(resume_dropout)",
                "led(handle_update,BLINK)",
                "basicrobotwrapper(handle_prio)",
                "led(blink_on,BLINK)",
                "basicrobotwrapper(wait)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "pickupdropouthandler(done_dropout)",
                "transporttrolley(chk_dropout)",
                "depositaction(chk_dropout,GLASS)",
                "pickupdropouthandler(wait)",
                "transporttrolley(wait)",
                "depositaction(next_move,GLASS)",
                "led(blink_on,BLINK)",
                "depositaction(move_home,GLASS)",
                "transporttrolley(req_move,HOME)",
                "mover(handle,GLASSBOX,HOME,ACLK)",
                "basicrobotwrapper(handle,w)",
                "basicrobotlorisdavide(execcmd,w)",
                "mover(aclk_or_clk,GLASSBOX,HOME,ACLK)",
                "basicrobotwrapper(forward_cmd)",
                "mover(prepare_aclk,GLASSBOX,HOME,ACLK)",
                "basicrobotlorisdavide(work)",
                "mover(req_forward_aclk,GLASSBOX,HOME,ACLK)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "basicrobotlorisdavide(handleObstacle,unkknown)",
                "basicrobotwrapper(collision)",
                "basicrobotwrapper(handle_prio)",
                "basicrobotlorisdavide(work)",
                "mover(chk_forward_aclk,GLASSBOX,HOME,ACLK)",
                "mover(req_post_turn_aclk,GLASSBOX,HOME,ACLK)",
                "basicrobotwrapper(wait)",
                "basicrobotwrapper(handle,l)",
                "basicrobotwrapper(other_cmd,l)",
                "led(blink_on,BLINK)",
                "led(blink_off,BLINK)",
                "basicrobotlorisdavide(execcmd,l)",
                "basicrobotlorisdavide(work)",
                "basicrobotwrapper(handle_prio)",
                "transporttrolley(chk_move)",
                "mover(chk_post_turn_aclk,GLASSBOX,HOME,ACLK)",
                "mover(update_aclk,HOME,HOME,ACLK)",
                "transporttrolley(wait)",
                "led(handle_update,OFF)",
                "led(off,OFF)",
                "mover(reply,HOME,HOME,ACLK)",
                "depositaction(wait)",
                "mover(handle,HOME,HOME,ACLK)",
                "basicrobotwrapper(wait)",
                "mover(wait,HOME,HOME,ACLK)")
                    as MutableList<String>
        )

        // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
        //Assertions.assertTrue(to!!.checkNextContent("led(*,OFF)") >= 0)    //this may happen before the test executable connects
        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //led blink while moving
            "basicrobotwrapper(forward_cmd)",
            "led(blink_on,*)",
        )).also{ println(it) } >= 0)
        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //halt move forward
            "alarmreceivertest(wait,alarm)",
            "led(*,ON)",
        )).also{ println(it) } >= 0)
        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //resume move forward
            "alarmreceivertest(wait,alarmceased)",
            "led(*,BLINK)"
        )).also{ println(it) } >= 0)
        val afterResume = to!!.nextCheckIndex
        Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(wait)") >= 0)

        Assertions.assertTrue(to!!.checkNextContent("basicrobotwrapper(other_cmd,l)") >= 0)
        Assertions.assertTrue(to!!.checkNextContent("alarmreceivertest(wait,alarm)") >= 0) //alarm is received but led continues to blink
        val afteralarminturn = to!!.nextCheckIndex

        //check that led continues to blink before robot halts (which happes after the turn)
        to!!.nextCheckIndex = afterResume
        val ledOn = to!!.checkNextContent("led(*,ON)") //should NOT be present between alarmreceivertest(wait,alarmceased) and basicrobotwrapper(alarm)
        to!!.nextCheckIndex = afterResume
        val robAlarm = to!!.checkNextContent("alarmreceivertest(wait,alarm)") //<---- this should be "basicrobotwrapper(alarm)". But the problem is that a "led(*,ON)" happens right after "basicrobotwrapper(alarm)", so it can actually happen that this "led(*,ON)" is received before "basicrobotwrapper(alarm)", even if it actually happened after it. So take the preceding event in the timeline, which is when the alarm arrives during the turn (unfortunately we cannot do any better)
        Assertions.assertTrue( ledOn > robAlarm )

        to!!.nextCheckIndex = afteralarminturn
        Assertions.assertTrue(to!!.checkNextContents(arrayOf(//basicrobotwrapper in alarm only after the turn completes: only then the led tuns on
            "basicrobotwrapper(alarm)",
            "led(*,ON)",
        )) >= 0)
        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //alarm ceased, led blinks
            "alarmreceivertest(wait,alarmceased)",
            "led(*,BLINK)"
        )) >= 0)

        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //pickup halted
            "alarmreceivertest(wait,alarm)",
            "led(*,ON)",
        )) >= 0)
        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //pickup resumed
            "alarmreceivertest(wait,alarmceased)",
            "led(*,BLINK)",
        )) >= 0)

        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //dropout halted
            "alarmreceivertest(wait,alarm)",
            "led(*,ON)",
        )) >= 0)
        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //dropout resumed
            "alarmreceivertest(wait,alarmceased)",
            "led(*,BLINK)",
        )) >= 0)

        Assertions.assertTrue(to!!.checkNextContents(arrayOf( //depositaction complete, robot at home
            "depositaction(wait)",
            "led(*,BLINK)",
        )) >= 0)

    }

}