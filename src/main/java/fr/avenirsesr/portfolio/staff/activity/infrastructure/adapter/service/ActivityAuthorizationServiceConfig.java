package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.authorization.domain.port.output.EntityAuthorizationResolver;
import fr.avenirsesr.portfolio.authorization.domain.port.output.UserEntityAuthorizationReader;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityAuthorizationService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.service.ActivityAuthorizationServiceImpl;
import fr.avenirsesr.portfolio.staff.activity.domain.service.ActivityEntityAuthorizationResolver;
import fr.avenirsesr.portfolio.staff.activity.domain.service.ActivityUserEntityAuthorizationReader;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ActivityAuthorizationServiceConfig {
  private final ActivityAuthorizationRepository activityAuthorizationRepository;
  private final ActivityDraftAuthorizationRepository activityDraftAuthorizationRepository;
  private final ActivityRepository activityRepository;
  private final ActivityDraftRepository activityDraftRepository;
  private final StaffRepository staffRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public ActivityAuthorizationService activityAuthorizationService() {
    return new ActivityAuthorizationServiceImpl(
        activityAuthorizationRepository,
        activityDraftAuthorizationRepository,
        activityRepository,
        activityDraftRepository,
        staffRepository,
        loggedInUserService);
  }

  @Bean
  public EntityAuthorizationResolver activityEntityAuthorizationResolver(
      ActivityAuthorizationService activityAuthorizationService) {
    return new ActivityEntityAuthorizationResolver(
        EAuthorizationEntityType.ACTIVITY,
        activityAuthorizationService::getLoggedInStaffRightsByActivityIds,
        activityAuthorizationService::hasLoggedInStaffAnyRight);
  }

  @Bean
  public UserEntityAuthorizationReader activityUserEntityAuthorizationReader() {
    return new ActivityUserEntityAuthorizationReader(activityAuthorizationRepository);
  }

  @Bean
  public EntityAuthorizationResolver activityDraftEntityAuthorizationResolver(
      ActivityAuthorizationService activityAuthorizationService) {
    return new ActivityEntityAuthorizationResolver(
        EAuthorizationEntityType.ACTIVITY_DRAFT,
        activityAuthorizationService::getLoggedInStaffDraftRightsByDraftIds,
        activityAuthorizationService::hasLoggedInStaffAnyDraftRight);
  }
}
