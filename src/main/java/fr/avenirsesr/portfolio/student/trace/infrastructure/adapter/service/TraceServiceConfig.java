package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.port.output.strategy.AssociationStrategy;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.student.trace.domain.service.TraceAssociationStrategy;
import fr.avenirsesr.portfolio.student.trace.domain.service.TraceServiceImpl;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.client.TraceConfigurationClient;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.repository.TraceDatabaseRepository;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TraceServiceConfig {
  private final TraceDatabaseRepository traceRepository;
  private final StudentService studentService;
  private final DeclaredActivityService declaredActivityService;
  private final TraceConfigurationClient traceConfigurationClient;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;
  private final FeedbackService feedbackService;
  private final FileResourceService fileResourceService;

  @Bean
  public TraceService traceService() {
    return new TraceServiceImpl(
        traceRepository,
        studentService,
        declaredActivityService,
        traceConfigurationClient,
        loggedInUserService,
        associationService,
        feedbackService,
        fileResourceService);
  }

  @Bean
  public AssociationStrategy traceAssociationStrategy() {
    return new TraceAssociationStrategy(traceService(), loggedInUserService);
  }
}
