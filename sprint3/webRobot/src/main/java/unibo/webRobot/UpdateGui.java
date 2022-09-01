package unibo.webRobot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.Serializable;

@Controller
public class UpdateGui implements Serializable {
    private String statett;
    private String stateled;
    private String position;
    private String pb;
    private String gb;

    public UpdateGui(){
        this.statett="";
        this.stateled="";
        this.position="";
        this.pb="";
        this.gb="";
    }

    public UpdateGui(String statett,String stateled,String position,String pb,String gb) {
        this.statett=statett;
        this.stateled=stateled;
        this.position=position;
        this.pb=pb;
        this.gb=gb;
    }

    public String getStatett() {
        return statett;
    }

    public String getStateled() {
        return stateled;
    }

    public String getPosition() {
        return position;
    }

    public String getPb() {
        return pb;
    }

    public String getGb() {
        return gb;
    }

    @Override
    public String toString(){
        return "{\"statett\":\""+statett+"\",\"stateled\":\""+stateled+"\",\"position\":\""+position+"\",\"pb\":\""+pb+"\",\"gb\":\""+gb+"\"}";
    }
}
