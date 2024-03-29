
import alice.tuprolog.Struct
import alice.tuprolog.Term
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.IApplMessage

class dataCleaner (name : String ) : ActorBasic( name ) {
val LimitLow  = 2	
val LimitHigh = 100


    override suspend fun actorBody(msg: IApplMessage) {
  		elabData( msg )
 	}

//WARNING: datacleaner perecpisce TUTTI gli event 	
@kotlinx.coroutines.ObsoleteCoroutinesApi

	  suspend fun elabData( msg: IApplMessage ){ 
        //println("$tt $name | received  $msg "  )  //RICEVE TUTTI GLI EVENTI!!!
	    if( msg.msgId() != "sonar" ) return
 		val data  = (Term.createTerm( msg.msgContent() ) as Struct).getArg(0).toString()
  		//println("$tt $name |  data = $data ")		
		val Distance = Integer.parseInt( data ) 
 		if( Distance > LimitLow && Distance < LimitHigh ){
			//println("emit distance ${Distance}")
			emitLocalStreamEvent( msg ) //propagate
     	}else{
			println("$tt $name |  DISCARDS $Distance ")
 		}				
 	}
}