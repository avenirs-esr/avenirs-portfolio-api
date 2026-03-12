package fr.avenirsesr.portfolio.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.service.TraceServiceImpl;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository.TraceDatabaseRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TraceServiceConfig {
  private final TraceDatabaseRepository traceRepository;
  private final UserRepository userRepository;
  private final StudentProgressDatabaseRepository studentProgressRepository;
  private final DeclaredActivityRepository declaredActivityRepository;
  private final TraceConfigurationClient traceConfigurationClient;
  private final TraceAttachmentRepository traceAttachmentRepository;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;

  @Bean
  public TraceService traceService() {
    return new TraceServiceImpl(
        traceRepository,
        userRepository,
        studentProgressRepository,
        traceAttachmentRepository,
        declaredActivityRepository,
        traceConfigurationClient,
        loggedInUserService,
        associationService);
  }
}
