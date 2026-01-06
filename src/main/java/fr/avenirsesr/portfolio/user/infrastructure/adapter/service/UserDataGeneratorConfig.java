package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.user.domain.port.output.seeder.UserDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.UserFakerDataGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDataGeneratorConfig {
  @Bean
  public UserDataGenerator userDataGenerator() {
    return new UserFakerDataGenerator();
  }
}
