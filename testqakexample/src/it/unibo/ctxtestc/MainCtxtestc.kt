/* Generated by AN DISI Unibo */ 
package it.unibo.ctxtestc
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	QakContext.createContexts(
	        "localhost", this, "demo0.pl", "sysRules.pl"
	)
}

