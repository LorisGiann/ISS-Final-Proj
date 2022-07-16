/* Generated by AN DISI Unibo */ 
package it.unibo.transporttrolley

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Transporttrolley ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "init"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 lateinit var dest : String
			   lateinit var currpos : String
			   var RobotType     = "" 
			   
			   fun newPosition(CURRPOS:String) : String {
		 			if (CURRPOS=="HOME"){
		 				return "INDOOR";
		 			}
		 			if (CURRPOS=="INDOOR"){
		 				return "PLASTICBOX";
		 			}
		 			if (CURRPOS=="PLASTICBOX"){
		 				return "GLASSBOX";
		 			}
		 			if (CURRPOS=="GLASSBOX"){
		 				return "HOME";
		 			}
		 			return "ERRORE";
		 		} 			
			   
		return { //this:ActionBasciFsm
				state("init") { //this:State
					action { //it:State
						discardMessages = true
						 dest = "HOME"
								   currpos = "HOME"	
						println("Init trasport trolley")
						unibo.robot.robotSupport.create(myself ,"basicrobotConfig.json" )
						 RobotType = unibo.robot.robotSupport.robotKind  
						delay(1000) 
						if(  RobotType != "virtual"  
						 ){ var robotsonar = context!!.hasActor("realsonar")  
						        	   unibo.robot.robotSupport.createSonarPipe(myself) 
						}
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("wait") { //this:State
					action { //it:State
						println("Wait")
						println("Dest: ${dest} CurrPos: ${currpos}")
						if(  currpos!=dest  
						 ){forward("noMsg", "noMsg(_)" ,"transporttrolley" ) 
						}
					}
					 transition(edgeName="toNewState17",targetState="picking_up",cond=whenRequestGuarded("pickup",{ currpos==dest 
					}))
					transition(edgeName="toNewState18",targetState="dropping_down",cond=whenRequestGuarded("dropout",{ currpos==dest 
					}))
					transition(edgeName="toNewState19",targetState="set_new_dest",cond=whenRequestGuarded("move",{ currpos==dest 
					}))
					transition(edgeName="toNewState20",targetState="forward_robot",cond=whenDispatch("noMsg"))
				}	 
				state("picking_up") { //this:State
					action { //it:State
						println("PickUp material from truck")
						delay(1000) 
						answer("pickup", "pickupanswer", "pickupanswer(OK)"   )  
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("dropping_down") { //this:State
					action { //it:State
						println("DropOut material in container")
						delay(1000) 
						answer("dropout", "dropoutanswer", "dropoutanswer(OK)"   )  
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("set_new_dest") { //this:State
					action { //it:State
						println("Set new destination robot")
						if( checkMsgContent( Term.createTerm("move(POSITION)"), Term.createTerm("move(ARG)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 dest= payloadArg(0)  
								println("move(${dest})")
						}
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("forward_robot") { //this:State
					action { //it:State
						println("Forward robot")
						unibo.robot.robotSupport.move( "w"  )
					}
					 transition(edgeName="t121",targetState="turn",cond=whenDispatch("obstacle"))
				}	 
				state("turn") { //this:State
					action { //it:State
						println("Turn robot")
						unibo.robot.robotSupport.move( "l"  )
						delay(350) 
						currpos=newPosition(currpos)  
						answer("move", "moveanswer", "moveanswer(OK)"   )  
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
			}
		}
}
