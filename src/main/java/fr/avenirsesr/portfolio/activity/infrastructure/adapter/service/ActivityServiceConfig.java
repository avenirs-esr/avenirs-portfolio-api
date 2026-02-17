package fr.avenirsesr.portfolio.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.activity.domain.service.ActivityServiceImpl;
import fr.avenirsesr.portfolio.file.domain.port.input.ActivityResourceService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ActivityServiceConfig {
  private final ActivityRepository activityRepository;
  private final DeclaredActivityService declaredActivityService;
  private final LoggedInUserService loggedInUserService;
  private final ActivityResourceService activityResourceService;

  @Bean
  public ActivityService activityService() {
    return new ActivityServiceImpl(
        activityRepository, declaredActivityService, loggedInUserService, activityResourceService);
  }
}
