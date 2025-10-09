package fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.infrastructure.service;

import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.port.input.XXIService;
import fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.service.CompetenceReader;
import fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.service.XXIServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class XXIServiceConfig {
  private final AdditionalSkillRepository additionalSkillRepository;
  private final OpenSearchIndex openSearchIndex;
  private final CompetenceReader competenceReader;

  @Bean
  public XXIService xxiServiceConfig() {
    return new XXIServiceImpl(competenceReader, openSearchIndex, additionalSkillRepository);
  }
}
