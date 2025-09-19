package fr.avenirsesr.portfolio.ams.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.ams.domain.port.output.seeder.AmsDataGenerator;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.data.AmsCSVDataGenerator;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.data.AmsFakerDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmsDataGeneratorConfig {
  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Bean
  public AmsDataGenerator amsDataGenerator() {
    return switch (seederSource) {
      case CSV -> new AmsCSVDataGenerator();
      case FAKER -> new AmsFakerDataGenerator();
    };
  }
}
