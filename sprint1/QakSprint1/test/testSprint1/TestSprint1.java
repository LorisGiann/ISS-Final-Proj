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
private CoapConnection connTransportTrolley, connWasteService;

	@Before
	public void up() {
		CommSystemConfig.tracing=false;
		startObserverCoap("localhost", new TestObserver());
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
		CommUtils.delay(2500);
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
		ColorsOut.outappl("testLoadok STARTS" , ColorsOut.BLUE);
		String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)";
		assertTrue( coapCheckWasteService("home") );
		try{
			ConnTcp connTcp   = new ConnTcp("localhost", 8095);
			String answer     = connTcp.request(truckRequestStr);
 			ColorsOut.outappl("testLoadok answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadaccept"));
			assertTrue( coapCheckWasteService("indoor"));
			CommUtils.delay(10000);
			connTcp.close();
		}catch(Exception e){
			ColorsOut.outerr("testLoadok ERROR:" + e.getMessage());

		}

 	}

	 @Test
	 public void testMultiRequest(){
		 CommUtils.delay(100);
		 ColorsOut.outappl("testMultiRequestSTARTS" , ColorsOut.BLUE);
		 //assertTrue( coapCheck("home") );
		 String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)";
		 try{
			 ConnTcp connTcp   = new ConnTcp("localhost", 8095);
			 String answer     = connTcp.request(truckRequestStr);
			 ColorsOut.outappl("testFirstRequest answer=" + answer , ColorsOut.GREEN);
			 assertTrue(answer.contains("loadaccept"));
			 CommUtils.delay(10000);
			 truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,5),1)";
			 answer     = connTcp.request(truckRequestStr);
			 ColorsOut.outappl("testSecondRequest answer=" + answer , ColorsOut.GREEN);
			 assertTrue(answer.contains("loadaccept"));
			 connTcp.close();

			 //TODO: problema dei tempi
			 //assertTrue( coapCheck("indoor") );
			 //CommUtils.delay(1000);
			 //assertTrue( coapCheck("gbox") );
			 //TODO: controllare la history
			 //CommUtils.delay(3000);
		 }catch(Exception e){
			 ColorsOut.outerr("testMultiRequestSTARTS ERROR:" + e.getMessage());

		 }
	 }



//---------------------------------------------------

protected boolean coapCheckWasteService(String check){
	String answer = connWasteService.request("");
	ColorsOut.outappl("coapCheck answer=" + answer, ColorsOut.CYAN);
	return answer.contains(check);
}
protected boolean coapCheckconnTransportTrolley(String check){
	String answer = connTransportTrolley.request("");
	ColorsOut.outappl("coapCheck answer=" + answer, ColorsOut.CYAN);
	return answer.contains(check);
}
protected void startObserverCoap(String addr, CoapHandler handler){
		new Thread(){
			public void run(){
				try {
					String qakdestination1 	= "wasteservice";
					String qakdestination2 	= "transporttrolley";
					String ctxqakdest       = "ctxserver";
					String applPort         = "8095";
					connWasteService        = new CoapConnection(addr+":"+applPort, ctxqakdest+"/"+qakdestination1);
					connTransportTrolley    = new CoapConnection(addr+":"+applPort, ctxqakdest+"/"+qakdestination2);
					connWasteService.observeResource( handler );
					connTransportTrolley.observeResource( handler );
					ColorsOut.outappl("connected via Coap conn:" + connWasteService , ColorsOut.CYAN);
					ColorsOut.outappl("connected via Coap conn:" + connTransportTrolley , ColorsOut.CYAN);
				}catch(Exception e){
					ColorsOut.outerr("connectUsingCoap ERROR:"+e.getMessage());
				}
			}
		}.start();

}
}