package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.user.domain.port.output.seeder.StaffDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.StaffFakerDataGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StaffDataGeneratorConfig {
  @Bean
  public StaffDataGenerator staffDataGenerator() {
    return new StaffFakerDataGenerator();
  }
}
