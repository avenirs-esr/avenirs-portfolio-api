package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.file.domain.port.input.TraceAttachmentService;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.TraceAttachmentServiceImpl;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.repository.TraceAttachmentDatabaseRepository;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository.TraceDatabaseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceAttachmentServiceConfig {
  private final TraceAttachmentDatabaseRepository traceAttachmentRepository;
  private final TraceDatabaseRepository traceRepository;
  private final FileStorageService fileStorageService;
  private final TraceService traceService;

  public TraceAttachmentServiceConfig(
      TraceAttachmentDatabaseRepository traceAttachmentRepository,
      TraceDatabaseRepository traceRepository,
      FileStorageService fileStorageService,
      TraceService traceService) {
    this.traceAttachmentRepository = traceAttachmentRepository;
    this.traceRepository = traceRepository;
    this.fileStorageService = fileStorageService;
    this.traceService = traceService;
  }

  @Bean
  public TraceAttachmentService traceAttachmentService() {
    return new TraceAttachmentServiceImpl(
        traceAttachmentRepository, traceRepository, fileStorageService, traceService);
  }
}
