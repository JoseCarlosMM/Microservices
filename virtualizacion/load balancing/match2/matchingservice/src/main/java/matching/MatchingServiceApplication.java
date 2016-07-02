package matching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableDiscoveryClient
public class MatchingServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MatchingServiceApplication.class, args);
		ConsumerC consumerC = new ConsumerC();
		consumerC.logear("esta es una prueba");
	}
}
