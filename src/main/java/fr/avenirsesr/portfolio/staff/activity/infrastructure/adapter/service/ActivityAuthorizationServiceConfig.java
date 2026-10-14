package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityAuthorizationService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.service.ActivityAuthorizationServiceImpl;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ActivityAuthorizationServiceConfig {
  private final ActivityAuthorizationRepository activityAuthorizationRepository;
  private final ActivityRepository activityRepository;
  private final ActivityDraftRepository activityDraftRepository;
  private final StaffRepository staffRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public ActivityAuthorizationService activityAuthorizationService() {
    return new ActivityAuthorizationServiceImpl(
        activityAuthorizationRepository,
        activityRepository,
        activityDraftRepository,
        staffRepository,
        loggedInUserService);
  }
}
