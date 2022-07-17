/* Generated by AN DISI Unibo */ 
package it.unibo.ddrrobot

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Ddrrobot ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "init"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		lateinit var move : String
			   var RobotType     = "" 
			   var robotMoveObserver : unibo.actor22comm.interfaces.IObserver
			   lateinit  var conn    : unibo.actor22comm.interfaces.Interaction2021   
		return { //this:ActionBasciFsm
				state("init") { //this:State
					action { //it:State
						discardMessages = true
						println("Init ddrrobot")
						  conn=unibo.Robots.common.RobotUtils.connectWithVirtualRobot(getName()) 
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("wait") { //this:State
					action { //it:State
						discardMessages = true
					}
					 transition(edgeName="t023",targetState="handle_moving",cond=whenRequest("cmd"))
				}	 
				state("handle_moving") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("cmd(MOVE)"), Term.createTerm("cmd(MOVE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 move = payloadArg(0)  
								if( move=="w" 
								 ){println("cmd move(${move})")
								 unibo.Robots.common.VRobotMoves.moveForward(getName(), conn ,10000)  
								}
								if( move=="l" 
								 ){println("cmd move(${move})")
								 unibo.Robots.common.VRobotMoves.turnLeft(getName(), conn)  
								delay(450) 
								forward("noMsg", "noMsg(_)" ,"ddrrobot" ) 
								}
						}
					}
					 transition(edgeName="t124",targetState="handle_answer",cond=whenDispatch("endMoveKo"))
					transition(edgeName="t125",targetState="handle_answer",cond=whenDispatch("noMsg"))
				}	 
				state("handle_answer") { //this:State
					action { //it:State
						println("Handle cmd answer")
						answer("cmd", "cmdanswer", "cmdanswer(OK)"   )  
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
			}
		}
}