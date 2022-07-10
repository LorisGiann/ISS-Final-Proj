/* Generated by AN DISI Unibo */ 
package it.unibo.led

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Led ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = true
					}
					 transition(edgeName="t00",targetState="handle_update",cond=whenEvent("update_led"))
				}	 
				state("handle_update") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("update_led(STATE)"), Term.createTerm("update_led(on)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("update_led(${payloadArg(0)})")
						}
					}
				}	 
				state("off") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("update_led(STATE)"), Term.createTerm("update_led(off)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("s2:msg1:msg1(${payloadArg(0)})")
								delay(1000) 
						}
					}
				}	 
			}
		}
}
