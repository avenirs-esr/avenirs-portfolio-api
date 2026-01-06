package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.user.domain.port.output.seeder.StudentDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.StudentFakerDataGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StudentDataGeneratorConfig {
  @Bean
  public StudentDataGenerator studentDataGenerator() {
    return new StudentFakerDataGenerator();
  }
}
