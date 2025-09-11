package fr.avenirsesr.portfolio.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.ESeederSource;
import fr.avenirsesr.portfolio.trace.domain.port.output.seeder.TraceDataGenerator;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.data.TraceCSVDataGenerator;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.data.TraceFakerDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceDataGeneratorConfig {
  @Value("${seeder.source:false}")
  private ESeederSource seederSource;

  @Bean
  public TraceDataGenerator traceDataGenerator() {
    return switch (seederSource) {
      case FAKER -> new TraceFakerDataGenerator();
      case CSV -> new TraceCSVDataGenerator();
    };
  }
}
