package fr.avenirsesr.portfolio.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.port.input.TraceConfigurationService;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.service.TraceServiceImpl;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository.TraceDatabaseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceServiceConfig {
  private final UuidGenerator uuidGenerator;
  private final TraceDatabaseRepository traceRepository;
  private final StudentProgressDatabaseRepository studentProgressRepository;
  private final TraceConfigurationService traceConfigurationService;

  public TraceServiceConfig(
      UuidGenerator uuidGenerator,
      TraceDatabaseRepository traceRepository,
      StudentProgressDatabaseRepository studentProgressRepository,
      TraceConfigurationService traceConfigurationService) {
    this.uuidGenerator = uuidGenerator;
    this.traceRepository = traceRepository;
    this.studentProgressRepository = studentProgressRepository;
    this.traceConfigurationService = traceConfigurationService;
  }

  @Bean
  public TraceService traceService() {
    return new TraceServiceImpl(
        uuidGenerator, traceRepository, studentProgressRepository, traceConfigurationService);
  }
}
