package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.file.domain.port.input.ActivityResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.ActivityBannerRepository;
import fr.avenirsesr.portfolio.file.domain.service.ActivityResourceServiceImpl;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.service.FileStorageServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ActivityResourceServiceConfig {
  private final FileStorageServiceImpl fileStorageService;
  private final ActivityBannerRepository activityBannerRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public ActivityResourceService activityResourceService() {
    return new ActivityResourceServiceImpl(
        fileStorageService, loggedInUserService, activityBannerRepository);
  }
}
