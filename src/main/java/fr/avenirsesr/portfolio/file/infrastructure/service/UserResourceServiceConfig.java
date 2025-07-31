package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.file.domain.service.UserResourceServiceImpl;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.service.FileStorageServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserResourceServiceConfig {
  private final FileStorageServiceImpl fileStorageService;
  private final UserPhotoRepository userPhotoRepository;

  public UserResourceServiceConfig(
      FileStorageServiceImpl fileStorageService, UserPhotoRepository userPhotoRepository) {
    this.fileStorageService = fileStorageService;
    this.userPhotoRepository = userPhotoRepository;
  }

  @Bean
  public UserResourceServiceImpl userResourceService() {
    return new UserResourceServiceImpl(fileStorageService, userPhotoRepository);
  }
}
