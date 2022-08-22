/* Generated by AN DISI Unibo */ 
package it.unibo.componentc

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Componentc ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "componentc(s0)"  
						)
						discardMessages = false
					}
					 transition(edgeName="t04",targetState="s2",cond=whenRequest("btoc"))
				}	 
				state("s2") { //this:State
					action { //it:State
						updateResourceRep( "componentc(s2)"  
						)
						answer("btoc", "ctob", "ctob(_)"   )  
						println("Componentc | DONE")
					}
				}	 
			}
		}
}
