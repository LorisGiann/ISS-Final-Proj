/* Generated by AN DISI Unibo */ 
package it.unibo.gui

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Gui ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "init"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 
				var LedStatus = "off"
				var ContainerGlass = 0
				var ContainerPaper = 0
				var Position = "HOME"
		return { //this:ActionBasciFsm
				state("init") { //this:State
					action { //it:State
					}
					 transition(edgeName="t00",targetState="handlePosition",cond=whenEvent("update_position"))
					transition(edgeName="t01",targetState="handleLed",cond=whenEvent("update_led"))
					transition(edgeName="t02",targetState="handleContainer",cond=whenEvent("update_container"))
				}	 
				state("handleLed") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("update_led(STATE)"), Term.createTerm("update_led(STATO)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 LedStatus = payloadArg(0)  
						}
						println("LedStatus: $LedStatus")
						println("ContainerGlass: $ContainerGlass")
						println("ContainerPaper: $ContainerPaper")
						println("Position: $Position")
					}
					 transition( edgeName="goto",targetState="init", cond=doswitch() )
				}	 
				state("handlePosition") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("update_position(POSITION)"), Term.createTerm("update_position(POSITION)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 Position = payloadArg(0)  
						}
						println("LedStatus: $LedStatus")
						println("ContainerGlass: $ContainerGlass")
						println("ContainerPaper: $ContainerPaper")
						println("Position: $Position")
					}
					 transition( edgeName="goto",targetState="init", cond=doswitch() )
				}	 
				state("handleContainer") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("update_container(MATERIAL,QUANTITA)"), Term.createTerm("update_container(MATERIAL,TRUCKLOAD)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if(  payloadArg(0) == "GLASS"  
								 ){ ContainerGlass = payloadArg(1).toInt()+ContainerGlass  
								}
								else
								 { ContainerPaper = payloadArg(1).toInt()+ContainerPaper  
								 }
						}
						println("LedStatus: $LedStatus")
						println("Position: $Position")
						println("CurrentGlass: $ContainerGlass")
						println("CurrentPaper: $ContainerPaper")
					}
					 transition( edgeName="goto",targetState="init", cond=doswitch() )
				}	 
			}
		}
}