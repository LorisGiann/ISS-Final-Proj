package gui

import it.unibo.kactor.MsgUtil
import it.unibo.kactor.ActorBasic
import alice.tuprolog.Term
import alice.tuprolog.Struct
import it.unibo.kactor.IApplMessage

class updateContainerGui (name : String ) : ActorBasic( name ) {
	
	override suspend fun actorBody(msg: IApplMessage) {
		//println("$tt $name | received  $msg "  )  //RICEVE GLI EVENTI!!!
		if( msg.msgId()=="update_container"){
			elabData( msg )
		}
	}
	
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	
	suspend fun elabData( msg: IApplMessage ){ //OPTIMISTIC
		val pb  = (Term.createTerm( msg.msgContent() ) as Struct).getArg(0).toString()
		val gb  = (Term.createTerm( msg.msgContent() ) as Struct).getArg(1).toString()
		println("$tt $name |  emit m1= $pb $qpb $gb $qgb")
		MsgUtil.buildDispatch("gui","update_container_gui","PB: "+pb+ "GB: "+gb,"guiserver");
		//println("$tt $name |  emit m1= $data, $toDisable, $wasDisabled, $Distance" )
	}
}