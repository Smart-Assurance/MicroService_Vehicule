package ma.fstt.microservicevehicule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka

@SpringBootApplication
public class MicroserviceVehiculeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceVehiculeApplication.class, args);
	}

}
