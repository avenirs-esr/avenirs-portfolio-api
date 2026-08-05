package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.FileResourceServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
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
      LoggedInUserService loggedInUserService) {
    return new FileResourceServiceImpl(fileStorageService, fileRepository, loggedInUserService);
  }
}
