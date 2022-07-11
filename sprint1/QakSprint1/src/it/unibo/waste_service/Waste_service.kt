/* Generated by AN DISI Unibo */ 
package it.unibo.waste_service

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Waste_service ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 fun checkdepositpossible(MATERIAL:String,LOAD:String) : Boolean {
		 				return (MATERIAL=="plastic" && LOAD.toInt()+contPB<=MAXPB) 
		 				 || (MATERIAL=="glass" && LOAD.toInt()+contGB<=MAXGB);
		 			} 
		 			
				const val MAXPB = 10
				const val MAXGB = 10
				lateinit var contPB : String 
				lateinit var contGB : String
				lateinit var Material  : String
				lateinit var TruckLoad : String
				lateint var RES : String
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						discardMessages = false
					}
					 transition(edgeName="t00",targetState="handle_req",cond=whenRequest("depositrequest"))
				}	 
				state("handle_req") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("depositrequest(MATERIAL,TRUCKLOAD)"), Term.createTerm("depositrequest(MATERIAL,TRUCKLOAD)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												 Material 	= payloadArg(0) ;
												 TruckLoad 	= payloadArg(1) ;
								if(  checkdepositpossible( Material, TruckLoad )  
								 ){request("move", "move("INDOOR")" ,"transporttrolley" )  
								}
								else
								 {answer("depositrequest", "loadrejected", "loadrejected($Material,$TruckLoad)"   )  
								 }
						}
					}
					 transition(edgeName="t01",targetState="handle_move_indoor",cond=whenReply("moveanswer"))
				}	 
				state("handle_move_indoor") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("moveanswer(RESULT)"), Term.createTerm("moveanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES == "OK"  
								 ){request("pickup", "pickup(_)" ,"transporttrolley" )  
								}
								forward("noMsg", "noMsg(_)" ,"waste_service" ) 
						}
					}
					 transition(edgeName="t02",targetState="handle_pickup_answer",cond=whenDispatchGuarded("noMsg",{ RES=="OK"  
					}))
					transition(edgeName="t03",targetState="error",cond=whenDispatchGuarded("noMsg",{ RES!="OK"  
					}))
				}	 
				state("handle_pickup_answer") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("pickupanswer(RESULT)"), Term.createTerm("pickupanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES == "OK"  
								 ){request("move", "move($Material)" ,"transporttrolley" )  
								answer("depositrequest", "loadaccept", "loadaccept($Material,$TruckLoad)"   )  
								}
								forward("noMsg", "noMsg(_)" ,"waste_service" ) 
						}
					}
					 transition(edgeName="t04",targetState="handle_move_container",cond=whenDispatchGuarded("noMsg",{ RES=="OK"  
					}))
					transition(edgeName="t05",targetState="error",cond=whenDispatchGuarded("noMsg",{ RES!="OK"  
					}))
				}	 
				state("handle_move_container") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("moveanswer(RESULT)"), Term.createTerm("moveanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES == "OK"  
								 ){request("dropout", "dropout(_)" ,"transporttrolley" )  
								}
								forward("noMsg", "noMsg(_)" ,"waste_service" ) 
						}
					}
					 transition(edgeName="t06",targetState="handle_dropout_answer",cond=whenDispatchGuarded("noMsg",{ RES=="OK"  
					}))
					transition(edgeName="t07",targetState="error",cond=whenDispatchGuarded("noMsg",{ RES!="OK"  
					}))
				}	 
				state("handle_dropout_answer") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("dropoutanswer(RESULT)"), Term.createTerm("dropoutanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES != "OK"  
								 ){forward("noMsg", "noMsg(_)" ,"waste_service" ) 
								}
						}
						stateTimer = TimerActor("timer_handle_dropout_answer", 
							scope, context!!, "local_tout_waste_service_handle_dropout_answer", 10.toLong() )
					}
					 transition(edgeName="t08",targetState="handle_move_home",cond=whenTimeout("local_tout_waste_service_handle_dropout_answer"))   
					transition(edgeName="t09",targetState="error",cond=whenDispatchGuarded("noMsg",{ RES!="OK"  
					}))
					transition(edgeName="t010",targetState="handle_new_request",cond=whenRequest("depositrequest"))
				}	 
				state("handle_new_request") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("depositrequest(MATERIAL,TRUCKLOAD)"), Term.createTerm("depositrequest(MATERIAL,TRUCKLOAD)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												 Material 	= payloadArg(0) ;
												 TruckLoad 	= payloadArg(1) ;
								if(  checkdepositpossible( Material, TruckLoad )  
								 ){request("move", "move("INDOOR")" ,"transporttrolley" )  
								}
								else
								 {answer("depositrequest", "loadrejected", "loadrejected($Material,$TruckLoad)"   )  
								 forward("noMsg", "noMsg(_)" ,"waste_service" ) 
								 }
						}
					}
					 transition(edgeName="t011",targetState="handle_move_indoor",cond=whenReply("moveanswer"))
					transition(edgeName="t012",targetState="handle_move_home",cond=whenDispatch("noMsg"))
				}	 
				state("handle_move_home") { //this:State
					action { //it:State
						request("move", "move("HOME")" ,"transporttrolley" )  
					}
					 transition( edgeName="goto",targetState="handle_move_home_answer", cond=doswitch() )
				}	 
				state("handle_move_home_answer") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("moveanswer(RESULT)"), Term.createTerm("moveanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES == "OK"  
								 ){request("dropout", "dropout(_)" ,"transporttrolley" )  
								}
								else
								 {forward("noMsg", "noMsg(_)" ,"waste_service" ) 
								 }
						}
					}
					 transition(edgeName="t013",targetState="handle_req",cond=whenRequest("depositrequest"))
					transition(edgeName="t014",targetState="error",cond=whenDispatchGuarded("noMsg",{ RES!="OK"  
					}))
					transition(edgeName="t015",targetState="wait",cond=whenReply("moveanswer"))
				}	 
				state("error") { //this:State
					action { //it:State
						println("error")
						println("$name in ${currentState.stateName} | $currentMsg")
					}
				}	 
			}
		}
}
