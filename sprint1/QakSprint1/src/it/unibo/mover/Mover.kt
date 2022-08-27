/* Generated by AN DISI Unibo */ 
package it.unibo.mover

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Mover ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "wait"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 var DEST = ws.Position.HOME
			   var CURRPOS = ws.Position.HOME
			   var CURRDIR = "ACLK" // ACLK | CLK
			   lateinit var RES : String
		return { //this:ActionBasciFsm
				state("wait") { //this:State
					action { //it:State
						discardMessages = false
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(wait,$CURRPOS,$DEST,$CURRDIR)"  
						)
					}
					 transition(edgeName="t022",targetState="handle",cond=whenRequest("moveto"))
				}	 
				state("handle") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("moveto(POSITION)"), Term.createTerm("moveto(POS)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 DEST = ws.Position.valueOf(payloadArg(0)) 
								if(  DEST==CURRPOS 
								 ){answer("moveto", "movetoanswer", "movetoanswer(OK)"   )  
								}
						}
						updateResourceRep( "mover(handle,$CURRPOS,$DEST,$CURRDIR)"  
						)
					}
					 transition( edgeName="goto",targetState="aclk_or_clk", cond=doswitchGuarded({ DEST!=CURRPOS  
					}) )
					transition( edgeName="goto",targetState="wait", cond=doswitchGuarded({! ( DEST!=CURRPOS  
					) }) )
				}	 
				state("aclk_or_clk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(aclk_or_clk,$CURRPOS,$DEST,$CURRDIR)"  
						)
					}
					 transition( edgeName="goto",targetState="prepare_aclk", cond=doswitchGuarded({ DEST==ws.func.nextPos(CURRPOS) || DEST==ws.func.nextPos(ws.func.nextPos(CURRPOS))  
					}) )
					transition( edgeName="goto",targetState="prepare_clk", cond=doswitchGuarded({! ( DEST==ws.func.nextPos(CURRPOS) || DEST==ws.func.nextPos(ws.func.nextPos(CURRPOS))  
					) }) )
				}	 
				state("prepare_aclk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(prepare_aclk,$CURRPOS,$DEST,$CURRDIR)"  
						)
					}
					 transition( edgeName="goto",targetState="req_pre_turn_aclk", cond=doswitchGuarded({ CURRDIR!="ACLK"  
					}) )
					transition( edgeName="goto",targetState="req_forward_aclk", cond=doswitchGuarded({! ( CURRDIR!="ACLK"  
					) }) )
				}	 
				state("req_pre_turn_aclk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(req_pre_turn_aclk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						request("cmdsync", "cmdsync(r)" ,"basicrobotwrapper" )  
					}
					 transition(edgeName="t023",targetState="chk_pre_turn_aclk",cond=whenReply("cmdanswer"))
				}	 
				state("chk_pre_turn_aclk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(chk_pre_turn_aclk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						if( checkMsgContent( Term.createTerm("cmdanswer(RESULT)"), Term.createTerm("cmdanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
					}
					 transition( edgeName="goto",targetState="req_forward_aclk", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("req_forward_aclk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						 CURRDIR="ACLK"  
						updateResourceRep( "mover(req_forward_aclk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						request("cmdsync", "cmdsync(w)" ,"basicrobotwrapper" )  
					}
					 transition(edgeName="t024",targetState="chk_forward_aclk",cond=whenReply("cmdanswer"))
					transition(edgeName="t025",targetState="set_new_dest_aclk",cond=whenRequest("moveto"))
				}	 
				state("chk_forward_aclk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(chk_forward_aclk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						if( checkMsgContent( Term.createTerm("cmdanswer(RESULT)"), Term.createTerm("cmdanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
					}
					 transition( edgeName="goto",targetState="req_post_turn_aclk", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("set_new_dest_aclk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("moveto(POSITION)"), Term.createTerm("moveto(POS)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 DEST = ws.Position.valueOf(payloadArg(0)) 
						}
						updateResourceRep( "mover(set_new_dest_aclk,$CURRPOS,$DEST,$CURRDIR)"  
						)
					}
					 transition( edgeName="goto",targetState="req_forward_aclk", cond=doswitchGuarded({ DEST==ws.func.nextPos(CURRPOS) || DEST==ws.func.nextPos(ws.func.nextPos(CURRPOS))  
					}) )
					transition( edgeName="goto",targetState="req_u_turn", cond=doswitchGuarded({! ( DEST==ws.func.nextPos(CURRPOS) || DEST==ws.func.nextPos(ws.func.nextPos(CURRPOS))  
					) }) )
				}	 
				state("req_post_turn_aclk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(req_post_turn_aclk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						request("cmdsync", "cmdsync(l)" ,"basicrobotwrapper" )  
					}
					 transition(edgeName="t026",targetState="chk_post_turn_aclk",cond=whenReply("cmdanswer"))
				}	 
				state("chk_post_turn_aclk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(chk_post_turn_aclk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						if( checkMsgContent( Term.createTerm("cmdanswer(RESULT)"), Term.createTerm("cmdanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
					}
					 transition( edgeName="goto",targetState="update_aclk", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("update_aclk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						 CURRPOS=ws.func.nextPos(CURRPOS)  
						updateResourceRep( "mover(update_aclk,$CURRPOS,$DEST,$CURRDIR)"  
						)
					}
					 transition( edgeName="goto",targetState="reply", cond=doswitch() )
				}	 
				state("prepare_clk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(prepare_clk,$CURRPOS,$DEST,$CURRDIR)"  
						)
					}
					 transition( edgeName="goto",targetState="req_pre_turn_clk", cond=doswitchGuarded({ CURRDIR!="CLK"  
					}) )
					transition( edgeName="goto",targetState="req_forward_clk", cond=doswitchGuarded({! ( CURRDIR!="CLK"  
					) }) )
				}	 
				state("req_pre_turn_clk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(req_pre_turn_clk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						request("cmdsync", "cmdsync(l)" ,"basicrobotwrapper" )  
					}
					 transition(edgeName="t027",targetState="chk_pre_turn_clk",cond=whenReply("cmdanswer"))
				}	 
				state("chk_pre_turn_clk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(chk_pre_turn_clk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						if( checkMsgContent( Term.createTerm("cmdanswer(RESULT)"), Term.createTerm("cmdanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
					}
					 transition( edgeName="goto",targetState="req_forward_clk", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("req_forward_clk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						 CURRDIR="CLK"  
						updateResourceRep( "mover(req_forward_clk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						request("cmdsync", "cmdsync(w)" ,"basicrobotwrapper" )  
					}
					 transition(edgeName="t028",targetState="chk_forward_clk",cond=whenReply("cmdanswer"))
					transition(edgeName="t029",targetState="set_new_dest_clk",cond=whenRequest("moveto"))
				}	 
				state("chk_forward_clk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(chk_forward_clk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						if( checkMsgContent( Term.createTerm("cmdanswer(RESULT)"), Term.createTerm("cmdanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
					}
					 transition( edgeName="goto",targetState="req_post_turn_clk", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("set_new_dest_clk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("moveto(POSITION)"), Term.createTerm("moveto(POS)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 DEST = ws.Position.valueOf(payloadArg(0)) 
						}
						updateResourceRep( "mover(set_new_dest_clk,$CURRPOS,$DEST,$CURRDIR)"  
						)
					}
					 transition( edgeName="goto",targetState="req_forward_clk", cond=doswitchGuarded({ DEST==ws.func.prevPos(CURRPOS) || DEST==ws.func.prevPos(ws.func.prevPos(CURRPOS))  
					}) )
					transition( edgeName="goto",targetState="req_u_turn", cond=doswitchGuarded({! ( DEST==ws.func.prevPos(CURRPOS) || DEST==ws.func.prevPos(ws.func.prevPos(CURRPOS))  
					) }) )
				}	 
				state("req_post_turn_clk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(req_post_turn_clk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						request("cmdsync", "cmdsync(r)" ,"basicrobotwrapper" )  
					}
					 transition(edgeName="t030",targetState="chk_post_turn_clk",cond=whenReply("cmdanswer"))
				}	 
				state("chk_post_turn_clk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(chk_post_turn_clk,$CURRPOS,$DEST,$CURRDIR)"  
						)
						if( checkMsgContent( Term.createTerm("cmdanswer(RESULT)"), Term.createTerm("cmdanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
					}
					 transition( edgeName="goto",targetState="update_clk", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("update_clk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						 CURRPOS=ws.func.prevPos(CURRPOS)  
						updateResourceRep( "mover(update_clk,$CURRPOS,$DEST,$CURRDIR)"  
						)
					}
					 transition( edgeName="goto",targetState="reply", cond=doswitch() )
				}	 
				state("req_u_turn") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						request("moveruturn", "moveruturn($CURRDIR)" ,"moveruturn" )  
						updateResourceRep( "mover(req_u_turn,$CURRPOS,$DEST,$CURRDIR)"  
						)
					}
					 transition(edgeName="t031",targetState="chk_u_turn",cond=whenReply("moveruturnanswer"))
					transition(edgeName="t032",targetState="req_u_turn",cond=whenReply("movetoanswer"))
				}	 
				state("chk_u_turn") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("moveruturnanswer(RESULT)"), Term.createTerm("moveruturnanswer(RES)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0) 
						}
						 CURRDIR = when(CURRDIR){
										"ACLK" -> "CLK"
										"CLK"  -> "ACLK"
										else -> {println("moveruturn | error: unknow direction $CURRDIR"); ""}
								   }  
						updateResourceRep( "mover(chk_u_turn,$CURRPOS,$DEST,$CURRDIR)"  
						)
					}
					 transition( edgeName="goto",targetState="reply", cond=doswitchGuarded({ RES=="OK"  
					}) )
					transition( edgeName="goto",targetState="error", cond=doswitchGuarded({! ( RES=="OK"  
					) }) )
				}	 
				state("reply") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if(  CURRPOS==DEST  
						 ){answer("moveto", "movetoanswer", "movetoanswer(OK)"   )  
						}
						updateResourceRep( "mover(reply,$CURRPOS,$DEST,$CURRDIR)"  
						)
						stateTimer = TimerActor("timer_reply", 
							scope, context!!, "local_tout_mover_reply", 10.toLong() )
					}
					 transition(edgeName="t033",targetState="handle",cond=whenTimeout("local_tout_mover_reply"))   
					transition(edgeName="t034",targetState="handle",cond=whenRequest("moveto"))
				}	 
				state("error") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						updateResourceRep( "mover(error,$CURRPOS,$DEST,$CURRDIR)"  
						)
						answer("moveto", "movetoanswer", "movetoanswer(ERROR)"   )  
						println("mover | ERROR STATE")
					}
				}	 
			}
		}
}
