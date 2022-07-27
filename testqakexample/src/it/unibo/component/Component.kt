/* Generated by AN DISI Unibo */ 
package it.unibo.component

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Component ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 val actor = this@Component;
				suspend fun transitNow(stateName : String){
					var res = actor.handleCurrentMessage(NoMsg,actor.getStateByName(stateName));
					if(res) actor.elabMsgInState( );
					else println("ERROR: transition was not possible")
				}
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						discardMessages = false
						delay(2000) 
					}
					 transition( edgeName="goto",targetState="s1", cond=doswitch() )
				}	 
				state("s1") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if(  actor.checkMsgQueueStore("myMsg1")  
						 ){println("body transition")
						 transitNow("myMsg1")  
						}
						else
						 {println("no msg1")
						 }
					}
					 transition(edgeName="t00",targetState="stateMsg1",cond=whenEvent("myMsg1"))
					transition(edgeName="t01",targetState="stateMsg2",cond=whenEvent("myMsg2"))
				}	 
				state("stateMsg1") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						println("stateMsg1")
					}
					 transition( edgeName="goto",targetState="s0", cond=doswitch() )
				}	 
				state("stateMsg2") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						println("stateMsg2")
					}
					 transition( edgeName="goto",targetState="s0", cond=doswitch() )
				}	 
			}
		}
}