/* Generated by AN DISI Unibo */ 
package it.unibo.alarmemitter

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Alarmemitter ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 var alarm = false  
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "alarmemitter(wait)"  
						)
						if( checkMsgContent( Term.createTerm("distance(V)"), Term.createTerm("distance(D)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  payloadArg(0).toInt() <= ws.const.DLIMIT && !alarm  
								 ){emit("alarm", "alarm(_)" ) 
								 alarm = true  
								}
								if(  payloadArg(0).toInt() > ws.const.DLIMIT && alarm  
								 ){emit("alarmceased", "alarmceased(_)" ) 
								 alarm = false  
								}
						}
					}
					 transition(edgeName="t081",targetState="wait",cond=whenEvent("local_sonardata"))
				}	 
			}
		}
}
