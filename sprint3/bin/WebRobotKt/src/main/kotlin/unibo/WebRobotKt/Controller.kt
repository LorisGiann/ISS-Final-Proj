package unibo.WebRobotKt

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class Controller {
    protected var mainPage = "consoleGui"

    @GetMapping("/")
    fun entry(viewmodel: Model): String {
        return mainPage
    }


}
