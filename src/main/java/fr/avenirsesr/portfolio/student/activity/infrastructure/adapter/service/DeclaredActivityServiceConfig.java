package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.FeedbackRepository;
import fr.avenirsesr.portfolio.student.activity.domain.service.DeclaredActivityAssociationStrategy;
import fr.avenirsesr.portfolio.student.activity.domain.service.DeclaredActivityServiceImpl;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.port.output.strategy.AssociationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeclaredActivityServiceConfig {
  private final DeclaredActivityRepository declaredActivityRepository;
  private final LoggedInUserService loggedInUserService;
  private final AssociationService associationService;
  private final FeedbackRepository feedbackRepository;

  @Bean
  public DeclaredActivityService declaredActivityService(
      @Lazy ActivityService activityService,
      @Lazy FeedbackService feedbackService,
      @Lazy NotificationService notificationService) {
    return new TransactionalDeclaredActivityService(
        new DeclaredActivityServiceImpl(
            declaredActivityRepository,
            activityService,
            associationService,
            loggedInUserService,
            feedbackRepository,
            feedbackService,
            notificationService));
  }

  @Bean
  public AssociationStrategy declaredActivityAssociationStrategy(
      DeclaredActivityService declaredActivityService) {
    return new DeclaredActivityAssociationStrategy(
        declaredActivityService, associationService, loggedInUserService);
  }
}
