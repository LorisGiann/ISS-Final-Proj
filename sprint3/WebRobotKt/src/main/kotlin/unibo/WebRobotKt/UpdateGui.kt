package unibo.WebRobotKt

import org.springframework.stereotype.Controller
import java.io.Serializable

@Controller
class UpdateGui : Serializable {
    var statett: String
        set(value){
            field=value
        }
    var stateled: String
        set(value){
            field=value
        }
    var position: String
        set(value){
            field=value
        }
    var pb: String
        set(value){
            field=value
        }
    var gb: String
        set(value){
            field=value
        }

    constructor() {
        statett = "coap connecting"
        stateled = "coap connecting"
        position = "coap connecting"
        pb = "coap connecting"
        gb = "coap connecting"
    }

    /*constructor() {
        statett = "load"
        stateled = "OFF"
        position = "HOME"
        pb = "10"
        gb = "10"
    }*/

    override fun toString(): String {
        return "{\"statett\":\"$statett\",\"stateled\":\"$stateled\",\"position\":\"$position\",\"pb\":\"$pb\",\"gb\":\"$gb\"}"
    }
}