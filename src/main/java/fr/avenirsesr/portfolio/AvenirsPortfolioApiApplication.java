package fr.avenirsesr.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(
    scanBasePackages = {"fr.avenirsesr.portfolio", "fr.avenirsesr.portfolio.common"})
public class AvenirsPortfolioApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(AvenirsPortfolioApiApplication.class, args);
  }
}
