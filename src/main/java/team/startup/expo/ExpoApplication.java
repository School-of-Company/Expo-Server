package team.startup.expo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ExpoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpoApplication.class, args);
	}

}
