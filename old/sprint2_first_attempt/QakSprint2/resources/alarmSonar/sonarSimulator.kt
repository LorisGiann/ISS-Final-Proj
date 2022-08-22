package alarmSonar

import it.unibo.kactor.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class sonarSimulator (name : String ) : ActorBasic( name ) {
	var goon = true
	var i = 0
	lateinit var m1 : String
	lateinit var stateTimer : TimerActor

	override suspend fun actorBody(msg : IApplMessage){
  		//println("$tt $name | received  $msg "  )  //RICEVE GLI EVENTI!!!

		if( msg.msgId() == "sonaractivate") {
			println("sonar activate")
			goon = true
			stateTimer = TimerActor("timer_read_simulator",
				super.scope, super.context!!, "local_tout_"+name, 500.toLong() )

		}
		if( msg.msgId() == "sonardeactivate") goon=false
		if( msg.msgId() == "local_tout_"+name && goon){
			if (i<=9) {
				m1 = "distance( 20 )"
			}else{
				m1 = "distance( 0 )"
			}
			i=(i+1)%12
			//println(m1)
			val event = MsgUtil.buildEvent( name ,"sonar", m1)
			//println("$tt $name | generates $event")
			emit(event)  //APPROPRIATE ONLY IF NOT INCLUDED IN A PIPE

			stateTimer = TimerActor("timer_read_simulator",
				super.scope, super.context!!, "local_tout_"+name, 500.toLong() )
		}
	}


} 

