package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.ESeederSource;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.UserDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.UserCSVDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.UserFakerDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDataGeneratorConfig {

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Bean
  public UserDataGenerator userDataGenerator() {
    return switch (seederSource) {
      case CSV -> new UserCSVDataGenerator();
      case FAKER -> new UserFakerDataGenerator();
    };
  }
}
