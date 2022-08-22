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
		 lateinit var ledM : `it.unibo`.radarSystem22.domain.interfaces.ILed
				
				`it.unibo`.radarSystem22.domain.utils.DomainSystemConfig.ledGui=true
				var newState = ws.LedState.OFF
				
				val actor = this@Led;
				suspend fun transitNow(stateName : String){
					var res = actor.handleCurrentMessage(NoMsg,actor.getStateByName(stateName));
					if(res) actor.elabMsgInState( );
					else println("ERROR: transition was not possible")
				}
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = true
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "led(initial,${newState})"  
						)
						 ledM = `it.unibo`.radarSystem22.domain.models.LedModel.create()  
						 ledM.turnOff() 
					}
					 transition(edgeName="t050",targetState="handle_update",cond=whenEvent("update_led"))
				}	 
				state("handle_update") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("update_led(LEDSTATE)"), Term.createTerm("update_led(ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 newState = ws.LedState.valueOf(payloadArg(0))  
								 val stateName = when(newState) {
									 					ws.LedState.OFF -> "off"
									 					ws.LedState.ON -> "on"
									 					ws.LedState.BLINK -> "blink_on"
									 			   }
											   transitNow(stateName)
						}
						updateResourceRep( "led(handle_update,${newState})"  
						)
					}
				}	 
				state("off") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "led(off,${newState})"  
						)
						 unibo.actor22comm.utils.ColorsOut.outappl("${name} - off", unibo.actor22comm.utils.ColorsOut.GREEN) 
						 ledM.turnOff() 
					}
					 transition(edgeName="t051",targetState="handle_update",cond=whenEvent("update_led"))
				}	 
				state("on") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "led(on,${newState})"  
						)
						 unibo.actor22comm.utils.ColorsOut.outappl("${name} - on", unibo.actor22comm.utils.ColorsOut.GREEN) 
						 ledM.turnOn() 
					}
					 transition(edgeName="t052",targetState="handle_update",cond=whenEvent("update_led"))
				}	 
				state("blink_on") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "led(blink_on,${newState})"  
						)
						 unibo.actor22comm.utils.ColorsOut.outappl("${name} - on (blinking)", unibo.actor22comm.utils.ColorsOut.GREEN) 
						 ledM.turnOn() 
						stateTimer = TimerActor("timer_blink_on", 
							scope, context!!, "local_tout_led_blink_on", 250.toLong() )
					}
					 transition(edgeName="t053",targetState="blink_off",cond=whenTimeout("local_tout_led_blink_on"))   
					transition(edgeName="t054",targetState="handle_update",cond=whenEvent("update_led"))
				}	 
				state("blink_off") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "led(blink_off,${newState})"  
						)
						 unibo.actor22comm.utils.ColorsOut.outappl("${name} - off (blinking)", unibo.actor22comm.utils.ColorsOut.GREEN) 
						 ledM.turnOff() 
						stateTimer = TimerActor("timer_blink_off", 
							scope, context!!, "local_tout_led_blink_off", 250.toLong() )
					}
					 transition(edgeName="t055",targetState="blink_on",cond=whenTimeout("local_tout_led_blink_off"))   
					transition(edgeName="t056",targetState="handle_update",cond=whenEvent("update_led"))
				}	 
			}
		}
}