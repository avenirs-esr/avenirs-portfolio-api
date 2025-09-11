package fr.avenirsesr.portfolio.ams.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.ams.domain.port.output.seeder.CohortDataGenerator;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.data.CohortCSVDataGenerator;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.data.CohortFakerDataGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.ESeederSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CohortDataGeneratorConfig {
  @Value("${seeder.source:false}")
  private ESeederSource seederSource;

  @Bean
  public CohortDataGenerator cohortDataGenerator() {
    return switch (seederSource) {
      case CSV -> new CohortCSVDataGenerator();
      case FAKER -> new CohortFakerDataGenerator();
    };
  }
}
