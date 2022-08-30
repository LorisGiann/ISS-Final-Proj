package unibo.webRobot;

import Robots.common.RobotUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebRobotApplication {

	public static void main(String[] args) {
		RobotUtils.connectUsingCoap("localhost");
		SpringApplication.run(WebRobotApplication.class, args);
	}

}
