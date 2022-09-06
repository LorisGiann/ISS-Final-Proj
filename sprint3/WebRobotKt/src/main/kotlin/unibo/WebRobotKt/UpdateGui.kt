package unibo.WebRobotKt

import org.springframework.stereotype.Controller
import java.io.Serializable

@Controller
class UpdateGui : Serializable {
    var statett: String
        set(value) {         // setter
            field = value
        }
    var stateled: String
        set(value) {         // setter
            field = value
        }
    var position: String
        set(value) {         // setter
            field = value
        }
    var pb: String
        set(value) {         // setter
            field = value
        }
    var gb: String
        set(value) {         // setter
            field = value
        }

    constructor() {
        statett = "WAIT"
        stateled = "OFF"
        position = "HOME"
        pb = "10"
        gb = "10"
    }

    constructor(statett: String, stateled: String, position: String, pb: String, gb: String) {
        this.statett = statett
        this.stateled = stateled
        this.position = position
        this.pb = pb
        this.gb = gb
    }

    override fun toString(): String {
        return "{\"statett\":\"$statett\",\"stateled\":\"$stateled\",\"position\":\"$position\",\"pb\":\"$pb\",\"gb\":\"$gb\"}"
    }
}