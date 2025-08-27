package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.file.domain.service.UserResourceServiceImpl;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.service.FileStorageServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserResourceServiceConfig {
  private final UuidGenerator uuidGenerator;
  private final FileStorageServiceImpl fileStorageService;
  private final UserPhotoRepository userPhotoRepository;

  public UserResourceServiceConfig(
      UuidGenerator uuidGenerator,
      FileStorageServiceImpl fileStorageService,
      UserPhotoRepository userPhotoRepository) {
    this.uuidGenerator = uuidGenerator;
    this.fileStorageService = fileStorageService;
    this.userPhotoRepository = userPhotoRepository;
  }

  @Bean
  public UserResourceServiceImpl userResourceService() {
    return new UserResourceServiceImpl(uuidGenerator, fileStorageService, userPhotoRepository);
  }
}
