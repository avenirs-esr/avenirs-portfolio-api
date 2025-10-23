package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.input.RomeAdditionalSkillService;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.output.RomeAdditionalSkillApi;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.output.repository.Rome4VersionRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.service.RomeAdditionalSkillServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AdditionalSkillServiceConfig {
  private final AdditionalSkillRepository additionalSkillRepository;
  private final Rome4VersionRepository rome4VersionRepository;
  private final RomeAdditionalSkillApi romeAdditionalSkillApi;
  private final OpenSearchIndex openSearchIndex;

  public AdditionalSkillServiceConfig(
      AdditionalSkillRepository additionalSkillRepository,
      Rome4VersionRepository rome4VersionRepository,
      RomeAdditionalSkillApi romeAdditionalSkillApi,
      OpenSearchIndex openSearchIndex) {
    this.additionalSkillRepository = additionalSkillRepository;
    this.rome4VersionRepository = rome4VersionRepository;
    this.romeAdditionalSkillApi = romeAdditionalSkillApi;
    this.openSearchIndex = openSearchIndex;
  }

  @Bean
  public RomeAdditionalSkillService romeAdditionalSkillService() {
    return new RomeAdditionalSkillServiceImpl(
        additionalSkillRepository, rome4VersionRepository, romeAdditionalSkillApi, openSearchIndex);
  }
}
