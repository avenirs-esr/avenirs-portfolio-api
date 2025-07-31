package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.domain.service.AdditionalSkillServiceImpl;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository.AdditionalSkillDatabaseProgressRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AdditionalSkillServiceConfig {
  private final AdditionalSkillCache additionalSkillCache;
  private final AdditionalSkillDatabaseProgressRepository additionalSkillProgressRepository;

  public AdditionalSkillServiceConfig(
      AdditionalSkillCache additionalSkillCache,
      AdditionalSkillDatabaseProgressRepository additionalSkillProgressRepository) {
    this.additionalSkillCache = additionalSkillCache;
    this.additionalSkillProgressRepository = additionalSkillProgressRepository;
  }

  @Bean
  public AdditionalSkillService additionalSkillService() {
    return new AdditionalSkillServiceImpl(additionalSkillCache, additionalSkillProgressRepository);
  }
}
