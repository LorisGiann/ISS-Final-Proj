/* Generated by AN DISI Unibo */ 
package it.unibo.test_trasporttrolley

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Test_trasporttrolley ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 lateinit var RES : String  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = true
						request("move", "move(INDOOR)" ,"transporttrolley" )  
					}
					 transition(edgeName="t022",targetState="s1",cond=whenReply("moveanswer"))
				}	 
				state("s1") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("moveanswer(RESULT)"), Term.createTerm("moveanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES == "OK"  
								 ){println("move indoor")
								request("pickup", "pickup(_)" ,"transporttrolley" )  
								}
								else
								 {println("error")
								 }
						}
					}
					 transition(edgeName="t023",targetState="s2",cond=whenReply("pickupanswer"))
				}	 
				state("s2") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("pickupanswer(RESULT)"), Term.createTerm("pickupanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								request("move", "move(PLASTICBOX)" ,"transporttrolley" )  
						}
					}
					 transition(edgeName="t024",targetState="s3",cond=whenReply("moveanswer"))
				}	 
				state("s3") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("moveanswer(RESULT)"), Term.createTerm("moveanswer(RESULT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 RES = payloadArg(0);  
								if(  RES == "OK"  
								 ){println("move plastic box")
								request("dropout", "dropout(_)" ,"transporttrolley" )  
								}
								else
								 {println("error")
								 }
						}
					}
					 transition(edgeName="t025",targetState="s4",cond=whenReply("dropoutanswer"))
				}	 
				state("s4") { //this:State
					action { //it:State
						println("dropout")
						println("finish")
					}
				}	 
			}
		}
}
