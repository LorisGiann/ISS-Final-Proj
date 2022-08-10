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
		 var MOVE : ws.Move? = null   
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						discardMessages = true
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "basicrobotwrapper(wait)"  
						)
					}
					 transition(edgeName="t042",targetState="handle",cond=whenRequest("cmdsync"))
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
						forward("cmd", "cmd($MoveStr)" ,"basicrobot" ) 
						if(  MOVE==ws.Move.l || MOVE==ws.Move.r  
						 ){delay(500) 
						}
						delay(100) 
						answer("cmdsync", "cmdanswer", "cmdanswer(OK)"   )  
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("forward_cmd") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "basicrobotwrapper(forward_cmd)"  
						)
						forward("cmd", "cmd(w)" ,"basicrobot" ) 
					}
					 transition(edgeName="t043",targetState="collision",cond=whenEvent("info"))
					transition(edgeName="t044",targetState="handle",cond=whenRequest("cmdsync"))
				}	 
				state("collision") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "basicrobotwrapper(collision)"  
						)
						answer("cmdsync", "cmdanswer", "cmdanswer(OK)"   )  
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
			}
		}
}
