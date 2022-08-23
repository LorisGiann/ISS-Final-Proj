/* Generated by AN DISI Unibo */ 
package it.unibo.transporttrolley

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Transporttrolley ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		  lateinit var tmp : String 
				var newState : String? = null  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = true
						println("$name in ${currentState.stateName} | $currentMsg")
						 sysUtil.logMsgs=true  
					}
					 transition(edgeName="t00",targetState="handle_cmd",cond=whenRequest("move"))
					transition(edgeName="t01",targetState="handle_cmd",cond=whenRequest("pickup"))
					transition(edgeName="t02",targetState="handle_cmd",cond=whenRequest("dropout"))
				}	 
				state("handle_cmd") { //this:State
					action { //it:State
						updateResourceRep( "transporttrolley(handle_cmd)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("move(POSITION)"), Term.createTerm("move(ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 tmp = payloadArg(0)  
								println("move(${tmp})")
								 newState = when(ws.Position.valueOf(tmp)) {
													ws.Position.HOME -> "moving_home"
													ws.Position.INDOOR -> "moving_indoor"
													ws.Position.PLASTICBOX -> "moving_plasticbox"
													ws.Position.GLASSBOX -> "moving_glassbox"
												}
						}
						if( checkMsgContent( Term.createTerm("pickup(_)"), Term.createTerm("pickup(_)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("PICKUP")
								 newState = "pickUp"  
						}
						if( checkMsgContent( Term.createTerm("dropout(_)"), Term.createTerm("dropout(_)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("DROPOUT")
								 newState = "dropOut"  
						}
					}
					 transition( edgeName="goto",targetState="handle_update_switch_1", cond=doswitch() )
				}	 
				state("handle_update_switch_1") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="moving_home", cond=doswitchGuarded({ newState=="moving_home"  
					}) )
					transition( edgeName="goto",targetState="handle_update_switch_2", cond=doswitchGuarded({! ( newState=="moving_home"  
					) }) )
				}	 
				state("handle_update_switch_2") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="moving_indoor", cond=doswitchGuarded({ newState=="moving_indoor"  
					}) )
					transition( edgeName="goto",targetState="handle_update_switch_3", cond=doswitchGuarded({! ( newState=="moving_indoor"  
					) }) )
				}	 
				state("handle_update_switch_3") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="moving_plasticbox", cond=doswitchGuarded({ newState=="moving_plasticbox"  
					}) )
					transition( edgeName="goto",targetState="handle_update_switch_4", cond=doswitchGuarded({! ( newState=="moving_plasticbox"  
					) }) )
				}	 
				state("handle_update_switch_4") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="moving_glassbox", cond=doswitchGuarded({ newState=="moving_glassbox"  
					}) )
					transition( edgeName="goto",targetState="handle_update_switch_5", cond=doswitchGuarded({! ( newState=="moving_glassbox"  
					) }) )
				}	 
				state("handle_update_switch_5") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="pickUp", cond=doswitchGuarded({ newState=="pickUp"  
					}) )
					transition( edgeName="goto",targetState="handle_update_switch_6", cond=doswitchGuarded({! ( newState=="pickUp"  
					) }) )
				}	 
				state("handle_update_switch_6") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="dropOut", cond=doswitchGuarded({ newState=="dropOut"  
					}) )
					transition( edgeName="goto",targetState="s0", cond=doswitchGuarded({! ( newState=="dropOut"  
					) }) )
				}	 
				state("moving_home") { //this:State
					action { //it:State
						updateResourceRep( "transporttrolley(moving_home)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						stateTimer = TimerActor("timer_moving_home", 
							scope, context!!, "local_tout_transporttrolley_moving_home", 1000.toLong() )
					}
					 transition(edgeName="t03",targetState="moved_home",cond=whenTimeout("local_tout_transporttrolley_moving_home"))   
					transition(edgeName="t04",targetState="handle_cmd",cond=whenRequest("move"))
				}	 
				state("moved_home") { //this:State
					action { //it:State
						updateResourceRep( "transporttrolley(moved_home)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						answer("move", "moveanswer", "moveanswer(OK)"   )  
						println("robot reached HOME")
					}
					 transition(edgeName="t05",targetState="handle_cmd",cond=whenRequest("move"))
					transition(edgeName="t06",targetState="handle_cmd",cond=whenRequest("pickup"))
					transition(edgeName="t07",targetState="handle_cmd",cond=whenRequest("dropout"))
				}	 
				state("moving_indoor") { //this:State
					action { //it:State
						updateResourceRep( "transporttrolley(moving_indoor)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						stateTimer = TimerActor("timer_moving_indoor", 
							scope, context!!, "local_tout_transporttrolley_moving_indoor", 1000.toLong() )
					}
					 transition(edgeName="t08",targetState="moved_indoor",cond=whenTimeout("local_tout_transporttrolley_moving_indoor"))   
					transition(edgeName="t09",targetState="handle_cmd",cond=whenRequest("move"))
				}	 
				state("moved_indoor") { //this:State
					action { //it:State
						updateResourceRep( "transporttrolley(moved_indoor)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						answer("move", "moveanswer", "moveanswer(OK)"   )  
						println("robot reached INDOOR")
					}
					 transition(edgeName="t010",targetState="handle_cmd",cond=whenRequest("move"))
					transition(edgeName="t011",targetState="handle_cmd",cond=whenRequest("pickup"))
					transition(edgeName="t012",targetState="handle_cmd",cond=whenRequest("dropout"))
				}	 
				state("moving_plasticbox") { //this:State
					action { //it:State
						updateResourceRep( "transporttrolley(moving_plasticbox)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						stateTimer = TimerActor("timer_moving_plasticbox", 
							scope, context!!, "local_tout_transporttrolley_moving_plasticbox", 1000.toLong() )
					}
					 transition(edgeName="t013",targetState="moved_plasticbox",cond=whenTimeout("local_tout_transporttrolley_moving_plasticbox"))   
					transition(edgeName="t014",targetState="handle_cmd",cond=whenRequest("move"))
				}	 
				state("moved_plasticbox") { //this:State
					action { //it:State
						updateResourceRep( "transporttrolley(moved_plasticbox)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						answer("move", "moveanswer", "moveanswer(OK)"   )  
						println("robot reached PLASTICBOX")
					}
					 transition(edgeName="t015",targetState="handle_cmd",cond=whenRequest("move"))
					transition(edgeName="t016",targetState="handle_cmd",cond=whenRequest("pickup"))
					transition(edgeName="t017",targetState="handle_cmd",cond=whenRequest("dropout"))
				}	 
				state("moving_glassbox") { //this:State
					action { //it:State
						updateResourceRep( "transporttrolley(moving_glassbox)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						stateTimer = TimerActor("timer_moving_glassbox", 
							scope, context!!, "local_tout_transporttrolley_moving_glassbox", 1000.toLong() )
					}
					 transition(edgeName="t018",targetState="moved_glassbox",cond=whenTimeout("local_tout_transporttrolley_moving_glassbox"))   
					transition(edgeName="t019",targetState="handle_cmd",cond=whenRequest("move"))
				}	 
				state("moved_glassbox") { //this:State
					action { //it:State
						updateResourceRep( "transporttrolley(moved_glassbox)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						answer("move", "moveanswer", "moveanswer(OK)"   )  
						println("robot reached GLASSBOX")
					}
					 transition(edgeName="t020",targetState="handle_cmd",cond=whenRequest("move"))
					transition(edgeName="t021",targetState="handle_cmd",cond=whenRequest("pickup"))
					transition(edgeName="t022",targetState="handle_cmd",cond=whenRequest("dropout"))
				}	 
				state("pickUp") { //this:State
					action { //it:State
						updateResourceRep( "transporttrolley(pickUp)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						delay(1000) 
						answer("pickup", "pickupanswer", "pickupanswer(OK)"   )  
						println("robot pickedUp")
					}
					 transition(edgeName="t023",targetState="handle_cmd",cond=whenRequest("move"))
					transition(edgeName="t024",targetState="handle_cmd",cond=whenRequest("pickup"))
					transition(edgeName="t025",targetState="handle_cmd",cond=whenRequest("dropout"))
				}	 
				state("dropOut") { //this:State
					action { //it:State
						updateResourceRep( "transporttrolley(dropOut)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						delay(1000) 
						answer("dropout", "dropoutanswer", "dropoutanswer(OK)"   )  
						println("robot droppedOut")
					}
					 transition(edgeName="t026",targetState="handle_cmd",cond=whenRequest("move"))
					transition(edgeName="t027",targetState="handle_cmd",cond=whenRequest("pickup"))
					transition(edgeName="t028",targetState="handle_cmd",cond=whenRequest("dropout"))
				}	 
			}
		}
}
