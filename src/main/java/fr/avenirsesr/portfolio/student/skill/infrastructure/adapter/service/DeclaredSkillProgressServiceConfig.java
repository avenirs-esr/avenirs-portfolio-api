package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.student.skill.domain.service.DeclaredSkillProgressServiceImpl;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.repository.DeclaredSkillProgressDatabaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeclaredSkillProgressServiceConfig {
  private final DeclaredSkillSyncService declaredSkillSyncService;
  private final DeclaredSkillProgressDatabaseRepository declaredSkillProgressRepository;
  private final ExternalSkillClient externalSkillClient;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;
  private final AssociationSearchHelper associationSearchHelper;

  @Bean
  public DeclaredSkillProgressService declaredSkillProgressService() {
    return new DeclaredSkillProgressServiceImpl(
        declaredSkillSyncService,
        declaredSkillProgressRepository,
        externalSkillClient,
        loggedInUserService,
        associationService,
        associationSearchHelper);
  }
}
