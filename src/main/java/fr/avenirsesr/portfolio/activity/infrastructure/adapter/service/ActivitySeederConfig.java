package fr.avenirsesr.portfolio.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.port.input.ActivityResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.ActivityBannerRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.domain.service.ActivityResourceServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivitySeederConfig {

  @Bean
  @Qualifier("MockActivityResourceService")
  public ActivityResourceService MockActivityResourceService(
      @Qualifier("seederFileStorageService") FileStorageService fileStorageService,
      ActivityBannerRepository activityBannerRepository,
      LoggedInUserService loggedInUserService) {
    return new ActivityResourceServiceImpl(
        fileStorageService, loggedInUserService, activityBannerRepository);
  }
}
