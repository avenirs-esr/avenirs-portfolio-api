package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service.DeclaredActivityServiceImpl;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeclaredActivityServiceConfig {
  private final DeclaredActivityRepository declaredActivityRepository;
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;

  @Bean
  public DeclaredActivityService declaredActivityService(
      @Lazy ActivityService activityService, @Lazy TraceService traceService) {
    return new DeclaredActivityServiceImpl(
        declaredActivityRepository,
        activityService,
        traceService,
        declaredSkillProgressService,
        associationService,
        loggedInUserService);
  }
}
