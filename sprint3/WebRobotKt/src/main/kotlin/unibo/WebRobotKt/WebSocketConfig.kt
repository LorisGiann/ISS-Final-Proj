package unibo.WebRobotKt

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    var wshandler = WebSocketHandler()

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(wshandler, "/socket").setAllowedOrigins("*")
    }

}