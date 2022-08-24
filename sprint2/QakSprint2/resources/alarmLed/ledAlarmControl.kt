package alarmLed

import alice.tuprolog.InvalidTermException
import alice.tuprolog.Struct
import alice.tuprolog.Term
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.CoapObserverSupport
import it.unibo.kactor.IApplMessage
import it.unibo.kactor.MsgUtil
import unibo.comm22.utils.CommUtils
import ws.LedState

class ledAlarmControl (name : String ) : ActorBasic( name ) {

	//function and variables to determine if the robot is in home and if the robot has been interrupted
	fun checkIsHome(From: String, To: String) = From==To && To==ws.Position.HOME.toString() //true if the robot is currently in home (=false if the robot is moving from home to another place)
	var ISHOME: Boolean = true
	fun checkIsHaltedPickupDropout(stateName: String) = stateName=="halt_pickup" || stateName=="halt_dropout" || stateName=="alarm"
	fun checkIsHaltedBasicRobotWrapper(stateName: String) = stateName=="halt_forward" || stateName=="halt_collision" || stateName=="alarm"
	var ISPickupDropoutHalted = false
	var ISBasicRobotWrapperHalted = false
	var ISHALTED: Boolean = false

	var lastSentCommand : LedState? = null  //needed in order to avoid sending another blink command, to avoid glithes
	suspend fun updateLed(state : LedState) : Unit {
		if (lastSentCommand==state) return
		lastSentCommand=state
		println("$tt $name | pickupdropouthandler in home=$ISHOME, halted=$ISHALTED"  )
		var m1 = MsgUtil.buildEvent(name, "update_led", "update_led(${state})")
		emit( m1 )
	}

	init {
		initCoapObserver()
	}

	fun initCoapObserver() {
		try {
			CommUtils.delay(1000)
			println("$tt $name | connecting")
			CoapObserverSupport(this, "127.0.0.1", "8096", "ctxrobot", "basicrobotwrapper")
			CoapObserverSupport(this, "127.0.0.1", "8096", "ctxrobot", "mover")
			CoapObserverSupport(this, "127.0.0.1", "8096", "ctxrobot", "pickupdropouthandler")
		}catch (e: Exception){
			System.err.println(e.stackTrace)
		}
	}
	
	override suspend fun actorBody(msg: IApplMessage) {
		MsgUtil.outgreen("$tt $name | msg ${msg.msgId()} : ${msg.toString()}")
		if (msg.msgId() == "autoStartSysMsg") {
			//initCoapObserver()
			//MsgUtil.outgreen("$tt $name | started ")
		}else if(msg.msgId() == "coapUpdate"){
			try {
				val term = (Term.createTerm(msg.msgContent()) as Struct)
				val RESOURCE = term.getArg(0).toString()
				val VALUE = term.getArg(1).toString()
				//MsgUtil.outgreen("$tt $name | update $VALUE FROM $RESOURCE ")

				when (RESOURCE) {
					"mover" -> {
						val msgterm = (Term.createTerm(VALUE) as Struct)
						val FROM = msgterm.getArg(1).toString()
						val TO = msgterm.getArg(2).toString()
						ISHOME = checkIsHome(FROM, TO)
						println("$tt $name | mover from $FROM to $TO, ishome = $ISHOME" )
					}
					"basicrobotwrapper" -> {
						val msgterm = (Term.createTerm(VALUE) as Struct)
						val STATE = msgterm.getArg(0).toString()
						ISBasicRobotWrapperHalted = checkIsHaltedBasicRobotWrapper(STATE)
						println("$tt $name | basicrobotwrapper in $STATE, halted = $ISBasicRobotWrapperHalted" )
					}
					"pickupdropouthandler" -> {
						val msgterm = (Term.createTerm(VALUE) as Struct)
						val STATE = msgterm.getArg(0).toString()
						ISPickupDropoutHalted = checkIsHaltedPickupDropout(STATE)
						println("$tt $name | pickupdropouthandler in $STATE, halted = $ISPickupDropoutHalted" )
					}
				}

				//calculate new led state
				ISHALTED = ISBasicRobotWrapperHalted && ISPickupDropoutHalted //if turning ISBasicRobotWrapperHalted=false, while ISPickupDropoutHalted=true
				if (ISHOME) {
					updateLed(LedState.OFF)
					//println("led off")
				} else if (ISHALTED) {
					updateLed(LedState.ON)
					//println("led on")
				} else { //not halted and not at home
					updateLed(LedState.BLINK)
					//println("led blink")
				}
			} catch (e: Exception){
				System.err.println(e.stackTrace)
			}
		}

	}
}