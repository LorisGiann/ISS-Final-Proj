package unibo.webRobot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UpdateGui {
    private String type;
    private String content;

    public UpdateGui(){
        
    }

    public UpdateGui(String type,String content) {
        this.type = type;
        this.content = content;
    }

    public String getType(){
        return type;
    }

    public String getContent() {
        return content;
    }
}
