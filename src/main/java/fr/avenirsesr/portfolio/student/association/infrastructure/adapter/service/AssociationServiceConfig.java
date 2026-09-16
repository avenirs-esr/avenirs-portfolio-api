package fr.avenirsesr.portfolio.student.association.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociatedElementsService;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociatedElementsServiceImpl;
import fr.avenirsesr.portfolio.student.association.domain.service.AssociationSearchHelper;
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

  @Bean
  public AssociationService AssociationService() {
    return new AssociationServiceImpl(associationRepository);
  }

  @Bean
  public AssociationSearchHelper associationSearchHelper(AssociationService associationService) {
    return new AssociationSearchHelper(associationService);
  }

  @Bean
  public AssociatedElementsService associatedElementsService(
      AssociationService associationService,
      @Lazy TraceService traceService,
      @Lazy DeclaredActivityService declaredActivityService,
      @Lazy DeclaredSkillProgressService declaredSkillProgressService,
      @Lazy DeclaredExperienceService declaredExperienceService) {
    return new AssociatedElementsServiceImpl(
        associationService,
        traceService,
        declaredActivityService,
        declaredSkillProgressService,
        declaredExperienceService);
  }
}
