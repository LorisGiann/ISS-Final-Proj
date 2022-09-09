/* Generated by AN DISI Unibo */ 
package it.unibo.basicrobotwrapper

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Basicrobotwrapper ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		 var MOVE : ws.Move? = null   
		return { //this:ActionBasciFsm
				state("handle_prio") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "basicrobotwrapper(handle_prio)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		//sysaction { //it:State
				 	 		  stateTimer = TimerActor("timer_handle_prio", 
				 	 			scope, context!!, "local_tout_basicrobotwrapper_handle_prio", 10.toLong() )
				 	 		//}
					}	 	 
					 transition(edgeName="t058",targetState="wait",cond=whenTimeout("local_tout_basicrobotwrapper_handle_prio"))   
					transition(edgeName="t059",targetState="alarm",cond=whenDispatch("alarm"))
					transition(edgeName="t060",targetState="handle_prio",cond=whenDispatch("alarmceased"))
				}	 
				state("alarm") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "basicrobotwrapper(alarm)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t061",targetState="handle_prio",cond=whenDispatch("alarmceased"))
				}	 
				state("wait") { //this:State
					action { //it:State
						discardMessages = false
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "basicrobotwrapper(wait)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t062",targetState="handle",cond=whenRequest("cmdsync"))
					transition(edgeName="t063",targetState="alarm",cond=whenDispatch("alarm"))
				}	 
				state("handle") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("cmdsync(MOVE)"), Term.createTerm("cmdsync(MOVE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 MOVE = ws.Move.valueOf(payloadArg(0))  
						}
						updateResourceRep( "basicrobotwrapper(handle,$MOVE)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="forward_cmd", cond=doswitchGuarded({ MOVE==ws.Move.w  
					}) )
					transition( edgeName="goto",targetState="other_cmd", cond=doswitchGuarded({! ( MOVE==ws.Move.w  
					) }) )
				}	 
				state("other_cmd") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "basicrobotwrapper(other_cmd,$MOVE)"  
						)
						 val MoveStr = MOVE.toString() 
						forward("cmd", "cmd($MoveStr)" ,"basicrobotlorisdavide" ) 
						if(  MOVE==ws.Move.l || MOVE==ws.Move.r  
						 ){delay(500) 
						}
						delay(100) 
						answer("cmdsync", "cmdanswer", "cmdanswer(OK)","mover"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="handle_prio", cond=doswitch() )
				}	 
				state("forward_cmd") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "basicrobotwrapper(forward_cmd)"  
						)
						forward("cmd", "cmd(w)" ,"basicrobotlorisdavide" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t064",targetState="collision",cond=whenEvent("info"))
					transition(edgeName="t065",targetState="handle",cond=whenRequest("cmdsync"))
					transition(edgeName="t066",targetState="halt_forward",cond=whenDispatch("alarm"))
				}	 
				state("halt_forward") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "basicrobotwrapper(halt_forward)"  
						)
						forward("cmd", "cmd(h)" ,"basicrobotlorisdavide" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t067",targetState="halt_collision",cond=whenEvent("info"))
					transition(edgeName="t068",targetState="forward_cmd",cond=whenDispatch("alarmceased"))
				}	 
				state("halt_collision") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "basicrobotwrapper(halt_collision)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t069",targetState="collision",cond=whenDispatch("alarmceased"))
				}	 
				state("collision") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "basicrobotwrapper(collision)"  
						)
						answer("cmdsync", "cmdanswer", "cmdanswer(OK)","mover"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="handle_prio", cond=doswitch() )
				}	 
			}
		}
}
