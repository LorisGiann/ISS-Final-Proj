package gui

import it.unibo.kactor.MsgUtil
import it.unibo.kactor.ActorBasic
import alice.tuprolog.Term
import alice.tuprolog.Struct
import it.unibo.kactor.IApplMessage

class updatePositionGui (name : String ) : ActorBasic( name ) {
	
	
	init {
		initCoapObserver()
	}

	fun initCoapObserver() {
		try {
			CommUtils.delay(1000)
			println("$tt $name | connecting")
			CoapObserverSupport(this, "127.0.0.1", "8096", "ctxrobot", "mover")
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
				
				val msgterm = (Term.createTerm(VALUE) as Struct)
				val currpos = msgterm.getArg(2).toString()
				println("$tt $name | state $state" )
				

				
				println("$tt $name |  emit m1= $position")
				MsgUtil.buildDispatch("gui","update_position_gui",position,"guiserver");
			} catch (e: Exception){
				System.err.println(e.stackTrace)
			}
		}

	}
}