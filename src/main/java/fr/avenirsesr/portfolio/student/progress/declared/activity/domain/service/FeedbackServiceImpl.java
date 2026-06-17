package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_DESCRIPTION_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalEnrichedTextMaxLength;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalTextMaxLength;

import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.domain.utils.AssociationUtils;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.notification.domain.model.notification.AskForFeedbackNotification;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.FeedbackData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.FeedbackInProcessException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.FeedbackMaximumIterationReachedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.FeedbackNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.FeedbackRepository;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
  private final FeedbackRepository feedbackRepository;
  private final DeclaredActivityService declaredActivityService;
  private final AssociationService associationService;
  private final TraceService traceService;
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final LoggedInUserService loggedInUserService;
  private final NotificationService notificationService;

  @Override
  public Feedback createFeedback(UUID declaredActivityId) {
    DeclaredActivity declaredActivity =
        declaredActivityService.fetchActivityAndCheckLoggedInStudentAuthorization(
            declaredActivityId);

    validateOptionalEnrichedTextMaxLength(
        "reflexion", declaredActivity.getReflection(), RICH_DESCRIPTION_LENGTH);

    var allAssociations =
        associationService.getAllOf(
            declaredActivityId,
            DeclaredActivity.class,
            List.of(
                EAssociationType.DECLARED_ACTIVITY_TRACE,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL));

    var traceIds =
        AssociationUtils.getIdsOf(
            allAssociations, EAssociationType.DECLARED_ACTIVITY_TRACE, Trace.class);

    var declaredSkillsIds =
        AssociationUtils.getIdsOf(
            allAssociations,
            EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
            DeclaredSkillProgress.class);

    List<Trace> traces = traceService.findAllTracesById(traceIds);
    List<DeclaredSkillProgress> declaredSkills =
        declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(declaredSkillsIds);

    List<Feedback> existingFeedbacks =
        feedbackRepository.findAllByDeclaredActivityId(declaredActivityId);

    if (!existingFeedbacks.isEmpty()) {
      Feedback lastFeedback = existingFeedbacks.getLast();
      switch (lastFeedback.getStatus()) {
        case IN_PROCESS -> throw new FeedbackInProcessException();
        case NEW -> {
          lastFeedback.setReflexion(declaredActivity.getReflection());
          lastFeedback.setAssociatedTraces(traces);
          lastFeedback.setAssociatedDeclaredSkills(declaredSkills);
          return feedbackRepository.save(lastFeedback);
        }
        case SUBMITTED -> {
          if (existingFeedbacks.size()
              >= declaredActivity.getActivity().getFeedbackAllowedIterations()) {
            throw new FeedbackMaximumIterationReachedException();
          }
        }
      }
    }

    int iteration = existingFeedbacks.size() + 1;
    Feedback feedback =
        Feedback.create(
            declaredActivity, declaredActivity.getReflection(), traces, declaredSkills, iteration);
    var savedFeedback = feedbackRepository.save(feedback);

    var author = savedFeedback.getDeclaredActivity().getActivity().getAuthor().getUser();
    notificationService.notify(new AskForFeedbackNotification(author, savedFeedback));

    return savedFeedback;
  }

  @Override
  public FeedbackData getFeedbackDetails(UUID feedbackId, EUserCategory userCategory) {
    var user = loggedInUserService.getLoggedInUser();
    Feedback feedback =
        feedbackRepository.findById(feedbackId).orElseThrow(FeedbackNotFoundException::new);

    return switch (userCategory) {
      case STUDENT -> getStudentFeedbackDetails(user, feedback);
      case STAFF -> getStaffFeedbackDetails(user, feedback);
    };
  }

  private FeedbackData getStaffFeedbackDetails(User loggedInUser, Feedback feedback) {
    if (!loggedInUser.equals(feedback.getDeclaredActivity().getActivity().getAuthor().getUser())) {
      throw new UserNotAuthorizedException();
    }

    return new FeedbackData(
        feedback.getId(),
        feedback.getCreatedAt(),
        feedback.getUpdatedAt(),
        feedback.getDeclaredActivity(),
        feedback.getReflexion().orElse(null),
        feedback.getFeedback().orElse(null),
        feedback.getStatus(),
        feedback.getIteration(),
        feedback.getAssociatedTraces(),
        feedback.getAssociatedDeclaredSkills());
  }

  @Override
  public FeedbackData getStudentFeedbackDetails(User loggedInUser, Feedback feedback) {
    if (!loggedInUser.equals(feedback.getDeclaredActivity().getStudent().getUser())) {
      throw new UserNotAuthorizedException();
    }

    String feedbackText =
        feedback.getStatus() == EFeedbackStatus.SUBMITTED
            ? feedback.getFeedback().orElse(null)
            : null;
    return new FeedbackData(
        feedback.getId(),
        feedback.getCreatedAt(),
        feedback.getUpdatedAt(),
        feedback.getDeclaredActivity(),
        feedback.getReflexion().orElse(null),
        feedbackText,
        feedback.getStatus(),
        feedback.getIteration(),
        feedback.getAssociatedTraces(),
        feedback.getAssociatedDeclaredSkills());
  }

  @Override
  public void updateFeedback(UUID feedbackId, String feedback) {
    Feedback feedbackToUpdate =
        feedbackRepository.findById(feedbackId).orElseThrow(FeedbackNotFoundException::new);

    User loggedInUser = loggedInUserService.getLoggedInUser();
    Staff author = feedbackToUpdate.getDeclaredActivity().getActivity().getAuthor();

    if (!loggedInUser.equals(author.getUser())) {
      throw new UserNotAuthorizedException();
    }

    validateOptionalTextMaxLength("feedback", feedback, RICH_DESCRIPTION_LENGTH);

    feedbackToUpdate.setFeedback(feedback);
    feedbackRepository.save(feedbackToUpdate);
  }

  @Override
  public PagedResult<Feedback> getStaffFeedbacks(
      EFeedbackStatus statusFilter, UUID activityId, PageCriteria pageCriteria) {
    var staff = loggedInUserService.getLoggedInStaff();
    return feedbackRepository.findByStaff(staff.getId(), statusFilter, activityId, pageCriteria);
  }
}
