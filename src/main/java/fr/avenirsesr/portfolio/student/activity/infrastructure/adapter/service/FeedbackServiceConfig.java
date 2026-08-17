package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.FeedbackRepository;
import fr.avenirsesr.portfolio.student.activity.domain.service.FeedbackServiceImpl;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeedbackServiceConfig {
  private final FeedbackRepository feedbackRepository;
  private final DeclaredActivityRepository declaredActivityRepository;
  private final AssociationService associationService;
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final LoggedInUserService loggedInUserService;
  private final NotificationService notificationService;
  private final ActivityService activityService;
  private final FileResourceService fileResourceService;

  @Bean
  public FeedbackService feedbackService(
      @Lazy TraceService traceService, @Lazy DeclaredActivityService declaredActivityService) {
    return new FeedbackServiceImpl(
        feedbackRepository,
        declaredActivityRepository,
        declaredActivityService,
        associationService,
        traceService,
        declaredSkillProgressService,
        loggedInUserService,
        notificationService,
        activityService,
        fileResourceService);
  }
}
