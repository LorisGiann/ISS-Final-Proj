/* Generated by AN DISI Unibo */ 
package it.unibo.moveruturn

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Moveruturn ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 lateinit var RES : String     
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						discardMessages = true
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "moveruturn(wait)"  
						)
					}
					 transition(edgeName="t034",targetState="req_halt",cond=whenRequest("moveruturn"))
				}	 
				state("req_halt") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "moveruturn(req_halt)"  
						)
						request("cmdsync", "cmdsync(h)" ,"basicrobotwrapper" )  
					}
					 transition(edgeName="t035",targetState="chk_halt",cond=whenReply("cmdanswer"))
				}	 
				state("chk_halt") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "moveruturn(chk_halt)"  
						)
						if( checkMsgContent( Term.createTerm("cmdanswer(RESULT)"), Term.createTerm("cmdanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
					}
					 transition( edgeName="goto",targetState="req_turn_180_1", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("req_turn_180_1") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "moveruturn(req_turn_180_1)"  
						)
						request("mover180turn", "mover180turn(_)" ,"mover180turn" )  
					}
					 transition(edgeName="t036",targetState="chk_turn_180_1",cond=whenReply("mover180turnanswer"))
				}	 
				state("chk_turn_180_1") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "moveruturn(chk_turn_180_1)"  
						)
						if( checkMsgContent( Term.createTerm("mover180turnanswer(RESULT)"), Term.createTerm("mover180turnanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
					}
					 transition( edgeName="goto",targetState="req_forward", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("req_forward") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "moveruturn(req_forward)"  
						)
						request("cmdsync", "cmdsync(w)" ,"basicrobotwrapper" )  
					}
					 transition(edgeName="t037",targetState="chk_forward",cond=whenReply("cmdanswer"))
				}	 
				state("chk_forward") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "moveruturn(chk_forward)"  
						)
						if( checkMsgContent( Term.createTerm("cmdanswer(RESULT)"), Term.createTerm("cmdanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
					}
					 transition( edgeName="goto",targetState="req_turn_180_2", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("req_turn_180_2") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "moveruturn(req_turn_180_2)"  
						)
						request("mover180turn", "mover180turn(_)" ,"mover180turn" )  
					}
					 transition(edgeName="t038",targetState="chk_turn_180_2",cond=whenReply("mover180turnanswer"))
				}	 
				state("chk_turn_180_2") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "moveruturn(chk_turn_180_2)"  
						)
						if( checkMsgContent( Term.createTerm("mover180turnanswer(RESULT)"), Term.createTerm("mover180turnanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
					}
					 transition( edgeName="goto",targetState="reply", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("reply") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						answer("moveruturn", "moveruturnanswer", "moveruturnanswer(OK)"   )  
						updateResourceRep( "moveruturn(reply)"  
						)
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("error") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "moveruturn(error)"  
						)
						answer("move", "moveruturnanswer", "moveruturnanswer(ERROR)"   )  
						println("moveruturn | ERROR STATE")
					}
				}	 
			}
		}
}
