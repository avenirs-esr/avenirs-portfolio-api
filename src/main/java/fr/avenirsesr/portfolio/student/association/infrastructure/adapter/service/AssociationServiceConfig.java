package fr.avenirsesr.portfolio.student.association.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationServiceImpl;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@RequiredArgsConstructor
public class AssociationServiceConfig {
  private final AssociationRepository associationRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public AssociationService AssociationService(
      @Lazy TraceService traceService,
      @Lazy DeclaredActivityService declaredActivityService,
      @Lazy DeclaredSkillProgressService declaredSkillProgressService,
      @Lazy DeclaredExperienceService declaredExperienceService) {
    return new AssociationServiceImpl(
        associationRepository,
        loggedInUserService,
        traceService,
        declaredActivityService,
        declaredSkillProgressService,
        declaredExperienceService);
  }
}
