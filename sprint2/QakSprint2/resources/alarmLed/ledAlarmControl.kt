package alarmLed

import it.unibo.kactor.MsgUtil
import it.unibo.kactor.ActorBasic
import alice.tuprolog.Term
import alice.tuprolog.Struct
import it.unibo.kactor.IApplMessage
import ws.LedState

class ledAlarmControl (name : String ) : ActorBasic( name ) {
	val LimitDistance = ws.const.DLIMIT

	fun checkIsHome(From: String, To: String) = From==To && To==ws.Position.HOME.toString() //true if the robot is currently in home (=false if the robot is moving from home to another place)
	var ISHOME: Boolean = true

	var lastSentCommand : LedState? = null  //needed in order to avoid sending another blink command, to avoid glithes
	suspend fun updateLed(state : LedState) : Unit {
		if (lastSentCommand==state) return
		lastSentCommand=state
		var m1 = MsgUtil.buildEvent(name, "update_led", "update_led(${state})")
		emit( m1 )
	}
	
	override suspend fun actorBody(msg: IApplMessage) {
		if( msg.msgSender()=="sonar" && msg.msgId()=="sonardata"){
			newDistance( msg )
			println("$tt $name | received  $msg "  )
		}
		if( msg.msgId()=="moving"){
			newPos( msg )
			println("$tt $name | received  $msg "  )
		}
	}
	
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	
	suspend fun newDistance( msg: IApplMessage ){ //OPTIMISTIC
		val data  = (Term.createTerm( msg.msgContent() ) as Struct).getArg(0).toString()
		val Distance = Integer.parseInt( data )
		val near : Boolean = Distance < LimitDistance
		
		if (!ISHOME){
			if(near){
				updateLed(LedState.ON)
				//println("led on")
			}else{
				updateLed(LedState.BLINK)
				//println("led blink")
			}
  		}
		
	}
	
	suspend fun newPos( msg: IApplMessage ){ //OPTIMISTIC
		val term  = (Term.createTerm( msg.msgContent() ) as Struct)
		var From : String = term.getArg(0).toString()
		var To : String = term.getArg(1).toString()
		println("ledAlarmControl | $From $To")
		ISHOME=checkIsHome(From,To)

		if (ISHOME){
			updateLed(LedState.OFF)
			//println("led off")
		}else{
			updateLed(LedState.BLINK)
			//println("led blink")
		}
	}
}