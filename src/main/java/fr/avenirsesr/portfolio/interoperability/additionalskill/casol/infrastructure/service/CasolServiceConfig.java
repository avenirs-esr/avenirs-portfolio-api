package fr.avenirsesr.portfolio.interoperability.additionalskill.casol.infrastructure.service;

import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casol.domain.port.input.CasolService;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casol.domain.service.CasolServiceImpl;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casol.domain.service.CompetenceReader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CasolServiceConfig {
  private final AdditionalSkillRepository additionalSkillRepository;
  private final OpenSearchIndex openSearchIndex;
  private final CompetenceReader competenceReader;

  @Bean
  public CasolService casolService() {
    return new CasolServiceImpl(competenceReader, openSearchIndex, additionalSkillRepository);
  }
}
