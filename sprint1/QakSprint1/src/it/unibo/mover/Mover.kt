/* Generated by AN DISI Unibo */ 
package it.unibo.mover

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Mover ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 var DEST = ws.Position.HOME
			   var CURRPOS = ws.Position.HOME
			   lateinit var RES : String     
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(wait,$CURRPOS,$DEST)"  
						)
					}
					 transition(edgeName="t022",targetState="handle",cond=whenRequest("move"))
				}	 
				state("handle") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("move(POSITION)"), Term.createTerm("move(POS)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 DEST = ws.Position.valueOf(payloadArg(0)) 
						}
						updateResourceRep( "mover(handle,$CURRPOS,$DEST)"  
						)
					}
					 transition( edgeName="goto",targetState="req_forward", cond=doswitchGuarded({ DEST!=CURRPOS  
					}) )
					transition( edgeName="goto",targetState="wait", cond=doswitchGuarded({! ( DEST!=CURRPOS  
					) }) )
				}	 
				state("req_forward") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(req_forward,$CURRPOS,$DEST)"  
						)
						request("cmdsync", "cmdsync(w)" ,"basicrobotwrapper" )  
					}
					 transition(edgeName="t023",targetState="chk_forward",cond=whenReply("cmdanswer"))
				}	 
				state("chk_forward") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(chk_forward,$CURRPOS,$DEST)"  
						)
						if( checkMsgContent( Term.createTerm("cmdanswer(RESULT)"), Term.createTerm("cmdanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
					}
					 transition( edgeName="goto",targetState="req_turn", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("req_turn") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(req_turn,$CURRPOS,$DEST)"  
						)
						request("cmdsync", "cmdsync(l)" ,"basicrobotwrapper" )  
					}
					 transition(edgeName="t024",targetState="chk_turn",cond=whenReply("cmdanswer"))
				}	 
				state("chk_turn") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(chk_turn,$CURRPOS,$DEST)"  
						)
						if( checkMsgContent( Term.createTerm("cmdanswer(RESULT)"), Term.createTerm("cmdanswer(RES)"), 
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
						 CURRPOS=ws.func.nextPosition(CURRPOS)  
						if(  CURRPOS==DEST  
						 ){answer("move", "moveanswer", "moveanswer(OK)"   )  
						}
						updateResourceRep( "mover(reply,$CURRPOS,$DEST)"  
						)
						stateTimer = TimerActor("timer_reply", 
							scope, context!!, "local_tout_mover_reply", 10.toLong() )
					}
					 transition(edgeName="t025",targetState="handle",cond=whenTimeout("local_tout_mover_reply"))   
					transition(edgeName="t026",targetState="handle",cond=whenRequest("move"))
				}	 
				state("error") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(error,$CURRPOS,$DEST)"  
						)
						println("mover | ERROR STATE")
					}
				}	 
			}
		}
}
