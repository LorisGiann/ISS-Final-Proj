package test

import it.unibo.kactor.MsgUtil
import org.junit.Assert
import org.junit.jupiter.api.*
import testCommon.*
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import java.io.IOException
import java.time.Duration
import kotlin.test.Test

//@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class Dummy {

    @Test
    fun dummy() {
        val to = TestObserver()
        Assert.assertTrue(true)
        to!!.debugSetHistory(
            listOf("mover(wait,HOME,HOME,ACLK)", "moveruturn(wait,null)", "basicrobotwrapper(wait)", "mover(handle,HOME,HOME,ACLK)", "mover(wait,HOME,HOME,ACLK)")
                    as MutableList<String>
        )

        // --------------------------------------------------------------------- CHECKING HISTORY ---------------------------------------------------------------------
        //no real action should happen
        Assertions.assertTrue(to!!.checkNextContent("mover(handle,HOME,HOME,ACLK)") >= 0)
        Assertions.assertTrue(to!!.checkNextContent("mover(wait,HOME,HOME,ACLK)") >= 0)
        Assertions.assertFalse(to!!.checkNextContent("mover(*)") >= 0)

        to!!.nextCheckIndex=0
        Assertions.assertFalse(to!!.checkNextContent("basicrobotwrapper(handle,*)") >= 0)

    }

}