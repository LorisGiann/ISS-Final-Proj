//package rx
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import it.unibo.kactor.IApplMessage
import kotlinx.coroutines.delay
import it.unibo.kactor.MsgUtil
import kotlinx.coroutines.runBlocking

/*
-------------------------------------------------------------------------------------------------
 
-------------------------------------------------------------------------------------------------
 */

class sonarSimulatorTesting (name : String ) : ActorBasic( name ) {
	var goon = true

    override suspend fun actorBody(msg : IApplMessage){
  		println("$tt $name | received  $msg "  )  //RICEVE GLI EVENTI!!!
		if( msg.msgId() == "sonaractivate") {
			println("sonar activate")
			startDataReadSimulation(   )
		}
		if( msg.msgId() == "sonardeactivate") goon=false
	}

	suspend fun startDataReadSimulation(    ){
  			var i = 0
			var m1 = "distance( 20 )"
			
			while( goon ){
				if (i!=10) {
					m1 = "distance( 20 )"
					i++
				}else{
					m1 = "distance( 0 )"
					i=0
				}
				println(m1)
 				val event = MsgUtil.buildEvent( "sonar","sonardistance",m1)
				//println(event.toString())								
  				//emitLocalStreamEvent( event )
 				println("$tt $name | generates $event")
 				emit(event)  //APPROPRIATE ONLY IF NOT INCLUDED IN A PIPE
 				delay(1000)
  			}			
			terminate()
	}

} 

