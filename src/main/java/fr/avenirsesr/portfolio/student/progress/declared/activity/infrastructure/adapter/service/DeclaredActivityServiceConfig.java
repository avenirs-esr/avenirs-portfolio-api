package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service.DeclaredActivityServiceImpl;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeclaredActivityServiceConfig {
  private final DeclaredActivityRepository declaredActivityRepository;
  private final ActivityRepository activityRepository;
  private final TraceRepository traceRepository;
  private final LoggedInUserService loggedInUserService;
  private final TraceService traceService;
  private final AssociationService associationService;

  @Bean
  public DeclaredActivityService declaredActivityService() {
    return new DeclaredActivityServiceImpl(
        declaredActivityRepository,
        activityRepository,
        traceRepository,
        associationService,
        traceService,
        loggedInUserService);
  }
}
