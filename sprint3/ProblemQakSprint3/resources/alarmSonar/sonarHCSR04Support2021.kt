/*
 sonarHCSR04Support2021
 A CodedQactor that is activated by the  dispatch  sonarstart : sonarstart(V).
 Launches a process p that activates SonarAlone.
 Reads data from the InputStream of p and, for each value,
 emits the event   sonarrobot : sonar( V ).
 */
package alarmSonar

import java.io.BufferedReader
import java.io.InputStreamReader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import it.unibo.kactor.ActorBasic		//qak 2020
import kotlinx.coroutines.delay
import it.unibo.kactor.MsgUtil
import it.unibo.kactor.ApplMessage
import it.unibo.kactor.IApplMessage
import unibo.comm22.utils.CommUtils


class sonarHCSR04Support2021 ( name : String ) : ActorBasic( name ) {
	lateinit var reader : BufferedReader
	var p : ProcessHandle? = null
	//var coapSupport = javacode.CoapSupport("coap://localhost:8028","ctxsonarresource/sonarresource")

	override suspend fun actorBody(msg : IApplMessage){
		//println("$tt $name | received  $msg "  )  //RICEVE GLI EVENTI!!!
		if( msg.msgId() == "sonaractivate"){
			println("sonarHCSR04Support2021 STARTING")
			try{
				var p  = Runtime.getRuntime().exec("sudo ./SonarAlone")
				reader = BufferedReader(  InputStreamReader(p.getInputStream() ))
				doRead(   )
			}catch( e : Exception){
				println("WARNING: sonarHCSR04Support2021 does not find SonarAlone")
			}
 		}
		if( msg.msgId() == "sonardeactivate") {
			reader?.close()
			p?.destroy()
			CommUtils.delay(100)
			p?.destroyForcibly()
		}
	}
		
	suspend fun doRead(   ){
		var counter = 0
		GlobalScope.launch{	//to allow message handling
			while( true ){
				var data = reader.readLine()
				println("sonarHCSR04Support data = $data"   )
				if( data != null ){
					try{
						val v = data.toInt()
						val m1 = "distance( ${v} )"
						//val event = MsgUtil.buildEvent( "sonarHCSR04Support","sonar",m1)
						val event = MsgUtil.buildEvent( name,"alarmsonar" , m1)
						//emit( event )  //should be propagated also to the remote resource
						emitLocalStreamEvent( event )		//not propagated to remote actors
						println("sonarHCSR04Support doRead ${counter++}: $event "   )
					}catch(e: Exception){
						println("sonarHCSR04Support doRead ERROR: $e "   )
					}
				}
			}
		}
	}
}