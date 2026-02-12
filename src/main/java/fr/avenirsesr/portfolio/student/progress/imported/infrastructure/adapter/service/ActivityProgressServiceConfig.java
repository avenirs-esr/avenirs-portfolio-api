package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.ActivityProgressService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.ActivityProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.domain.service.ActivityProgressServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ActivityProgressServiceConfig {
    private final ActivityProgressRepository activityProgressRepository;
    private final ActivityRepository activityRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public ActivityProgressService activityProgressService() {
return new ActivityProgressServiceImpl(
            activityProgressRepository,
            activityRepository,
        loggedInUserService);
  }
}
