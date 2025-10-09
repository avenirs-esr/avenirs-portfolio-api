package fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.infrastructure.service;

import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.domain.port.input.CasocService;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.domain.service.CasocServiceImpl;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.domain.service.CompetenceReader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CasocServiceConfig {
  private final AdditionalSkillRepository additionalSkillRepository;
  private final OpenSearchIndex openSearchIndex;
  private final CompetenceReader competenceReader;

  @Bean
  public CasocService casocService() {
    return new CasocServiceImpl(competenceReader, openSearchIndex, additionalSkillRepository);
  }
}
