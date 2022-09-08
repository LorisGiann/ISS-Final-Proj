package unibo.WebRobotKt

import org.springframework.stereotype.Controller
import java.io.Serializable

@Controller
class UpdateGui : Serializable {
    var statett: String
        @Synchronized set
    var stateled: String
        @Synchronized set
    var position: String
        @Synchronized set
    var pb: String
        @Synchronized set
    var gb: String
        @Synchronized set

    constructor() {
        statett = "WAIT"
        stateled = "OFF"
        position = "HOME"
        pb = "10"
        gb = "10"
    }

    override fun toString(): String {
        return "{\"statett\":\"$statett\",\"stateled\":\"$stateled\",\"position\":\"$position\",\"pb\":\"$pb\",\"gb\":\"$gb\"}"
    }
}