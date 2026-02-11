package fr.avenirsesr.portfolio.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.activity.domain.service.ActivityServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ActivityServiceConfig {
  private final ActivityRepository activityRepository;

  @Bean
  public ActivityService activityService() {
    return new ActivityServiceImpl(activityRepository);
  }
}
