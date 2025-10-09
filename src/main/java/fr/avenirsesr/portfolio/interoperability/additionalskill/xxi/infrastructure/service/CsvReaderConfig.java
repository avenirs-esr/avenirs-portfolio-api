package fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.infrastructure.service;

import fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.service.CompetenceReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsvReaderConfig {
  @Bean
  public CompetenceReader competenceReader() {
    return new CsvCompetenceReader();
  }
}
