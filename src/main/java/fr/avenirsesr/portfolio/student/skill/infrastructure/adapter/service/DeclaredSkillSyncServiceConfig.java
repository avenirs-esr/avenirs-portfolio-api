package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.repository.DeclaredSkillRepository;
import fr.avenirsesr.portfolio.student.skill.domain.service.DeclaredSkillSyncServiceImpl;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.client.ExternalSkillClient;
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
