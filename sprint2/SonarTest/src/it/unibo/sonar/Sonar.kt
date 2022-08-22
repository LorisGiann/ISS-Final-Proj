/* Generated by AN DISI Unibo */ 
package it.unibo.sonar

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Sonar ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		 val simulate       = false
			   val sonarActorName = "sonar"
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						alarmSonar.configureSonarSubsystem.configureTheSonar( simulate, sonarActorName  )
					}
					 transition( edgeName="goto",targetState="activateTheSonar", cond=doswitch() )
				}	 
				state("activateTheSonar") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "sonar(activateTheSonar,simulate)"  
						)
						if(  simulate  
						 ){forward("sonaractivate", "info(ok)" ,"sonarsimulator" ) 
						}
						else
						 {forward("sonaractivate", "info(ok)" ,"sonardatasource" ) 
						 }
					}
					 transition(edgeName="t04",targetState="handleSonarData",cond=whenEvent("alarmsonar"))
					transition(edgeName="t05",targetState="deactivateTheSonar",cond=whenDispatch("sonardeactivate"))
				}	 
				state("deactivateTheSonar") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "sonar(deactivateTheSonar,simulate)"  
						)
						if(  simulate  
						 ){forward("sonardeactivate", "info(ok)" ,"sonarsimulator" ) 
						}
						else
						 {forward("sonardeactivate", "info(ok)" ,"sonardatasource" ) 
						 }
					}
					 transition( edgeName="goto",targetState="end", cond=doswitch() )
				}	 
				state("handleSonarData") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("distance(V)"), Term.createTerm("distance(D)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 val D = payloadArg(0)  
								updateResourceRep( "sonar(handleSonarData,simulate,${D})"  
								)
								emit("sonardata", "distance($D)" ) 
						}
					}
					 transition(edgeName="t06",targetState="handleSonarData",cond=whenEvent("alarmsonar"))
					transition(edgeName="t07",targetState="deactivateTheSonar",cond=whenDispatch("sonardeactivate"))
				}	 
				state("end") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "sonar(end,simulate)"  
						)
					}
					 transition(edgeName="t08",targetState="activateTheSonar",cond=whenDispatch("sonaractivate"))
				}	 
			}
		}
}