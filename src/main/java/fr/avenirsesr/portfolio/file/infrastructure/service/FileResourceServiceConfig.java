package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.FileResourceServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class FileResourceServiceConfig {
  private final FileStorageService fileStorageService;
  private final FileRepository fileRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  @Primary
  public FileResourceService fileResourceService() {
    return new FileResourceServiceImpl(fileStorageService, fileRepository, loggedInUserService);
  }
}
