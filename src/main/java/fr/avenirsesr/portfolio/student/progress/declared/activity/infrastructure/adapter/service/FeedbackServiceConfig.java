package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.FeedbackRepository;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service.FeedbackServiceImpl;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
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
  private final AssociationService associationService;
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final LoggedInUserService loggedInUserService;
  private final NotificationService notificationService;

  @Bean
  public FeedbackService feedbackService(
      @Lazy TraceService traceService, @Lazy DeclaredActivityService declaredActivityService) {
    return new FeedbackServiceImpl(
        feedbackRepository,
        declaredActivityService,
        associationService,
        traceService,
        declaredSkillProgressService,
        loggedInUserService,
        notificationService);
  }
}
