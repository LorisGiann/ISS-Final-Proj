package testSprint1;

import it.unibo.ctxserver.MainCtxserverKt;
import it.unibo.kactor.ActorBasic;
import it.unibo.kactor.QakContext;
import jdk.jfr.Enabled;
import org.eclipse.californium.core.CoapHandler;
import org.junit.*;
import unibo.comm22.coap.CoapConnection;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommSystemConfig;
import unibo.comm22.utils.CommUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;


public class TestSprint1 {
	private CoapConnection connTransportTrolley, connWasteService;
	private TestObserver to;
	private ProcessHandle processHandle;

	private Process prServer,prRobot;
	private Runtime rtServer,rtRobot;

	@Before
	public void up() {
		CommSystemConfig.tracing=false;

		try {
			rtRobot = Runtime.getRuntime();
			prRobot = rtRobot.exec("gradle -PmainClass=it.unibo.ctxrobot.MainCtxrobotKt execute");
			processHandle = prRobot.toHandle();
			if (prRobot.isAlive()) {
				ColorsOut.outappl("launch context robot", ColorsOut.ANSI_PURPLE);
			}
			InputStream is = prRobot.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				if(line.contains("WAIT/RETRY TO SET PROXY TO")) break;
			}
			br.close();
		}catch(IOException e){
			ColorsOut.outappl("Errore launch " , ColorsOut.RED);
			System.exit(1);
		}

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

		to=new TestObserver();
		startObserverCoap("localhost", to);
  	}

	protected void waitForApplStarted(){
		ActorBasic wasteservice = QakContext.Companion.getActor("wasteservice");
		//ActorBasic transporttrolley = QakContext.Companion.getActor("transporttrolley");
		while( wasteservice == null ){
			ColorsOut.outappl("TestSprint1 waits for appl ... " , ColorsOut.GREEN);
			CommUtils.delay(200);
			wasteservice = QakContext.Companion.getActor("wasteservice");
		}
		CommUtils.delay(100);
	}

	@After
	public void down() {
		processHandle.destroy();
		prRobot.destroy();
		ColorsOut.outappl("TestSprint1 ENDS" , ColorsOut.BLUE);
	}

	@Test
	public void dummy() {
		assertTrue(true);
	}

	@Test
	public void test_accepted() {
		ColorsOut.outappl("testLoadok STARTS" , ColorsOut.BLUE);
		String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)";
		//assertTrue( coapCheckWasteService("home") );
		try{
			ConnTcp connTcp   = new ConnTcp("localhost", 8095);
			String answer     = connTcp.request(truckRequestStr);
 			ColorsOut.outappl("test_accepted answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadaccept"));
			while(!coapCheckWasteService("wait")){
				CommUtils.delay(1000);
			}
			connTcp.close();
			ColorsOut.outappl(to.getHistory().toString(), ColorsOut.MAGENTA);
			//assertTrue(to.checkNextContents(new String[]{"wasteservice(wait,0,0)", "transporttrolley(wait,HOME,HOME)"}) > 0);
			assertTrue(to.checkNextSequence(new String[]{
					"wasteservice(handle_req,0,0)",
					"transporttrolley(wait,HOME,INDOOR)",
					"transporttrolley(wait,INDOOR,INDOOR)",
					"transporttrolley(picking_up,INDOOR,INDOOR)"
			}));
			assertTrue(to.checkNextContents(new String[]{"wasteservice(handle_pickup_answer,0,2)", "transporttrolley(wait,INDOOR,GLASSBOX)"}) > 0); //check container load update. (handle_pickup_answer also moves the robot to the container)
			assertTrue(to.checkNextSequence(new String[]{
					"transporttrolley(wait,PLASTICBOX,GLASSBOX)",
					"transporttrolley(wait,GLASSBOX,GLASSBOX)",
					"transporttrolley(dropping_down,GLASSBOX,GLASSBOX)"
			}));
			assertTrue(to.checkNextContents(new String[]{"wasteservice(handle_move_home,0,2)", "transporttrolley(wait,GLASSBOX,HOME)"}) > 0);
			assertTrue(to.checkNextContents(new String[]{"wasteservice(wait,0,2)", "transporttrolley(wait,HOME,HOME)"}) > 0);
			//to.setStartPosition(0);*/
		}catch(Exception e){
			fail("test_accepted ERROR:" + e.getMessage());
		}
 	}

	@Test
	public void test_rejected() {
		ColorsOut.outappl("testLoadKo STARTS" , ColorsOut.BLUE);
		String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,11),1)";
		//assertTrue( coapCheckWasteService("home") );
		try{
			ConnTcp connTcp   = new ConnTcp("localhost", 8095);
			String answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("test_rejected answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadrejected"));
			while(!coapCheckWasteService("wait")){
				CommUtils.delay(1000);
			}
			connTcp.close();
			ColorsOut.outappl(to.getHistory().toString(), ColorsOut.MAGENTA);
			assertTrue(to.checkNextContents(new String[]{"wasteservice(wait,0,0)", "transporttrolley(wait,HOME,HOME)"}) > 0);
			assertTrue(to.checkNextSequence(new String[]{
					"wasteservice(handle_req,0,0)",
					"wasteservice(wait,0,0)"
			}));
		}catch(Exception e){
			ColorsOut.outerr("test_rejected ERROR:" + e.getMessage());
		}

	}


	 @Test
	 public void test_2_accepted(){
		 CommUtils.delay(100);
		 String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)";
		 try{
			 //FIRST REQUEST
			 ConnTcp connTcp   = new ConnTcp("localhost", 8095);
			 String answer     = connTcp.request(truckRequestStr);
			 ColorsOut.outappl("test_2_accepted answer=" + answer , ColorsOut.GREEN);
			 assertTrue(answer.contains("loadaccept"));
			 while(!coapCheckWasteService("wait")){
				 CommUtils.delay(1000);
			 }
			 //SECONDO REQUEST
			 truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,7),1)";
			 answer     = connTcp.request(truckRequestStr);
			 ColorsOut.outappl("testSecondRequest answer=" + answer , ColorsOut.GREEN);
			 assertTrue(answer.contains("loadaccept"));
			 while(!coapCheckWasteService("wait")){
				 CommUtils.delay(1000);
			 }
			 connTcp.close();
		 }catch(Exception e){
			 ColorsOut.outerr("test_2_accepted ERROR:" + e.getMessage());

		 }
	 }

	@Test
	 public void test_1_accepted_1_rejected(){
		CommUtils.delay(100);
		String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,2),1)";
		try{
			//FIRST REQUEST
			ConnTcp connTcp   = new ConnTcp("localhost", 8095);
			String answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testFirstRequest answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadaccept"));
			while(!coapCheckWasteService("wait")){
				CommUtils.delay(1000);
			}
			//SECONDO REQUEST
			truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,9),1)";
			answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testSecondRequest answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadrejected"));
			while(!coapCheckWasteService("wait")){
				CommUtils.delay(1000);
			}
			connTcp.close();
		}catch(Exception e){
			ColorsOut.outerr("test_1_accepted_1_rejected ERROR:" + e.getMessage());

		}
	}

	@Test
	public void test_2_accepted_while_in_operation(){ //the second request is made while the robot is still in operation, while still dropping down material
		CommUtils.delay(100);
		String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)";
		try{
			//FIRST REQUEST
			ConnTcp connTcp   = new ConnTcp("localhost", 8095);
			String answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testFirstRequest answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadaccept"));
			while(!coapCheckWasteService("handle_move_container")){
				CommUtils.delay(800);
			}
			CommUtils.delay(100);
			//SECONDO REQUEST
			truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,7),1)";
			answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testSecondRequest answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadaccept"));
			while(!coapCheckWasteService("wait")){
				CommUtils.delay(1000);
			}
			connTcp.close();
			assertTrue(to.checkNextContents(new String[]{"wasteservice(wait,0,0)", "transporttrolley(wait,HOME,HOME)"}) > 0);
			assertTrue(to.checkNextSequence(new String[]{
					"wasteservice(handle_req,0,0)",
					"transporttrolley(wait,HOME,INDOOR)",
					"transporttrolley(wait,INDOOR,INDOOR)",
					"transporttrolley(picking_up,INDOOR,INDOOR)"
			}));
			assertTrue(to.checkNextContents(new String[]{"wasteservice(handle_pickup_answer,2,0)", "transporttrolley(wait,INDOOR,PLASTICBOX)"}) > 0); //check container load update. (handle_pickup_answer also moves the robot to the container)
			assertTrue(to.checkNextSequence(new String[]{
					"transporttrolley(wait,PLASTICBOX,PLASTICBOX)",
					"transporttrolley(dropping_down,PLASTICBOX,PLASTICBOX)"
			}));
			assertTrue(to.checkNextContents(new String[]{"wasteservice(handle_new_req,2,0)", "transporttrolley(wait,PLASTICBOX,INDOOR)"}) > 0);
			assertTrue(to.checkNextContent("wasteservice(handle_move_indoor,2,7)") > 0);
			//second request management...
			assertTrue(to.checkNextContent("wasteservice(wait,2,7)") > 0);
		}catch(Exception e){
			ColorsOut.outerr("test_2_accepted_while_in_operation ERROR:" + e.getMessage());
		}
	}

	@Test
	public void test_1_accepted_1_rejected_while_in_operation(){ //the second request is made while the robot is still in operation, while still dropping down material
		CommUtils.delay(100);
		String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)";
		try{
			//FIRST REQUEST
			ConnTcp connTcp   = new ConnTcp("localhost", 8095);
			String answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testFirstRequest answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadaccept"));
			while(!coapCheckWasteService("handle_move_container")){
				CommUtils.delay(800);
			}
			CommUtils.delay(100);
			//SECONDO REQUEST
			truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,11),1)";
			answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testSecondRequest answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadrejected"));
			while(!coapCheckWasteService("wait")){
				CommUtils.delay(1000);
			}
			connTcp.close();
			assertTrue(to.checkNextContents(new String[]{"wasteservice(wait,0,0)", "transporttrolley(wait,HOME,HOME)"}) > 0);
			assertTrue(to.checkNextSequence(new String[]{
					"wasteservice(handle_req,0,0)",
					"transporttrolley(wait,HOME,INDOOR)",
					"transporttrolley(wait,INDOOR,INDOOR)",
					"transporttrolley(picking_up,INDOOR,INDOOR)"
			}));
			assertTrue(to.checkNextContents(new String[]{"wasteservice(handle_pickup_answer,2,0)", "transporttrolley(wait,INDOOR,PLASTICBOX)"}) > 0); //check container load update. (handle_pickup_answer also moves the robot to the container)
			assertTrue(to.checkNextSequence(new String[]{
					"transporttrolley(wait,PLASTICBOX,PLASTICBOX)",
					"transporttrolley(dropping_down,PLASTICBOX,PLASTICBOX)"
			}));
			assertTrue(to.checkNextContents(new String[]{"wasteservice(handle_new_req,2,0)", "transporttrolley(wait,PLASTICBOX,HOME)", "wasteservice(handle_move_home,2,0)"}) > 0);
			//robot moves towards home
			assertTrue(to.checkNextContent("wasteservice(wait,2,0)") > 0);
		}catch(Exception e){
			ColorsOut.outerr("test_1_accepted_1_rejected_while_in_operation ERROR:" + e.getMessage());
		}
	}

	@Test
	public void test_2_accepted_while_returning_home(){ //the second request is made while the robot is still in operation, while returning to home
		CommUtils.delay(100);
		String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)";
		try{
			//FIRST REQUEST
			ConnTcp connTcp   = new ConnTcp("localhost", 8095);
			String answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testFirstRequest answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadaccept"));
			while(!coapCheckWasteService("handle_move_home")){
				CommUtils.delay(800);
			}
			CommUtils.delay(100);
			//SECONDO REQUEST
			truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,7),1)";
			answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testSecondRequest answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadaccept"));
			while(!coapCheckWasteService("wait")){
				CommUtils.delay(1000);
			}
			connTcp.close();
			assertTrue(to.checkNextContents(new String[]{"wasteservice(wait,0,0)", "transporttrolley(wait,HOME,HOME)"}) > 0);
			assertTrue(to.checkNextSequence(new String[]{
					"wasteservice(handle_req,0,0)",
					"transporttrolley(wait,HOME,INDOOR)",
					"transporttrolley(wait,INDOOR,INDOOR)",
					"transporttrolley(picking_up,INDOOR,INDOOR)"
			}));
			assertTrue(to.checkNextContents(new String[]{"wasteservice(handle_pickup_answer,2,0)", "transporttrolley(wait,INDOOR,PLASTICBOX)"}) > 0); //check container load update. (handle_pickup_answer also moves the robot to the container)
			assertTrue(to.checkNextSequence(new String[]{
					"transporttrolley(wait,PLASTICBOX,PLASTICBOX)",
					"transporttrolley(dropping_down,PLASTICBOX,PLASTICBOX)"
			}));
			assertTrue(to.checkNextContents(new String[]{"wasteservice(handle_move_home,2,0)", "transporttrolley(wait,PLASTICBOX,HOME)"}) > 0);
			assertTrue(to.checkNextContent("wasteservice(home,2,0)") < 0); //wasteservice shouldnt pass through wait...
			assertTrue(to.checkNextContent("wasteservice(handle_req,2,0)") > 0); //...it should go directly to handle_req
			//second request management...
			assertTrue(to.checkNextContent("wasteservice(wait,2,7)") > 0);
		}catch(Exception e){
			ColorsOut.outerr("test_2_accepted_while_returning_home ERROR:" + e.getMessage());
		}
	}
	@Test
	public void test_1_accepted_1_rejected_while_returning_home(){ //the second request is made while the robot is still in operation, while returning home
		CommUtils.delay(100);
		String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(PLASTIC,2),1)";
		try{
			//FIRST REQUEST
			ConnTcp connTcp   = new ConnTcp("localhost", 8095);
			String answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testFirstRequest answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadaccept"));
			while(!coapCheckWasteService("handle_move_home")){
				CommUtils.delay(800);
			}
			CommUtils.delay(100);
			//SECONDO REQUEST
			truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(GLASS,11),1)";
			answer     = connTcp.request(truckRequestStr);
			ColorsOut.outappl("testSecondRequest answer=" + answer , ColorsOut.GREEN);
			assertTrue(answer.contains("loadrejected"));
			while(!coapCheckWasteService("wait")){
				CommUtils.delay(1000);
			}
			CommUtils.delay(8000);//this is a special case: we are in wait even if the robot is still moving towards home. Gire the robot some extra more time
			connTcp.close();
			assertTrue(to.checkNextContents(new String[]{"wasteservice(wait,0,0)", "transporttrolley(wait,HOME,HOME)"}) > 0);
			assertTrue(to.checkNextSequence(new String[]{
					"wasteservice(handle_req,0,0)",
					"transporttrolley(wait,HOME,INDOOR)",
					"transporttrolley(wait,INDOOR,INDOOR)",
					"transporttrolley(picking_up,INDOOR,INDOOR)"
			}));
			assertTrue(to.checkNextContents(new String[]{"wasteservice(handle_pickup_answer,2,0)", "transporttrolley(wait,INDOOR,PLASTICBOX)"}) > 0); //check container load update. (handle_pickup_answer also moves the robot to the container)
			assertTrue(to.checkNextSequence(new String[]{
					"transporttrolley(wait,PLASTICBOX,PLASTICBOX)",
					"transporttrolley(dropping_down,PLASTICBOX,PLASTICBOX)"
			}));
			assertTrue(to.checkNextContents(new String[]{"wasteservice(handle_move_home,2,0)", "transporttrolley(wait,PLASTICBOX,HOME)"}) > 0);
			//assertTrue(to.checkNextContent("wasteservice(home,2,0)") < 0); //wasteservice shouldnt pass through wait...
			assertTrue(to.checkNextContent("wasteservice(handle_req,2,0)") > 0); //...it should go directly to handle_req
			assertTrue(to.checkNextContent("wasteservice(wait,2,0)") > 0); //then it goes into wait, since request is rejected
		}catch(Exception e){
			ColorsOut.outerr("test_1_accepted_1_rejected_while_returning_home ERROR:" + e.getMessage());
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
					String ctxqakdest1       = "ctxserver";
					String ctxqakdest2       = "ctxrobot";
					String applPort1         = "8095";
					String applPort2         = "8096";
					connWasteService        = new CoapConnection(addr+":"+applPort1, ctxqakdest1+"/"+qakdestination1);
					connTransportTrolley    = new CoapConnection(addr+":"+applPort2, ctxqakdest2+"/"+qakdestination2);
					connWasteService.observeResource( handler );
					connTransportTrolley.observeResource( handler );
					ColorsOut.outappl("connecting via Coap conn:" + connWasteService , ColorsOut.CYAN);
					ColorsOut.outappl("connecting via Coap conn:" + connTransportTrolley , ColorsOut.CYAN);
					while(connWasteService.request("")=="0"){
						ColorsOut.outappl("waiting for conn " + connWasteService , ColorsOut.CYAN);
						CommUtils.delay(500);
					}
					while(connTransportTrolley.request("")=="0"){
						ColorsOut.outappl("waiting for conn " + connTransportTrolley , ColorsOut.CYAN);
						CommUtils.delay(500);
					}
				}catch(Exception e){
					ColorsOut.outerr("connectUsingCoap ERROR:"+e.getMessage());
				}
			}
		}.start();

}
}