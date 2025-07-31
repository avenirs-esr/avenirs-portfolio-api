package fr.avenirsesr.portfolio.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.configuration.domain.port.input.ConfigurationService;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.service.TraceServiceImpl;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository.TraceDatabaseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceServiceConfig {
  private final TraceDatabaseRepository traceRepository;
  private final StudentProgressDatabaseRepository studentProgressRepository;
  private final ConfigurationService configurationService;

  public TraceServiceConfig(
      TraceDatabaseRepository traceRepository,
      StudentProgressDatabaseRepository studentProgressRepository,
      ConfigurationService configurationService) {
    this.traceRepository = traceRepository;
    this.studentProgressRepository = studentProgressRepository;
    this.configurationService = configurationService;
  }

  @Bean
  public TraceService traceService() {
    return new TraceServiceImpl(traceRepository, studentProgressRepository, configurationService);
  }
}
