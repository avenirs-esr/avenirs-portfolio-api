package fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.infrastructure.service;

import fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.domain.service.CompetenceReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CasocCsvReaderConfig {
  @Bean
  public CompetenceReader casocCompetenceReader() {
    return new CsvCompetenceReader();
  }
}
