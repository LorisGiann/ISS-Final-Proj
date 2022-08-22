/* Generated by AN DISI Unibo */ 
package it.unibo.componenta

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Componenta ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "componenta(s0)"  
						)
						discardMessages = false
					}
					 transition(edgeName="t00",targetState="s1",cond=whenRequest("start"))
				}	 
				state("s1") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "componenta(s1)"  
						)
						request("atob", "atob(_)" ,"componentb" )  
					}
					 transition(edgeName="t01",targetState="s2",cond=whenReply("btoa"))
				}	 
				state("s2") { //this:State
					action { //it:State
						updateResourceRep( "componenta(s2)"  
						)
						answer("start", "done", "done(_)"   )  
						println("Componenta | DONE")
					}
				}	 
			}
		}
}