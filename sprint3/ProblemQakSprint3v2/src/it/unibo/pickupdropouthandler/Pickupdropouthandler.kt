/* Generated by AN DISI Unibo */ 
package it.unibo.pickupdropouthandler

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Pickupdropouthandler ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		return { //this:ActionBasciFsm
				state("handle_prio") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(handle_prio)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		//sysaction { //it:State
				 	 		  stateTimer = TimerActor("timer_handle_prio", 
				 	 			scope, context!!, "local_tout_pickupdropouthandler_handle_prio", 10.toLong() )
				 	 		//}
					}	 	 
					 transition(edgeName="t028",targetState="wait",cond=whenTimeout("local_tout_pickupdropouthandler_handle_prio"))   
					transition(edgeName="t029",targetState="alarm",cond=whenDispatch("alarm"))
					transition(edgeName="t030",targetState="handle_prio",cond=whenDispatch("alarmceased"))
				}	 
				state("alarm") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(alarm)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t031",targetState="handle_prio",cond=whenDispatch("alarmceased"))
				}	 
				state("wait") { //this:State
					action { //it:State
						discardMessages = false
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(wait)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t032",targetState="do_dropout",cond=whenRequest("dropout"))
					transition(edgeName="t033",targetState="do_pickup",cond=whenRequest("pickup"))
					transition(edgeName="t034",targetState="alarm",cond=whenDispatch("alarm"))
				}	 
				state("do_dropout") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(do_dropout)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		//sysaction { //it:State
				 	 		  stateTimer = TimerActor("timer_do_dropout", 
				 	 			scope, context!!, "local_tout_pickupdropouthandler_do_dropout", 1000.toLong() )
				 	 		//}
					}	 	 
					 transition(edgeName="t035",targetState="done_dropout",cond=whenTimeout("local_tout_pickupdropouthandler_do_dropout"))   
					transition(edgeName="t036",targetState="halt_dropout",cond=whenDispatch("alarm"))
				}	 
				state("halt_dropout") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(halt_dropout)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t037",targetState="resume_dropout",cond=whenDispatch("alarmceased"))
				}	 
				state("resume_dropout") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(resume_dropout)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		//sysaction { //it:State
				 	 		  stateTimer = TimerActor("timer_resume_dropout", 
				 	 			scope, context!!, "local_tout_pickupdropouthandler_resume_dropout", 1000.toLong() )
				 	 		//}
					}	 	 
					 transition(edgeName="t038",targetState="done_dropout",cond=whenTimeout("local_tout_pickupdropouthandler_resume_dropout"))   
				}	 
				state("do_pickup") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(do_pickup)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		//sysaction { //it:State
				 	 		  stateTimer = TimerActor("timer_do_pickup", 
				 	 			scope, context!!, "local_tout_pickupdropouthandler_do_pickup", 1000.toLong() )
				 	 		//}
					}	 	 
					 transition(edgeName="t039",targetState="done_pickup",cond=whenTimeout("local_tout_pickupdropouthandler_do_pickup"))   
					transition(edgeName="t040",targetState="halt_pickup",cond=whenDispatch("alarm"))
				}	 
				state("halt_pickup") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(halt_pickup)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t041",targetState="resume_pickup",cond=whenDispatch("alarmceased"))
				}	 
				state("resume_pickup") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(resume_pickup)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		//sysaction { //it:State
				 	 		  stateTimer = TimerActor("timer_resume_pickup", 
				 	 			scope, context!!, "local_tout_pickupdropouthandler_resume_pickup", 1000.toLong() )
				 	 		//}
					}	 	 
					 transition(edgeName="t042",targetState="done_pickup",cond=whenTimeout("local_tout_pickupdropouthandler_resume_pickup"))   
				}	 
				state("done_dropout") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(done_dropout)"  
						)
						answer("dropout", "dropoutanswer", "dropoutanswer(OK)","transporttrolley"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("done_pickup") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(done_pickup)"  
						)
						answer("pickup", "pickupanswer", "pickupanswer(OK)","transporttrolley"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
			}
		}
}