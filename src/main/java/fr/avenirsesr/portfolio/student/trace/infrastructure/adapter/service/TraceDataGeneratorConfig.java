package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.student.trace.domain.port.output.seeder.TraceDataGenerator;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.seeder.data.TraceFakerDataGenerator;
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
      case FAKER, CSV -> new TraceFakerDataGenerator();
    };
  }
}
