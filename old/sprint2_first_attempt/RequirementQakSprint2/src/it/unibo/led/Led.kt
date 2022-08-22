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
			   lateinit var newState : String 
			   //set domainsystemconfig
			   `it.unibo`.radarSystem22.domain.utils.DomainSystemConfig.simulation = true
			   `it.unibo`.radarSystem22.domain.utils.DomainSystemConfig.ledGui      = true 
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = true
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "led(off,false)"  
						)
						println("${name} STARTS")
						 ledM = `it.unibo`.radarSystem22.domain.models.LedModel.create()  
					}
					 transition(edgeName="t00",targetState="handle_update",cond=whenEvent("update_led"))
				}	 
				state("handle_update") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("update_led(STATE)"), Term.createTerm("update_led(ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 newState = payloadArg(0)  
								println("update_led(${newState})")
								forward("noMsg", "noMsg(_)" ,"led" ) 
						}
					}
					 transition(edgeName="toNewState1",targetState="off",cond=whenDispatchGuarded("noMsg",{ newState=="off"  
					}))
					transition(edgeName="toNewState2",targetState="on",cond=whenDispatchGuarded("noMsg",{ newState=="on"  
					}))
					transition(edgeName="toNewState3",targetState="blink_on",cond=whenDispatchGuarded("noMsg",{ newState=="blink"  
					}))
				}	 
				state("off") { //this:State
					action { //it:State
						 ledM.turnOff() 
						println("Led off")
						 unibo.actor22comm.utils.ColorsOut.outappl("${name} - off", unibo.actor22comm.utils.ColorsOut.GREEN) 
						updateResourceRep( "led(off,${ledM.getState()})"  
						)
					}
					 transition(edgeName="t04",targetState="handle_update",cond=whenEvent("update_led"))
				}	 
				state("on") { //this:State
					action { //it:State
						 unibo.actor22comm.utils.ColorsOut.outappl("${name} - on", unibo.actor22comm.utils.ColorsOut.GREEN) 
						println("Led on")
						 ledM.turnOn() 
						updateResourceRep( "led(on,${ledM.getState()})"  
						)
					}
					 transition(edgeName="t05",targetState="handle_update",cond=whenEvent("update_led"))
				}	 
				state("blink_on") { //this:State
					action { //it:State
						 unibo.actor22comm.utils.ColorsOut.outappl("${name} - on", unibo.actor22comm.utils.ColorsOut.GREEN) 
						println("Blinking on")
						 ledM.turnOn() 
						updateResourceRep( "led(blink,${ledM.getState()})"  
						)
						stateTimer = TimerActor("timer_blink_on", 
							scope, context!!, "local_tout_led_blink_on", 500.toLong() )
					}
					 transition(edgeName="t06",targetState="blink_off",cond=whenTimeout("local_tout_led_blink_on"))   
					transition(edgeName="t07",targetState="handle_update",cond=whenEvent("update_led"))
				}	 
				state("blink_off") { //this:State
					action { //it:State
						 unibo.actor22comm.utils.ColorsOut.outappl("${name} - off", unibo.actor22comm.utils.ColorsOut.GREEN) 
						println("Blinking off")
						 ledM.turnOff() 
						updateResourceRep( "led(blink,${ledM.getState()})"  
						)
						stateTimer = TimerActor("timer_blink_off", 
							scope, context!!, "local_tout_led_blink_off", 500.toLong() )
					}
					 transition(edgeName="t08",targetState="blink_on",cond=whenTimeout("local_tout_led_blink_off"))   
					transition(edgeName="t09",targetState="handle_update",cond=whenEvent("update_led"))
				}	 
			}
		}
}