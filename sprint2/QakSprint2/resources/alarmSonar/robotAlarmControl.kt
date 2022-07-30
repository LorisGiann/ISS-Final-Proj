package alarmSonar

import it.unibo.kactor.MsgUtil
import it.unibo.kactor.ActorBasic
import alice.tuprolog.Term
import alice.tuprolog.Struct
import it.unibo.kactor.IApplMessage

class robotAlarmControl (name : String ) : ActorBasic( name ) {
val LimitDistance = ws.const.DLIMIT
	var wasDisabled = false;

override suspend fun actorBody(msg: IApplMessage) {
	if( msg.msgSender() == "sonar" && msg.msgId()=="sonardata")
	elabData( msg )
}

 	
@kotlinx.coroutines.ObsoleteCoroutinesApi

suspend fun elabData( msg: IApplMessage ){ //OPTIMISTIC
	val data  = (Term.createTerm( msg.msgContent() ) as Struct).getArg(0).toString()
	val Distance = Integer.parseInt( data )

	val toDisable : Boolean = Distance < LimitDistance
	if(toDisable != wasDisabled){
		val m1 = MsgUtil.buildEvent(name, "disable", "disable($toDisable)")
		//println("$tt $name |  emit m1= $m1")
		emitLocalStreamEvent( m1 ) //propagate event obstacle
		wasDisabled = toDisable
	}
}
}