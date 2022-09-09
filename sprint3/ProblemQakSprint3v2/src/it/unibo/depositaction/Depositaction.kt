/* Generated by AN DISI Unibo */ 
package it.unibo.depositaction

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Depositaction ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		 lateinit var RES : String
			   lateinit var MATERIAL : ws.Material  
		return { //this:ActionBasciFsm
				state("error") { //this:State
					action { //it:State
						updateResourceRep( "depositaction(error)"  
						)
						println("depositaction | error")
						println("$name in ${currentState.stateName} | $currentMsg")
						forward("err", "err(_)" ,"wasteservice" ) 
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
				}	 
				state("wait") { //this:State
					action { //it:State
						updateResourceRep( "depositaction(wait)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						discardMessages = false
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t012",targetState="req_move_indoor",cond=whenRequest("depositaction"))
				}	 
				state("req_move_indoor") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("depositaction(MATERIAL)"), Term.createTerm("depositaction(MATERIAL)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 MATERIAL=ws.Material.valueOf(payloadArg(0))  
								request("moveto", "moveto(INDOOR)" ,"transporttrolley" )  
						}
						updateResourceRep( "depositaction(req_move_indoor,$MATERIAL)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t013",targetState="chk_move_indoor",cond=whenReply("movetoanswer"))
				}	 
				state("chk_move_indoor") { //this:State
					action { //it:State
						updateResourceRep( "depositaction(chk_move_indoor,$MATERIAL)"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("movetoanswer(RESULT)"), Term.createTerm("movetoanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="req_pickup", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("req_pickup") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "depositaction(req_pickup,$MATERIAL)"  
						)
						request("pickup", "pickup(_)" ,"transporttrolley" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t014",targetState="chk_pickup",cond=whenReply("pickupanswer"))
				}	 
				state("chk_pickup") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "depositaction(chk_pickup,$MATERIAL)"  
						)
						if( checkMsgContent( Term.createTerm("pickupanswer(RESULT)"), Term.createTerm("pickupanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="reply", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("reply") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "depositaction(reply,$MATERIAL)"  
						)
						answer("depositaction", "pickupdone", "pickupdone(_)","wasteservice"   )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="req_move_container", cond=doswitch() )
				}	 
				state("req_move_container") { //this:State
					action { //it:State
						 
									val Position = when(MATERIAL){
									    ws.Material.PLASTIC -> ws.Position.PLASTICBOX
									    ws.Material.GLASS -> ws.Position.GLASSBOX
									}  
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "depositaction(req_move_container,$MATERIAL)"  
						)
						request("moveto", "moveto($Position)" ,"transporttrolley" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t015",targetState="chk_move_container",cond=whenReply("movetoanswer"))
				}	 
				state("chk_move_container") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "depositaction(chk_pickup,$MATERIAL)"  
						)
						if( checkMsgContent( Term.createTerm("movetoanswer(RESULT)"), Term.createTerm("movetoanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="req_dropout", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("req_dropout") { //this:State
					action { //it:State
						updateResourceRep( "depositaction(req_dropout,$MATERIAL)"  
						)
						request("dropout", "dropout(_)" ,"transporttrolley" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t016",targetState="chk_dropout",cond=whenReply("dropoutanswer"))
				}	 
				state("chk_dropout") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "depositaction(chk_dropout,$MATERIAL)"  
						)
						if( checkMsgContent( Term.createTerm("dropoutanswer(RESULT)"), Term.createTerm("dropoutanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="next_move", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("next_move") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "depositaction(next_move,$MATERIAL)"  
						)
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
				 	 		//sysaction { //it:State
				 	 		  stateTimer = TimerActor("timer_next_move", 
				 	 			scope, context!!, "local_tout_depositaction_next_move", 10.toLong() )
				 	 		//}
					}	 	 
					 transition(edgeName="t017",targetState="move_home",cond=whenTimeout("local_tout_depositaction_next_move"))   
					transition(edgeName="t018",targetState="req_move_indoor",cond=whenRequest("depositaction"))
				}	 
				state("move_home") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "depositaction(move_home,$MATERIAL)"  
						)
						request("moveto", "moveto(HOME)" ,"transporttrolley" )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t019",targetState="req_move_indoor",cond=whenRequest("depositaction"))
					transition(edgeName="t020",targetState="wait",cond=whenReply("movetoanswer"))
				}	 
			}
		}
}
