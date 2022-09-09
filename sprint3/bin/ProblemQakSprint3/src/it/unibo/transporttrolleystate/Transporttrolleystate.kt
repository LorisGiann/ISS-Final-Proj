/* Generated by AN DISI Unibo */ 
package it.unibo.transporttrolleystate

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Transporttrolleystate ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		 var STATE = ws.Transporttrolleystate.IDLE  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						CoapObserverSupport(myself, "127.0.0.1","8096","ctxrobot","transporttrolley")
						CoapObserverSupport(myself, "127.0.0.1","8096","ctxrobot","basicrobotwrapper")
						CoapObserverSupport(myself, "127.0.0.1","8096","ctxrobot","pickupdropouthandler")
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t00",targetState="handleUpdate",cond=whenDispatch("coapUpdate"))
				}	 
				state("handleUpdate") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("coapUpdate(RESOURCE,VALUE)"), Term.createTerm("coapUpdate(RESOURCE,VALUE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("update: ${payloadArg(0)} ${payloadArg(1)}")
								updateResourceRep( "transporttrolleystate(STATE)"  
								)
								discardMessages = false
						}
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition(edgeName="t01",targetState="handleUpdate",cond=whenDispatch("coapUpdate"))
				}	 
			}
		}
}
