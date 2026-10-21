package fr.avenirsesr.portfolio.skill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.skill.domain.port.input.SkillSyncService;
import fr.avenirsesr.portfolio.skill.domain.port.output.client.SkillCatalogClient;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.NationalSkillDetailsRepository;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillReferentialRepository;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.skill.domain.service.SkillSyncServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkillSyncServiceConfig {

  @Bean
  public SkillSyncService skillSyncService(
      SkillCatalogClient skillCatalogClient,
      SkillRepository skillRepository,
      SkillReferentialRepository skillReferentialRepository,
      NationalSkillDetailsRepository nationalSkillDetailsRepository) {
    return new TransactionalSkillSyncService(
        new SkillSyncServiceImpl(
            skillCatalogClient,
            skillRepository,
            skillReferentialRepository,
            nationalSkillDetailsRepository));
  }
}
