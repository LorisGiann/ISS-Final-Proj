/* Generated by AN DISI Unibo */ 
package it.unibo.basicrobot

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Basicrobot ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		
		  var StepTime      = 0L
		  var StartTime     = 0L
		  var Duration      = 0L
		  var ExpectingCollision = false
		  //var RobotType     = "" 
		  var CurrentMove   = "unkknown"
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = false
						println("basicrobot | START")
						unibo.robot.robotSupport.create(myself ,"basicrobotConfig.json" )
						updateResourceRep( "basicrobot(s0)"  
						)
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("work") { //this:State
					action { //it:State
						println("basicrobot | waiting .............. ")
					}
					 transition(edgeName="t170",targetState="execcmd",cond=whenDispatch("cmd"))
					transition(edgeName="t171",targetState="doStep",cond=whenRequest("step"))
					transition(edgeName="t172",targetState="handleObstacle",cond=whenDispatch("obstacle"))
					transition(edgeName="t173",targetState="endwork",cond=whenDispatch("end"))
				}	 
				state("execcmd") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("cmd(MOVE)"), Term.createTerm("cmd(MOVE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 val Move = payloadArg(0)  
								 if(Move=="w") ExpectingCollision=true  
								println("basicrobot | executing '${Move}'")
								unibo.robot.robotSupport.move( Move  )
								updateResourceRep( "basicrobot(execcmd,$Move)"  
								)
						}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("handleObstacle") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if(  ExpectingCollision  
						 ){ ExpectingCollision=false  
						unibo.robot.robotSupport.move( "h"  )
						delay(600) 
						updateResourceRep( "basicrobot(handleObstacle,${CurrentMove})"  
						)
						emit("info", "info(obstacledoing(w))" ) 
						}
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("doStep") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("step(TIME)"), Term.createTerm("step(T)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
									StepTime = payloadArg(0).toLong() 	 
								updateResourceRep( "basicrobot(doStep,${StepTime})"  
								)
						}
						StartTime = getCurrentTime()
						println("basicrobot | doStep StepTime =$StepTime  ")
						unibo.robot.robotSupport.move( "w"  )
						stateTimer = TimerActor("timer_doStep", 
							scope, context!!, "local_tout_basicrobot_doStep", StepTime )
					}
					 transition(edgeName="t074",targetState="stepDone",cond=whenTimeout("local_tout_basicrobot_doStep"))   
					transition(edgeName="t075",targetState="stepFail",cond=whenDispatch("obstacle"))
				}	 
				state("stepDone") { //this:State
					action { //it:State
						unibo.robot.robotSupport.move( "h"  )
						updateResourceRep( "basicrobot(stepDone,$StepTime)"  
						)
						answer("step", "stepdone", "stepdone(ok)"   )  
						println("basicrobot | stepDone reply done")
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("stepFail") { //this:State
					action { //it:State
						Duration = getDuration(StartTime)
						unibo.robot.robotSupport.move( "h"  )
						 var TunedDuration = Duration;
									TunedDuration = Duration * 5 / 6
						println("basicrobot | stepFail duration=$Duration TunedDuration=$TunedDuration")
						unibo.robot.robotSupport.move( "s"  )
						delay(TunedDuration)
						unibo.robot.robotSupport.move( "h"  )
						updateResourceRep( "basicrobot(stepFail,$Duration)"  
						)
						answer("step", "stepfail", "stepfail($Duration,obst)"   )  
					}
					 transition( edgeName="goto",targetState="work", cond=doswitch() )
				}	 
				state("endwork") { //this:State
					action { //it:State
						updateResourceRep( "basicrobot(endwork)"  
						)
						terminate(1)
					}
				}	 
			}
		}
}
