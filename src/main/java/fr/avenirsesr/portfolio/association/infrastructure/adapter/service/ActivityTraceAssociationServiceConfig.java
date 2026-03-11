package fr.avenirsesr.portfolio.association.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.association.domain.port.input.ActivityTraceAssociationService;
import fr.avenirsesr.portfolio.association.domain.port.output.repository.ActivityTraceAssociationRepository;
import fr.avenirsesr.portfolio.association.domain.service.ActivityTraceAssociationServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ActivityTraceAssociationServiceConfig {
  private final LoggedInUserService loggedInUserService;
  private final ActivityTraceAssociationRepository activityTraceAssociationRepository;
  private final TraceRepository traceRepository;
  private final DeclaredActivityRepository declaredActivityRepository;

  @Bean
  public ActivityTraceAssociationService activityTraceAssociationService() {
    return new ActivityTraceAssociationServiceImpl(
        loggedInUserService,
        activityTraceAssociationRepository,
        traceRepository,
        declaredActivityRepository);
  }
}
