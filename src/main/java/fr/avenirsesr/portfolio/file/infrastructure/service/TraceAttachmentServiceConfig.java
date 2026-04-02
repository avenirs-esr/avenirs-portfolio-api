package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.file.domain.port.input.TraceAttachmentService;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.TraceAttachmentServiceImpl;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.repository.TraceAttachmentDatabaseRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class TraceAttachmentServiceConfig {
  private final TraceAttachmentDatabaseRepository traceAttachmentRepository;
  private final FileStorageService fileStorageService;
  private final LoggedInUserService loggedInUserService;

  @Bean
  @Primary
  public TraceAttachmentService traceAttachmentService(@Lazy TraceService traceService) {
    return new TraceAttachmentServiceImpl(
        traceAttachmentRepository, traceService, fileStorageService, loggedInUserService);
  }
}
