/* Generated by AN DISI Unibo */ 
package it.unibo.alarmreceiverpickupdropdown

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Alarmreceiverpickupdropdown ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "alarmreceiverpickupdropdown(wait)"  
						)
						if( checkMsgContent( Term.createTerm("disable(_)"), Term.createTerm("disable(BOOL)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  payloadArg(0).toBoolean()  
								 ){forward("disable", "disable(_)" ,"pickupdropouthandler" ) 
								}
								else
								 {forward("enable", "enable(_)" ,"pickupdropouthandler" ) 
								 }
						}
					}
					 transition(edgeName="t019",targetState="wait",cond=whenEvent("disable"))
				}	 
			}
		}
}
