package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.declaredskill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.skill.domain.service.DeclaredSkillProgressServiceImpl;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.repository.DeclaredSkillProgressDatabaseRepository;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

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
  public DeclaredSkillProgressService declaredSkillProgressService(
      @Lazy TraceService traceService,
      @Lazy DeclaredActivityService declaredActivityService,
      @Lazy DeclaredExperienceService declaredExperienceService) {
    return new DeclaredSkillProgressServiceImpl(
        traceService,
        declaredSkillSyncService,
        declaredSkillProgressRepository,
        externalSkillClient,
        loggedInUserService,
        declaredActivityService,
        associationService,
        associationSearchHelper,
        declaredExperienceService);
  }
}
