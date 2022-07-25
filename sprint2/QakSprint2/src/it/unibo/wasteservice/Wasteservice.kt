/* Generated by AN DISI Unibo */ 
package it.unibo.wasteservice

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Wasteservice ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 lateinit var Material  : ws.Material
			   var TruckLoad : Float = 0F
			   
			   lateinit var RES : String
			   lateinit var TrolleyPos : String   //gbox,pbox,Home,indoor
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						updateResourceRep( "wasteservice(wait,${ws.func.contPB},${ws.func.contGB})"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						discardMessages = false
						 TrolleyPos = "home"  
						println("Waiting for requests")
					}
					 transition(edgeName="t00",targetState="handle_req",cond=whenRequest("depositrequest"))
					transition(edgeName="t01",targetState="wait",cond=whenReply("moveanswer"))
				}	 
				state("handle_req") { //this:State
					action { //it:State
						updateResourceRep( "wasteservice(handle_req,${ws.func.contPB},${ws.func.contGB})"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("depositrequest(MATERIAL,TRUCKLOAD)"), Term.createTerm("depositrequest(MATERIAL,TRUCKLOAD)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												 Material 	= ws.Material.valueOf(payloadArg(0))
												 TruckLoad 	= payloadArg(1).toFloat()
								println("arrived $TruckLoad Kg of $Material")
								if(  ws.func.checkdepositpossible( Material, TruckLoad )  
								 ){ ws.func.updateDeposit( Material, TruckLoad ) 
								println("PB capacity: ${ws.func.contPB}, GB capacity: ${ws.func.contGB}")
								request("move", "move(INDOOR)" ,"transporttrolley" )  
								}
								else
								 {answer("depositrequest", "loadrejected", "loadrejected($Material,$TruckLoad)"   )  
								 forward("noMsg", "noMsg(_)" ,"wasteservice" ) 
								 }
						}
					}
					 transition(edgeName="t02",targetState="handle_move_indoor",cond=whenReply("moveanswer"))
					transition(edgeName="t03",targetState="wait",cond=whenDispatch("noMsg"))
				}	 
				state("handle_move_indoor") { //this:State
					action { //it:State
						updateResourceRep( "wasteservice(handle_move_indoor,${ws.func.contPB},${ws.func.contGB})"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("moveanswer(RESULT)"), Term.createTerm("moveanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES == "OK"  
								 ){println("wateservice - request pickup")
								request("pickup", "pickup(_)" ,"transporttrolley" )  
								}
								else
								 {forward("noMsg", "noMsg(_)" ,"wasteservice" ) 
								 }
						}
					}
					 transition(edgeName="t04",targetState="handle_pickup_answer",cond=whenReply("pickupanswer"))
					transition(edgeName="t05",targetState="error",cond=whenDispatch("noMsg"))
				}	 
				state("handle_pickup_answer") { //this:State
					action { //it:State
						updateResourceRep( "wasteservice(handle_pickup_answer,${ws.func.contPB},${ws.func.contGB})"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("pickupanswer(RESULT)"), Term.createTerm("pickupanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES == "OK"  
								 ){ 
													val Position = when(Material){
													    ws.Material.PLASTIC -> ws.Position.PLASTICBOX
													    ws.Material.GLASS -> ws.Position.GLASSBOX
													}  
								println("move to ${Position}")
								request("move", "move($Position)" ,"transporttrolley" )  
								answer("depositrequest", "loadaccept", "loadaccept($Material,$TruckLoad)"   )  
								}
								else
								 {forward("noMsg", "noMsg(_)" ,"wasteservice" ) 
								 }
						}
					}
					 transition(edgeName="t06",targetState="handle_move_container",cond=whenReply("moveanswer"))
					transition(edgeName="t07",targetState="error",cond=whenDispatch("noMsg"))
				}	 
				state("handle_move_container") { //this:State
					action { //it:State
						updateResourceRep( "wasteservice(handle_move_container,${ws.func.contPB},${ws.func.contGB})"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("moveanswer(RESULT)"), Term.createTerm("moveanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES == "OK"  
								 ){println("wateservice - request dropout ${Material}")
								request("dropout", "dropout(_)" ,"transporttrolley" )  
								}
								else
								 {forward("noMsg", "noMsg(_)" ,"wasteservice" ) 
								 }
						}
					}
					 transition(edgeName="t08",targetState="handle_dropout_answer",cond=whenReply("dropoutanswer"))
					transition(edgeName="t09",targetState="error",cond=whenDispatch("noMsg"))
				}	 
				state("handle_dropout_answer") { //this:State
					action { //it:State
						updateResourceRep( "wasteservice(handle_dropout_answer,${ws.func.contPB},${ws.func.contGB})"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("dropoutanswer(RESULT)"), Term.createTerm("dropoutanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES != "OK"  
								 ){forward("noMsg", "noMsg(_)" ,"wasteservice" ) 
								}
						}
						stateTimer = TimerActor("timer_handle_dropout_answer", 
							scope, context!!, "local_tout_wasteservice_handle_dropout_answer", 10.toLong() )
					}
					 transition(edgeName="t010",targetState="move_home",cond=whenTimeout("local_tout_wasteservice_handle_dropout_answer"))   
					transition(edgeName="t011",targetState="error",cond=whenDispatchGuarded("noMsg",{ RES!="OK"  
					}))
					transition(edgeName="t012",targetState="handle_new_req",cond=whenRequest("depositrequest"))
				}	 
				state("handle_new_req") { //this:State
					action { //it:State
						updateResourceRep( "wasteservice(handle_new_req,${ws.func.contPB},${ws.func.contGB})"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("depositrequest(MATERIAL,TRUCKLOAD)"), Term.createTerm("depositrequest(MATERIAL,TRUCKLOAD)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												 Material 	= ws.Material.valueOf(payloadArg(0))
												 TruckLoad 	= payloadArg(1).toFloat()
								if(  ws.func.checkdepositpossible( Material, TruckLoad )  
								 ){ ws.func.updateDeposit( Material, TruckLoad ) 
								println("PB capacity: ${ws.func.contPB}, GB capacity: ${ws.func.contGB}")
								request("move", "move(INDOOR)" ,"transporttrolley" )  
								}
								else
								 {answer("depositrequest", "loadrejected", "loadrejected($Material,$TruckLoad)"   )  
								 forward("noMsg", "noMsg(_)" ,"wasteservice" ) 
								 }
						}
					}
					 transition(edgeName="t013",targetState="handle_move_indoor",cond=whenReply("moveanswer"))
					transition(edgeName="t014",targetState="move_home",cond=whenDispatch("noMsg"))
				}	 
				state("move_home") { //this:State
					action { //it:State
						updateResourceRep( "wasteservice(move_home,${ws.func.contPB},${ws.func.contGB})"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						request("move", "move(HOME)" ,"transporttrolley" )  
					}
					 transition( edgeName="goto",targetState="handle_move_home", cond=doswitch() )
				}	 
				state("handle_move_home") { //this:State
					action { //it:State
						updateResourceRep( "wasteservice(handle_move_home,${ws.func.contPB},${ws.func.contGB})"  
						)
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("moveanswer(RESULT)"), Term.createTerm("moveanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES == "OK"  
								 ){request("dropout", "dropout(_)" ,"transporttrolley" )  
								}
								else
								 {forward("noMsg", "noMsg(_)" ,"wasteservice" ) 
								 }
						}
					}
					 transition(edgeName="t015",targetState="handle_req",cond=whenRequest("depositrequest"))
					transition(edgeName="t016",targetState="error",cond=whenDispatchGuarded("noMsg",{ RES!="OK"  
					}))
					transition(edgeName="t017",targetState="wait",cond=whenReply("moveanswer"))
				}	 
				state("error") { //this:State
					action { //it:State
						updateResourceRep( "wasteservice(error,${ws.func.contPB},${ws.func.contGB})"  
						)
						println("error")
						println("$name in ${currentState.stateName} | $currentMsg")
					}
				}	 
			}
		}
}
