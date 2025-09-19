package fr.avenirsesr.portfolio.program.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.program.domain.port.output.seeder.ProgramDataGenerator;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.data.ProgramCSVDataGenerator;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.data.ProgramFakerDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProgramDataGeneratorConfig {

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Bean
  public ProgramDataGenerator programDataGenerator() {
    return switch (seederSource) {
      case CSV -> new ProgramCSVDataGenerator();
      case FAKER -> new ProgramFakerDataGenerator();
    };
  }
}
