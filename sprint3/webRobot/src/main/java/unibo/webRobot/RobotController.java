package unibo.webRobot;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import unibo.comm22.utils.ColorsOut;

//---------------------------------------------------
//import unibo.Robots.common.RobotUtils;


@Controller 
public class RobotController {
    public final static String robotName  = "robottrolley";
    protected String mainPage             = "consoleGui";


    protected String buildThePage(Model viewmodel) {
        return mainPage;
    }

    @GetMapping("/") 		 
    public String entry(Model viewmodel) {
      return buildThePage(viewmodel);
   }

  
   /*
   @PostMapping("/info")
    public String receiveInfo(Model viewmodel  , @RequestParam String info ){
        ColorsOut.outappl("RobotController | info:" + info + " robotName=" + robotName, ColorsOut.BLUE);
        //WebSocketConfiguration.wshandler.sendToAll("RobotController | doMove:" + move); //disappears
        try {
              //viewmodel.addAttribute("info", info);
        } catch (Exception e) {
            ColorsOut.outerr("RobotController | info ERROR:"+e.getMessage());
        }
        //return mainPage;
        return buildThePage(viewmodel);
    }
    */


    @ExceptionHandler
    public ResponseEntity handle(Exception ex) {
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity(
                "BaseController ERROR " + ex.getMessage(),
                responseHeaders, HttpStatus.CREATED);
    }
 
/*
 * curl --location --request POST 'http://localhost:8080/move' --header 'Content-Type: text/plain' --form 'move=l'	
 * curl -d move=r localhost:8080/move
 */
}

