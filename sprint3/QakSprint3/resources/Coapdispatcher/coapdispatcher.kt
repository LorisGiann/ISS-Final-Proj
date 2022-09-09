package coapdispatcher

import alice.tuprolog.Struct
import alice.tuprolog.Term
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.CoapObserverSupport
import it.unibo.kactor.IApplMessage
import it.unibo.kactor.MsgUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import unibo.comm22.coap.CoapConnection
import unibo.comm22.utils.ColorsOut
import ws.Transporttrolleystate

//IDLE, MOVING, PICKINGUP, DROPPINGOUT, HALT
class Coapdispatcher (name : String ) : ActorBasic( name ) {

	val connMap = mutableMapOf<String, CoapClient>()
	init {
		scope.launch { autoMsg(MsgUtil.buildDispatch(name, "autoStartSysMsg", "start", name)) }  //auto-start
	}

	suspend fun initCoapObserver() {
		try {
			delay(500) //improves stability, for some reason...
			println("$tt $name | connecting")
			coapObsserve("transporttrolley")
			coapObsserve("basicrobotwrapper")
			coapObsserve("pickupdropouthandler")
			coapObsserve("mover")
		}catch (e: Exception){
			println("$tt $name | ERROR: ")
			e.printStackTrace(System.err)
			//System.err.println(e.stackTrace)
			//System.exit(0)
		}
	}
	
	override suspend fun actorBody(msg: IApplMessage) {
		MsgUtil.outgreen("$tt $name | msg ${msg.msgId()} : ${msg.toString()}")
		try {
			if (msg.msgId() == "autoStartSysMsg") {
				initCoapObserver()
				//MsgUtil.outgreen("$tt $name | started ")
			}else if(msg.msgId() == "coapUpdate") {
				if ( msg.msgContent().contains("ActorBasic(Resource)") ) return
				if ( msg.msgContent().contains(", )") ) { //this wierd coap message has no content, sign that something went terribly wrong with the connection
					val actor = msg.msgContent().substringAfter('(').substringBefore(',')
					ColorsOut.outappl("$tt $name | coap reconnecting $actor", ColorsOut.CYAN)
					connMap[actor]!!.shutdown()
					coapObsserve(actor)
					return
				}
				forward("coapUpdate", msg.msgContent() ,"transporttrolleystate" )
				forward("coapUpdate", msg.msgContent() ,"ledalarmcontrol" )
			}
		} catch (e: Exception){
			println("$tt $name | ERROR2: ")
			e.printStackTrace(System.err)
			//System.err.println(e.stackTrace)
			//System.exit(0)
		}
	}


	suspend fun coapObsserve(actor : String){
		val ctx : String  = it.unibo.kactor.sysUtil.solve("qactor( $actor, CTX, CLASS)","CTX")!!
		val ctxHost : String  = it.unibo.kactor.sysUtil.solve("getCtxHost($ctx,H)","H")!!
		val ctxPort : String = it.unibo.kactor.sysUtil.solve("getCtxPort($ctx,P)","P")!!
		val conn = CoapConnection("${ctxHost}:${ctxPort}", "$ctx/$actor")
		var i=0;
		while (i<10 && conn.request("") == "0") {
			ColorsOut.outappl("waiting for Coap conn to $actor", ColorsOut.CYAN)
			delay(100)
			i++
		}
		val client = createClient(this, ctxHost, ctxPort, ctx, actor)
		connMap.put(actor,client)
		ColorsOut.outappl("$tt $name | coap connected to $actor", ColorsOut.CYAN)
	}

	fun createClient(
		owner: ActorBasic,
		host: String?,
		port: String,
		ctx: String,
		actorName: String
	): CoapClient {
		val addr = "coap://HOST:".replace("HOST", host!!) + port + "/CONTEXT/".replace("CONTEXT", ctx) + actorName
		val client = CoapClient(addr)
		client.observe( //
		object : CoapHandler {
			override fun onLoad(response: CoapResponse) {
				val content = response.responseText
				//ColorsOut.out("CoapObs | content=$content", ColorsOut.BLUE)
				val actorDispatch = MsgUtil.buildDispatch(
					actorName, "coapUpdate",
					"coapUpdate(RESOURCE, VALUE)".replace("RESOURCE", actorName).replace("VALUE", content),
					owner.name
				)
				owner.sendMsgToMyself(actorDispatch)
			}

			override fun onError() {
				ColorsOut.outerr("OBSERVING FAILED")
			}
		})
		return client
	}
}