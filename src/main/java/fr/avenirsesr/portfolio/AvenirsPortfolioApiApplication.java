package fr.avenirsesr.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AvenirsPortfolioApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(AvenirsPortfolioApiApplication.class, args);
  }
}
