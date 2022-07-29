/* Generated by AN DISI Unibo */ 
package it.unibo.pickupdropouthandler

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Pickupdropouthandler ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						discardMessages = false
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(wait)"  
						)
					}
					 transition(edgeName="t019",targetState="do_dropout",cond=whenRequest("dropout"))
					transition(edgeName="t020",targetState="do_pickup",cond=whenRequest("pickup"))
				}	 
				state("do_dropout") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(do_dropout)"  
						)
						println("pickupdropouthandler | DROPOUT STARTING...")
						delay(1000) 
						println("pickupdropouthandler | DROPOUT COMPLETE")
						answer("dropout", "dropoutanswer", "dropoutanswer(OK)"   )  
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("do_pickup") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "pickupdropouthandler(do_pickup)"  
						)
						println("pickupdropouthandler | PICKUP STARTING...")
						delay(1000) 
						println("pickupdropouthandler | PICKUP COMPLETE")
						answer("pickup", "pickupanswer", "pickupanswer(OK)"   )  
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
			}
		}
}
