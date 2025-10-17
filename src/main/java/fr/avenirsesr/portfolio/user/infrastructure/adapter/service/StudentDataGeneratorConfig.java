package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.StudentDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.StudentCSVDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.StudentFakerDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StudentDataGeneratorConfig {
  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Bean
  public StudentDataGenerator studentDataGenerator() {
    return switch (seederSource) {
      case CSV -> new StudentCSVDataGenerator();
      case FAKER -> new StudentFakerDataGenerator();
    };
  }
}
