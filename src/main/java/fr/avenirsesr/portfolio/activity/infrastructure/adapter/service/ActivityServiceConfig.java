package fr.avenirsesr.portfolio.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.StaffActivityOverviewRepository;
import fr.avenirsesr.portfolio.activity.domain.service.ActivityServiceImpl;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ActivityServiceConfig {
  private final ActivityRepository activityRepository;
  private final ActivityDraftRepository activityDraftRepository;
  private final DeclaredActivityService declaredActivityService;
  private final LoggedInUserService loggedInUserService;
  private final StaffActivityOverviewRepository staffActivityOverviewRepository;
  private final NotificationService notificationService;

  @Bean
  public ActivityService activityService() {
    return new ActivityServiceImpl(
        activityRepository,
        activityDraftRepository,
        declaredActivityService,
        loggedInUserService,
        staffActivityOverviewRepository,
        notificationService);
  }
}
