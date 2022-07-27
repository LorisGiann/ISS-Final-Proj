/* Generated by AN DISI Unibo */ 
package it.unibo.test_led

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Test_led ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						discardMessages = true
						emit("update_led", "update_led(off)" ) 
						delay(2000) 
						emit("update_led", "update_led(on)" ) 
						delay(2000) 
						emit("update_led", "update_led(blink)" ) 
						delay(2000) 
					}
				}	 
			}
		}
}
