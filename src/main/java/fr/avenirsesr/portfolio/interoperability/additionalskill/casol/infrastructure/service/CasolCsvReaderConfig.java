package fr.avenirsesr.portfolio.interoperability.additionalskill.casol.infrastructure.service;

import fr.avenirsesr.portfolio.interoperability.additionalskill.casol.domain.service.CompetenceReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CasolCsvReaderConfig {
  @Bean
  public CompetenceReader casolCompetenceReader() {
    return new CsvCompetenceReader();
  }
}
