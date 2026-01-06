package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.declaredskill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.declaredskill.domain.port.output.repository.DeclaredSkillRepository;
import fr.avenirsesr.portfolio.declaredskill.domain.service.DeclaredSkillSyncServiceImpl;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.client.ExternalSkillClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeclaredSkillSyncServiceConfig {

  @Bean
  public DeclaredSkillSyncService declaredSkillSyncService(
      DeclaredSkillRepository declaredSkillRepository, ExternalSkillClient externalSkillClient) {
    return new DeclaredSkillSyncServiceImpl(declaredSkillRepository, externalSkillClient);
  }
}
