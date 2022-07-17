package testSprint1;

import static org.junit.Assert.assertTrue;

import it.unibo.ctxserver.MainCtxserverKt;
import it.unibo.kactor.ActorBasic;
import it.unibo.kactor.QakContext;
import org.eclipse.californium.core.CoapHandler;
import org.junit.*;
import unibo.comm22.coap.CoapConnection;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommSystemConfig;
import unibo.comm22.utils.CommUtils;


public class TestSprint1 {
private CoapConnection conn;

	@Before
	public void up() {
		CommSystemConfig.tracing=false;
		startObserverCoap("localhost", new TrolleyPosObserver());
		new Thread(){
			public void run(){
				MainCtxserverKt.main();
			}
		}.start();
		/*
		new Thread(){
			public void run(){
				MainCtxrobotKt.main();
			}
		}.start();

		 */
		waitForApplStarted();
  	}

 	protected void waitForApplStarted(){
		ActorBasic wasteservice = QakContext.Companion.getActor("wasteservice");
		//ActorBasic transporttrolley = QakContext.Companion.getActor("transporttrolley");
		while( wasteservice == null ){
			ColorsOut.outappl("TestSprint1 waits for appl ... " , ColorsOut.GREEN);
			CommUtils.delay(20);
			wasteservice = QakContext.Companion.getActor("wasteservice");
			CommUtils.delay(20);
			//transporttrolley = QakContext.Companion.getActor("transporttrolley");
		}


	}
	@After
	public void down() {
		ColorsOut.outappl("TestSprint1 ENDS" , ColorsOut.BLUE);
	}

	@Test
	public void testLoadok() {
		CommUtils.delay(100);
		ColorsOut.outappl("testLoadok STARTS" , ColorsOut.BLUE);
		//assertTrue( coapCheck("home") );
		String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)";
		try{
			ConnTcp connTcp   = new ConnTcp("localhost", 8095);
			String answer     = connTcp.request(truckRequestStr);
 			ColorsOut.outappl("testLoadok answer=" + answer , ColorsOut.GREEN);


			assertTrue(answer.contains("loadaccept"));
			CommUtils.delay(10000);
			connTcp.close();

			//TODO: problema dei tempi
			//assertTrue( coapCheck("indoor") );
			//CommUtils.delay(1000);
			//assertTrue( coapCheck("gbox") );
			//TODO: controllare la history
			//CommUtils.delay(3000);
		}catch(Exception e){
			ColorsOut.outerr("testLoadok ERROR:" + e.getMessage());

		}

 	}

//---------------------------------------------------

protected boolean coapCheck(String check){
	String answer = conn.request("");
	ColorsOut.outappl("coapCheck answer=" + answer, ColorsOut.CYAN);
	return answer.contains(check);
}
protected void startObserverCoap(String addr, CoapHandler handler){
		new Thread(){
			public void run(){
				try {
					String ctxqakdest       = "serverctx";
					String qakdestination 	= "wasteservice";
					String applPort         = "8095";
					String path             = ctxqakdest+"/"+qakdestination;
					conn                    = new CoapConnection(addr+":"+applPort, path);
					conn.observeResource( handler );
					ColorsOut.outappl("connected via Coap conn:" + conn , ColorsOut.CYAN);
				}catch(Exception e){
					ColorsOut.outerr("connectUsingCoap ERROR:"+e.getMessage());
				}
			}
		}.start();

}
}