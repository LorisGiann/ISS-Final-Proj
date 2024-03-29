/* Generated by AN DISI Unibo */ 
package it.unibo.alarmreceiverbasicrobot

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Alarmreceiverbasicrobot ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "alarmreceiverpickupdropdown(wait)"  
						)
						if( checkMsgContent( Term.createTerm("alarm(_)"), Term.createTerm("alarm(_)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								forward("alarm", "alarm(_)" ,"basicrobotwrapper" ) 
						}
						if( checkMsgContent( Term.createTerm("alarmceased(_)"), Term.createTerm("alarmceased(_)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								forward("alarmceased", "alarmceased(_)" ,"basicrobotwrapper" ) 
						}
					}
					 transition(edgeName="t056",targetState="wait",cond=whenEvent("alarm"))
					transition(edgeName="t057",targetState="wait",cond=whenEvent("alarmceased"))
				}	 
			}
		}
}
