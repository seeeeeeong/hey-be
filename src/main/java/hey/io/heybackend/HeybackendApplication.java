package hey.io.heybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableCaching
@SpringBootApplication(
		scanBasePackages = {"hey.io.heybackend"})
public class HeybackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeybackendApplication.class, args);
	}

}
