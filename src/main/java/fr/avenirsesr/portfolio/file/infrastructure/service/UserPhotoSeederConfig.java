package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.file.domain.port.input.UserResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.UserResourceServiceImpl;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.service.FileStorageServiceMock;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserPhotoSeederConfig {

  @Bean
  @Qualifier("seederFileStorageService")
  public FileStorageService seederFileStorageService() {
    return new FileStorageServiceMock();
  }

  @Bean
  @Qualifier("MockUserResourceService")
  public UserResourceService MockUserResourceService(
      @Qualifier("seederFileStorageService") FileStorageService fileStorageService,
      UserPhotoRepository userPhotoRepository,
      LoggedInUserService loggedInUserService) {
    return new UserResourceServiceImpl(
        fileStorageService, userPhotoRepository, loggedInUserService);
  }
}
