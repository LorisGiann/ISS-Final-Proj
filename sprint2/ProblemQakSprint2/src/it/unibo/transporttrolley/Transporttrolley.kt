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
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		
				lateinit var RES : String
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "transporttrolley(wait)"  
						)
					}
					 transition(edgeName="toNewState13",targetState="req_pickup",cond=whenRequest("pickup"))
					transition(edgeName="toNewState14",targetState="req_dropout",cond=whenRequest("dropout"))
					transition(edgeName="toNewState15",targetState="req_move",cond=whenRequest("moveto"))
				}	 
				state("req_pickup") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "transporttrolley(req_pickup)"  
						)
						request("pickup", "pickup(_)" ,"pickupdropouthandler" )  
					}
					 transition(edgeName="toNewState16",targetState="chk_pickup",cond=whenReply("pickupanswer"))
				}	 
				state("chk_pickup") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "transporttrolley(chk_pickup)"  
						)
						if( checkMsgContent( Term.createTerm("pickupanswer(RESULT)"), Term.createTerm("pickupanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
								if(  RES=="OK"  
								 ){answer("pickup", "pickupanswer", "pickupanswer(OK)"   )  
								}
								else
								 {answer("pickup", "pickupanswer", "pickupanswer(ERROR)"   )  
								 }
						}
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("req_dropout") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "transporttrolley(req_dropout)"  
						)
						request("dropout", "dropout(_)" ,"pickupdropouthandler" )  
					}
					 transition(edgeName="toNewState17",targetState="chk_dropout",cond=whenReply("dropoutanswer"))
				}	 
				state("chk_dropout") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "transporttrolley(chk_dropout)"  
						)
						if( checkMsgContent( Term.createTerm("dropoutanswer(RESULT)"), Term.createTerm("dropoutanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
								if(  RES=="OK"  
								 ){answer("dropout", "dropoutanswer", "dropoutanswer(OK)"   )  
								}
								else
								 {answer("dropout", "dropoutanswer", "dropoutanswer(ERROR)"   )  
								 }
						}
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("req_move") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						 var Pos : ws.Position? = null  
						if( checkMsgContent( Term.createTerm("moveto(POSITION)"), Term.createTerm("moveto(POS)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Pos=ws.Position.valueOf(payloadArg(0))  
								request("moveto", "moveto($Pos)" ,"mover" )  
						}
						updateResourceRep( "transporttrolley(req_move,$Pos)"  
						)
					}
					 transition(edgeName="toNewState18",targetState="chk_move",cond=whenReply("movetoanswer"))
					transition(edgeName="toNewState19",targetState="req_move",cond=whenRequest("moveto"))
				}	 
				state("chk_move") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "transporttrolley(chk_move)"  
						)
						if( checkMsgContent( Term.createTerm("movetoanswer(RESULT)"), Term.createTerm("movetoanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
								if(  RES=="OK"  
								 ){answer("moveto", "movetoanswer", "movetoanswer(OK)"   )  
								}
								else
								 {answer("moveto", "movetoanswer", "movetoanswer(ERROR)"   )  
								 }
						}
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
			}
		}
}
