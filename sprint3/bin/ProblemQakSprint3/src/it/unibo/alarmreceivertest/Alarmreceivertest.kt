/* Generated by AN DISI Unibo */ 
package it.unibo.alarmreceivertest

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Alarmreceivertest ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("alarm(_)"), Term.createTerm("alarm(_)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								updateResourceRep( "alarmreceivertest(wait,alarm)"  
								)
						}
						if( checkMsgContent( Term.createTerm("alarmceased(_)"), Term.createTerm("alarmceased(_)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								updateResourceRep( "alarmreceivertest(wait,alarmceased)"  
								)
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t083",targetState="wait",cond=whenEvent("alarm"))
					transition(edgeName="t084",targetState="wait",cond=whenEvent("alarmceased"))
				}	 
			}
		}
}
