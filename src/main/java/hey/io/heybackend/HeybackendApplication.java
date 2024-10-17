package hey.io.heybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HeybackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeybackendApplication.class, args);
	}

}
