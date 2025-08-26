package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.RomeAdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.RomeAdditionalSkillApi;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.Rome4VersionRepository;
import fr.avenirsesr.portfolio.additionalskill.domain.service.AdditionalSkillServiceImpl;
import fr.avenirsesr.portfolio.additionalskill.domain.service.RomeAdditionalSkillServiceImpl;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository.AdditionalSkillDatabaseProgressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AdditionalSkillServiceConfig {
  private final AdditionalSkillCache additionalSkillCache;
  private final AdditionalSkillDatabaseProgressRepository additionalSkillProgressRepository;
  private final AdditionalSkillRepository additionalSkillRepository;
  private final Rome4VersionRepository rome4VersionRepository;
  private final RomeAdditionalSkillApi romeAdditionalSkillApi;
  private final OpenSearchIndex openSearchIndex;

  public AdditionalSkillServiceConfig(
      AdditionalSkillCache additionalSkillCache,
      AdditionalSkillDatabaseProgressRepository additionalSkillProgressRepository,
      AdditionalSkillRepository additionalSkillRepository,
      Rome4VersionRepository rome4VersionRepository,
      RomeAdditionalSkillApi romeAdditionalSkillApi,
      OpenSearchIndex openSearchIndex) {
    this.additionalSkillCache = additionalSkillCache;
    this.additionalSkillProgressRepository = additionalSkillProgressRepository;
    this.additionalSkillRepository = additionalSkillRepository;
    this.rome4VersionRepository = rome4VersionRepository;
    this.romeAdditionalSkillApi = romeAdditionalSkillApi;
    this.openSearchIndex = openSearchIndex;
  }

  @Bean
  public AdditionalSkillService additionalSkillService() {
    return new AdditionalSkillServiceImpl(additionalSkillCache, additionalSkillProgressRepository);
  }

  @Bean
  public RomeAdditionalSkillService romeAdditionalSkillService() {
    return new RomeAdditionalSkillServiceImpl(
        additionalSkillRepository, rome4VersionRepository, romeAdditionalSkillApi, openSearchIndex);
  }
}
