package gui

import it.unibo.kactor.MsgUtil
import it.unibo.kactor.ActorBasic
import alice.tuprolog.Term
import alice.tuprolog.Struct
import it.unibo.kactor.IApplMessage

class updateLedGui (name : String ) : ActorBasic( name ) {
	
	override suspend fun actorBody(msg: IApplMessage) {
		//println("$tt $name | received  $msg "  )  //RICEVE GLI EVENTI!!!
		if( msg.msgId()=="update_led"){
			elabData( msg )
		}
	}
	
	@kotlinx.coroutines.ObsoleteCoroutinesApi
	
	suspend fun elabData( msg: IApplMessage ){ //OPTIMISTIC
		val state  = (Term.createTerm( msg.msgContent() ) as Struct).getArg(0).toString()
		//println("$tt $name |  updateLedGui STATE $state" )
		//MsgUtil.buildDispatch(name,"update_led",state,"guiserver");
		//val m1 = MsgUtil.buildEvent(name, "update_led_gui","update_led_gui($state)")
		//emit(m1)
		
		forward("update_led_gui","update_led_gui($state)","guiServer")
		
		//println("$tt $name |  emit m1= $data, $toDisable, $wasDisabled, $Distance" )
	}
}