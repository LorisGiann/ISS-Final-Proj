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
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						discardMessages = false
					}
					 transition(edgeName="t01",targetState="handle_req",cond=whenRequest("transporttrolleycmd"))
				}	 
				state("handle_req") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("transporttrolleycmd(CMD)"), Term.createTerm("transporttrolleycmd(CMD)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								request("step", "step(_)" ,"basicrobot" )  
								forward("cmd", "cmd(_)" ,"basicrobot" ) 
								emit("update_tt_state", "update_tt_state(_)" ) 
								emit("update_position", "update_position(_)" ) 
						}
					}
					 transition( edgeName="goto",targetState="handling_req", cond=doswitch() )
				}	 
				state("handling_req") { //this:State
					action { //it:State
					}
					 transition(edgeName="t02",targetState="handle_obstacle",cond=whenEvent("obstacle"))
					transition(edgeName="t03",targetState="wait",cond=whenEvent("info"))
				}	 
				state("handle_obstacle") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="handle_req", cond=doswitch() )
				}	 
			}
		}
}
