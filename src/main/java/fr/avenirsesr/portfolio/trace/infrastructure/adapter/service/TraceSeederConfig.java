package fr.avenirsesr.portfolio.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.port.input.TraceAttachmentService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.TraceAttachmentServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceSeederConfig {
  @Bean
  @Qualifier("MockTraceAttachmentService")
  public TraceAttachmentService MockTraceAttachmentService(
      @Qualifier("seederFileStorageService") FileStorageService fileStorageService,
      TraceAttachmentRepository traceAttachmentRepository,
      TraceService traceService,
      LoggedInUserService loggedInUserService) {
    return new TraceAttachmentServiceImpl(
        traceAttachmentRepository, traceService, fileStorageService, loggedInUserService);
  }
}
