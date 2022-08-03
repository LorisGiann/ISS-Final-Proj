package testSprint1

import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.apache.log4j.RollingFileAppender
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommUtils
import java.io.BufferedReader
import java.io.InputStreamReader


class TestUtils {

    companion object {

        private var logger: Logger? = null
        private var appender: RollingFileAppender? = null

        private fun initlogger(fname : String){
            val logger = Logger.getLogger(TestUtils.javaClass)
            val appender = RollingFileAppender()
            appender.setLayout(PatternLayout("%d %-5p %c{1}:%L - %m%n"));
            appender.setFile("logs/$fname.txt");
            appender.setAppend(false);
            appender.setMaxFileSize("100MB");
            appender.setMaxBackupIndex(10);
            appender.activateOptions();

            logger.setAdditivity(false);
            logger.addAppender(appender);

            this.appender=appender
            this.logger=logger
        }

        /**
         * Terminate all the process listening fo TCP connections on the specified port
         */
        fun terminateProcOnPort(port:Int) : Unit {
            //for this to work give the permission to run with sudo the commands kill and ss, by addinthe following lines to /etc/sudoers:
            //<username> ALL=NOPASSWD:/usr/bin/ss
            //<username> ALL=NOPASSWD:/usr/bin/kill
            val rt = Runtime.getRuntime()
            val pr = ProcessBuilder(listOf("/bin/bash", "-c", "sudo kill -9 \$( sudo ss -ntlp | grep " + port + " | awk -F 'pid=' '{ print \$2 }' | cut -d',' -f1 )")).start()
            val processHandle = pr.toHandle()
            val `is` = pr.getInputStream()
            val isr = InputStreamReader(`is`)
            val br = BufferedReader(isr)
            var line: String?
            while (br.readLine().also { line = it } != null) {
                ColorsOut.outappl(line, ColorsOut.YELLOW)
            }
            br.close()
            pr.waitFor() //wait until the task is done = process exits
            processHandle!!.destroy()
            pr.destroy()
        }

        fun runCtx(jarPath : String) : Pair<Process, ProcessHandle> {
            ColorsOut.outappl("lauching JAR " + jarPath, ColorsOut.BLUE)
            val rt = Runtime.getRuntime()
            //prRobot = rtRobot.exec("gradle -PmainClass=it.unibo.ctxrobot.MainCtxrobotKt execute");   //problem with this is that every time it recompiles everithing: this takes ages if dome for every single test
            val pr = rt.exec("java -jar " + jarPath)
            val processHandle = pr.toHandle()
            CommUtils.delay(200)
            if (!processHandle!!.isAlive()) {
                ColorsOut.outappl("JAR launch failed, exit code " + pr.exitValue(), ColorsOut.RED)
                System.exit(1)
            }
            val `is` = pr.getInputStream()
            val isr = InputStreamReader(`is`)
            val br = BufferedReader(isr)
            var line: String?
            while (br.readLine().also { line = it } != null) {
                ColorsOut.outappl(line, ColorsOut.YELLOW)
                if (line!!.contains("WAIT/RETRY TO SET PROXY TO")) break
                if (line!!.contains("PROXY DONE TO")) break
                if (line!!.contains("msg(")) break //one context only, application already started
                if (line!!.contains("WARNING: Address already in use (Bind failed)")){
                    ColorsOut.outappl("JAR launch failed, process already running", ColorsOut.RED)
                    System.exit(1)
                }
            }
            //br.close()
            object : Thread(){
                override fun run(){
                    initlogger(jarPath.substringAfterLast('/'))
                    while (br.readLine().also { line = it } != null) {
                        //ColorsOut.outappl(line, ColorsOut.YELLOW)
                        logger!!.info(line)
                    }
                    logger!!.removeAppender(appender);
                }
            }.start()
            return Pair(pr,processHandle)
        }

        /*
        Creates the fat jar file runngin the main class specified (ex: it.unibo.ctxrobot.MainCtxrobotKt)
        output jar in build/libs/<mainClassName>.jar
         */
        fun compileCtx(mainClassName : String) : Unit{
            ColorsOut.outappl("Genrating JAR" + mainClassName, ColorsOut.BLUE)
            val rtRobot = Runtime.getRuntime()
            val prRobot = rtRobot.exec("gradle -PmainClass=" + mainClassName + " jar")
            val processHandle = prRobot.toHandle()
            val exitval = prRobot.waitFor() //wait until the task is done = process exits
            if (exitval != 0) {
                ColorsOut.outappl("Jar creation failed", ColorsOut.RED)
                System.exit(1)
            }
            processHandle.destroy()
            prRobot.destroy()
        }
    }
}