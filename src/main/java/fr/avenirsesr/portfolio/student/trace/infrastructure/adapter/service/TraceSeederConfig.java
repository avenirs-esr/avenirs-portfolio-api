package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.common.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.common.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.common.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.common.file.domain.port.output.service.FileUploaderProvider;
import fr.avenirsesr.portfolio.common.file.domain.service.FileResourceServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceSeederConfig {
  @Bean
  @Qualifier("MockFileResourceService")
  public FileResourceService MockFileResourceService(
      @Qualifier("seederFileStorageService") FileStorageService fileStorageService,
      FileRepository fileRepository,
      FileUploaderProvider fileUploaderProvider) {
    return new FileResourceServiceImpl(fileStorageService, fileRepository, fileUploaderProvider);
  }
}
