package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillSyncService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.domain.service.AdditionalSkillSyncServiceImpl;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.client.ExternalSkillClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdditionalSkillSyncServiceConfig {

  @Bean
  public AdditionalSkillSyncService additionalSkillSyncService(
      AdditionalSkillRepository additionalSkillRepository,
      ExternalSkillClient externalSkillClient) {
    return new AdditionalSkillSyncServiceImpl(additionalSkillRepository, externalSkillClient);
  }
}
