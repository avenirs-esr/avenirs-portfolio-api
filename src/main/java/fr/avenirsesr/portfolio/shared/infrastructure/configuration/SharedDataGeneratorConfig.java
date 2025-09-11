package fr.avenirsesr.portfolio.shared.infrastructure.configuration;

import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.SharedDataGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.ESeederSource;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.SharedFakerDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedDataGeneratorConfig {
  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Bean
  public SharedDataGenerator sharedDataGenerator() {
    return switch (seederSource) {
      case FAKER, CSV -> new SharedFakerDataGenerator();
    };
  }
}
