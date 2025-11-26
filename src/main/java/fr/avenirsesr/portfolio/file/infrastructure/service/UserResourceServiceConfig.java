package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.file.domain.service.UserResourceServiceImpl;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.service.FileStorageServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserResourceServiceConfig {
  private final FileStorageServiceImpl fileStorageService;
  private final UserPhotoRepository userPhotoRepository;
  private final LoggedInUserService loggedInUserService;

  public UserResourceServiceConfig(
      FileStorageServiceImpl fileStorageService,
      UserPhotoRepository userPhotoRepository,
      LoggedInUserService loggedInUserService) {
    this.fileStorageService = fileStorageService;
    this.userPhotoRepository = userPhotoRepository;
    this.loggedInUserService = loggedInUserService;
  }

  @Bean
  public UserResourceServiceImpl userResourceService() {
    return new UserResourceServiceImpl(
        fileStorageService, userPhotoRepository, loggedInUserService);
  }
}
