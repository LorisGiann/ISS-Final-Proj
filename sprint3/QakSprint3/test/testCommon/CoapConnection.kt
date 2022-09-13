package test.testCommon

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapObserveRelation
import org.eclipse.californium.core.coap.CoAP
import unibo.comm22.interfaces.Interaction2021
import unibo.comm22.utils.ColorsOut

class CoapConnection(address: String, path: String) : Interaction2021 {
    private var client: CoapClient? = null
    private var url: String? = null
    private val name = "CoapConn"

    init {
        setCoapClient(address, path)
    }

    protected fun setCoapClient(addressWithPort: String, path: String) {
        url = "coap://$addressWithPort/$path"
        client = CoapClient(url)
        client!!.useExecutor()
        ColorsOut.out(name + " | STARTS client url=" + url, "\u001b[43m")
        client!!.timeout = 1000L
    }

    fun removeObserve(relation: CoapObserveRelation) {
        relation.proactiveCancel()
        ColorsOut.out(name + " | removeObserve !!!!!!!!!!!!!!!" + relation, "\u001b[33m")
    }

    fun observeResource(handler: CoapHandler?): CoapObserveRelation {
        return client!!.observe(handler)
    }

    override fun forward(msg: String) {
        ColorsOut.out(name + " | forward " + url + " msg=" + msg, "\u001b[33m")
        if (client != null) {
            val resp = client!!.put(msg, 0)
            if (resp != null) {
                ColorsOut.out(name + " | forward " + msg + " resp=" + resp.code, "\u001b[33m")
            } else {
                ColorsOut.outerr(name + " | forward - resp null ")
            }
        }
    }

    override fun request(query: String): String {
        val param = if (query.isEmpty()) "" else "?q=$query"
        client!!.uri = url + param
        val respGet = client!!.get()
        //println(respGet.code.codeClass)
        return if (respGet != null && respGet.code.codeClass==2) {
            respGet.responseText
        } else {
            //ColorsOut.out(name + " | request=" + query + " RESPONSE NULL ", "\u001b[31m")
            "0"
        }
    }

    @Throws(Exception::class)
    override fun reply(reqid: String) {
        throw Exception(name + " | reply not allowed")
    }

    @Throws(Exception::class)
    override fun receiveMsg(): String {
        throw Exception(name + " | receiveMsg not allowed")
    }

    override fun close() {
        ColorsOut.out(name + " | client shutdown=" + client)
        client!!.shutdown()
    }
}