/* Generated by AN DISI Unibo */ 
package it.unibo.ctxrobot
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	QakContext.createContexts(
	        "127.0.0.1", this, "demosystemarchitecture.pl", "sysRules.pl"
	)
}

