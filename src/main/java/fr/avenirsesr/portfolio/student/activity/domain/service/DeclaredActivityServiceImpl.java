package fr.avenirsesr.portfolio.student.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotBlankAndMaxLength;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotNull;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateDateOrder;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalEnrichedTextMaxLength;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.validateOptionalTextMaxLength;

import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.AssessmentSentNotification;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityAssessmentException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityUnpublishedException;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAssessment;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityAuthorizationService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssessmentDetailsData;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationsData;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.activity.domain.data.FeedbackData;
import fr.avenirsesr.portfolio.student.activity.domain.exception.*;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivityAssessment;
import fr.avenirsesr.portfolio.student.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.FeedbackRepository;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.association.domain.utils.AssociationUtils;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.SkillProgressService;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class DeclaredActivityServiceImpl implements DeclaredActivityService {
  private static final int MIN_ASSESSMENT_GRADE = 0;

  private final DeclaredActivityRepository declaredActivityRepository;
  private final ActivityService activityService;
  private final AssociationService associationService;
  private final LoggedInUserService loggedInUserService;
  private final FeedbackRepository feedbackRepository;
  private final FeedbackService feedbackService;
  private final NotificationService notificationService;
  private final ActivityAuthorizationService activityAuthorizationService;
  private final TraceService traceService;
  private final SkillProgressService skillProgressService;
  private final DeclaredExperienceService declaredExperienceService;

  @Override
  public PagedResult<DeclaredActivity> getDeclaredActivities(
      PageCriteria pageCriteria,
      String keyword,
      List<EActivityThematic> thematic,
      List<EActivityStatus> status,
      SortCriteria sortCriterias,
      Boolean isValorized) {
    Student student = loggedInUserService.getLoggedInStudent();
    var graph = FetchGraph.init().add("activity").fetch("author");

    return declaredActivityRepository.findStudentActivitiesByProgressAndDate(
        student, keyword, thematic, status, sortCriterias, pageCriteria, isValorized, graph);
  }

  @Override
  public List<DeclaredActivity> getAllDeclaredActivitiesOf(Student student) {
    var graph = FetchGraph.init().add("activity").fetch("author");
    return declaredActivityRepository.findAllByStudent(student, graph);
  }

  @Override
  public PagedResult<DeclaredActivity> getDeclaredActivitiesToAssessBy(
      Staff staff, List<EDeclaredActivityStatus> statuses, PageCriteria pageCriteria) {
    var graph =
        FetchGraph.init().add("student").fetch("user").root().add("activity").fetch("author");

    return declaredActivityRepository.findAllSubmittedForAssessmentBy(
        staff.getId(), statuses, pageCriteria, graph);
  }

  @Override
  public Optional<DeclaredActivity> getByActivity(Activity activity) {
    Student student = loggedInUserService.getLoggedInStudent();
    return declaredActivityRepository.findByActivity(student, activity);
  }

  @Override
  public DeclaredActivity subscribe(UUID activityId, LocalDate startDate, LocalDate endDate) {
    return subscribe(UUID.randomUUID(), activityId, startDate, endDate);
  }

  @Override
  public DeclaredActivity subscribe(
      UUID declaredActivityId, UUID activityId, LocalDate startDate, LocalDate endDate) {
    Student student = loggedInUserService.getLoggedInStudent();
    Activity activity = activityService.getActivityById(activityId);

    if (activity.getStatus() == EActivityStatus.UNPUBLISHED) {
      throw new ActivityUnpublishedException();
    }

    validateActivityDates(startDate, endDate, Instant.now());

    var existingDeclaredActivity = declaredActivityRepository.findByActivity(student, activity);
    if (existingDeclaredActivity.filter(da -> !da.isUnsubscribed()).isPresent()) {
      throw new DeclaredActivityAlreadyExistException();
    }

    var declaredActivity =
        existingDeclaredActivity.orElse(
            DeclaredActivity.create(
                declaredActivityId, student, activity, null, null, startDate, endDate, null));

    declaredActivity.setUnsubscribedAt(null);
    declaredActivity.setStartDate(startDate);
    declaredActivity.setEndDate(endDate);

    return declaredActivityRepository.save(declaredActivity);
  }

  @Override
  public void unsubscribeMultiple(List<UUID> activityIds) {
    Student student = loggedInUserService.getLoggedInStudent();

    List<DeclaredActivity> declaredActivities =
        declaredActivityRepository.findAllByActivityIdAndStudent(
            activityIds,
            student,
            FetchGraph.init().add("activity").fetch("author").root().add("student").fetch("user"));

    if (!declaredActivities.stream()
        .map(declaredActivity -> declaredActivity.getActivity().getId())
        .collect(Collectors.toSet())
        .containsAll(activityIds)) {
      throw new DeclaredActivityNotFoundException();
    }

    var activitiesToUnsubscribe =
        declaredActivities.stream()
            .filter(declaredActivity -> !declaredActivity.isUnsubscribed())
            .toList();

    if (activitiesToUnsubscribe.isEmpty()) {
      return;
    }

    feedbackService.deletePendingFeedbacks(
        activitiesToUnsubscribe.stream().map(DeclaredActivity::getId).toList());

    var unsubscribedAt = Instant.now();
    activitiesToUnsubscribe.forEach(
        declaredActivity -> declaredActivity.unsubscribe(unsubscribedAt));

    declaredActivityRepository.saveAll(activitiesToUnsubscribe);
  }

  @Override
  public void finish(UUID declaredActivityId) {
    Student student = loggedInUserService.getLoggedInStudent();
    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId)
            .orElseThrow(DeclaredActivityNotFoundException::new);

    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }

    if (declaredActivity.isUnsubscribed()) {
      throw new DeclaredActivityUnsubscribedException();
    }

    if (declaredActivity.getStartedAt().isEmpty()) {
      throw new DeclaredActivityHasNotStartedException();
    }

    if (declaredActivity.getFinishedAt().isPresent()) {
      throw new DeclaredActivityAlreadyFinishedException();
    }

    declaredActivity.setFinishedAt(Instant.now());

    declaredActivityRepository.save(declaredActivity);
  }

  @Override
  public void submitForAssessment(UUID declaredActivityId) {
    DeclaredActivity declaredActivity =
        fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);

    if (declaredActivity.isUnsubscribed()) {
      throw new DeclaredActivityUnsubscribedException();
    }

    if (isSubmittedOrFinished(getDeclaredActivityStatus(declaredActivity))) {
      throw new DeclaredActivityLockedException();
    }

    if (declaredActivity
        .getActivity()
        .getAssessment()
        .filter(assessment -> !assessment.isEmpty())
        .isEmpty()) {
      throw new ActivityAssessmentException("The activity does not expect an assessment");
    }

    var requestedAt = Instant.now();
    declaredActivity.setSubmittedAt(requestedAt);
    declaredActivity.setAssessment(DeclaredActivityAssessment.requested(requestedAt));

    declaredActivityRepository.save(declaredActivity);
  }

  @Override
  public void updateReflection(UUID declaredActivityId, String reflection) {
    Student student = loggedInUserService.getLoggedInStudent();
    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId)
            .orElseThrow(DeclaredActivityNotFoundException::new);
    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }
    if (declaredActivity.isUnsubscribed()) {
      throw new DeclaredActivityUnsubscribedException();
    }
    if (declaredActivity.getFinishedAt().isPresent()) {
      throw new DeclaredActivityAlreadyFinishedException();
    }
    if (!declaredActivity.getActivity().isEnableReflection()) {
      throw new UserNotAuthorizedException();
    }
    validateOptionalEnrichedTextMaxLength("reflection", reflection, RICH_TEXT_LENGTH);

    declaredActivity.setReflection(reflection);

    if (declaredActivity.getStartedAt().isEmpty()) {
      declaredActivity.setStartedAt(Instant.now());
    }
    declaredActivityRepository.save(declaredActivity);
  }

  @Override
  public void sendAssessment(
      UUID declaredActivityId, String comment, Integer grade, Boolean validation) {
    Staff staff = loggedInUserService.getLoggedInStaff();
    DeclaredActivity declaredActivity = fetchDeclaredActivityAsEvaluator(staff, declaredActivityId);

    DeclaredActivityAssessment currentAssessment = assessmentOf(declaredActivity);

    if (currentAssessment.isSent()) {
      throw new ActivityAssessmentException("This assessment has already been sent");
    }

    Instant now = Instant.now();
    DeclaredActivityAssessment assessment =
        currentAssessment.send(comment, grade, validation, now, staff);

    ActivityAssessment setup = requireAssessmentSetup(declaredActivity.getActivity());
    validateAssessment(setup, assessment);
    requireCompleteAssessment(setup, assessment);

    declaredActivity.setAssessment(assessment);

    if (declaredActivity.getFinishedAt().isEmpty()) {
      declaredActivity.setFinishedAt(now);
    }

    declaredActivityRepository.save(declaredActivity);

    notificationService.notify(
        new AssessmentSentNotification(declaredActivity.getStudent().getUser(), declaredActivity));

    log.debug(
        "Sent the assessment of declared activity [{}] to student [{}]",
        declaredActivityId,
        declaredActivity.getStudent().getId());
  }

  private DeclaredActivity fetchDeclaredActivityAsEvaluator(Staff staff, UUID declaredActivityId) {
    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId)
            .orElseThrow(DeclaredActivityNotFoundException::new);

    activityAuthorizationService.checkRight(
        staff, declaredActivity.getActivity().getId(), EActivityRight.EVALUATION);

    return declaredActivity;
  }

  private void consultSentAssessment(DeclaredActivity declaredActivity) {
    declaredActivity
        .getAssessment()
        .filter(DeclaredActivityAssessment::isSent)
        .ifPresent(
            assessment -> {
              declaredActivity.setAssessment(assessment.consult(Instant.now()));
              declaredActivityRepository.save(declaredActivity);
            });
  }

  private DeclaredActivityAssessment assessmentOf(DeclaredActivity declaredActivity) {
    return declaredActivity
        .getAssessment()
        .orElseGet(
            () ->
                DeclaredActivityAssessment.requested(
                    declaredActivity.getSubmittedAt().orElse(null)));
  }

  private ActivityAssessment requireAssessmentSetup(Activity activity) {
    return activity
        .getAssessment()
        .filter(activityAssessment -> !activityAssessment.isEmpty())
        .orElseThrow(
            () -> new ActivityAssessmentException("This activity does not expect an assessment"));
  }

  private void requireCompleteAssessment(
      ActivityAssessment setup, DeclaredActivityAssessment assessment) {
    if (setup.isCommentEnabled()) {
      requireNotBlankAndMaxLength("comment", assessment.comment(), RICH_TEXT_LENGTH);
    }
    if (setup.isGradeEnabled()) {
      requireNotNull("grade", assessment.grade());
    }
    if (setup.isValidationEnabled()) {
      requireNotNull("validation", assessment.validation());
    }
  }

  private void validateAssessment(ActivityAssessment setup, DeclaredActivityAssessment assessment) {
    validateOptionalTextMaxLength("comment", assessment.comment(), RICH_TEXT_LENGTH);

    if (assessment.comment() != null && !setup.isCommentEnabled()) {
      throw new ActivityAssessmentException("This activity does not expect an assessment comment");
    }
    if (assessment.validation() != null && !setup.isValidationEnabled()) {
      throw new ActivityAssessmentException(
          "This activity does not expect an assessment validation");
    }
    if (assessment.grade() != null) {
      if (!setup.isGradeEnabled()) {
        throw new ActivityAssessmentException("This activity does not expect an assessment grade");
      }
      if (assessment.grade() < MIN_ASSESSMENT_GRADE || assessment.grade() > setup.grade()) {
        throw new ActivityAssessmentException(
            "The assessment grade must be between %d and %d"
                .formatted(MIN_ASSESSMENT_GRADE, setup.grade()));
      }
    }
  }

  @Override
  public DeclaredActivityDetailsData getDeclaredActivityDetails(UUID declaredActivityId) {
    DeclaredActivity declaredActivity =
        fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);

    consultSentAssessment(declaredActivity);

    List<FeedbackData> feedbacks =
        feedbackRepository.findAllByDeclaredActivityId(declaredActivityId).stream()
            .map(
                feedback ->
                    feedbackService.getStudentFeedbackDetails(
                        declaredActivity.getStudent().getUser(), feedback))
            .toList();
    return new DeclaredActivityDetailsData(declaredActivity, feedbacks);
  }

  @Override
  public DeclaredActivityAssessmentDetailsData getAssessmentDetails(UUID declaredActivityId) {
    Staff staff = loggedInUserService.getLoggedInStaff();
    var graph =
        FetchGraph.init()
            .add("student")
            .fetch("user")
            .root()
            .add("activity")
            .fetch("author")
            .fetch("files");

    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId, graph)
            .orElseThrow(DeclaredActivityNotFoundException::new);

    activityAuthorizationService.checkRight(
        staff, declaredActivity.getActivity().getId(), EActivityRight.EVALUATION);

    if (declaredActivity.getSubmittedAt().isEmpty()) {
      throw new DeclaredActivityNotFoundException();
    }

    return new DeclaredActivityAssessmentDetailsData(
        declaredActivity,
        getAssociations(declaredActivityId),
        getDeclaredActivityStatus(declaredActivity));
  }

  private DeclaredActivityAssociationsData getAssociations(UUID declaredActivityId) {
    var associations =
        associationService.getAllOf(
            declaredActivityId,
            DeclaredActivity.class,
            List.of(
                EAssociationType.DECLARED_ACTIVITY_TRACE,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_EXPERIENCE));

    List<Trace> traces =
        traceService.findAllTracesById(
            AssociationUtils.getIdsOf(
                associations, EAssociationType.DECLARED_ACTIVITY_TRACE, Trace.class));
    List<SkillProgress> skillProgresses =
        skillProgressService.findAllSkillProgressesByIds(
            AssociationUtils.getIdsOf(
                associations,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL,
                SkillProgress.class));
    List<DeclaredExperience> declaredExperiences =
        declaredExperienceService.findAllByIds(
            AssociationUtils.getIdsOf(
                associations,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_EXPERIENCE,
                DeclaredExperience.class));

    return new DeclaredActivityAssociationsData(
        traces,
        skillProgresses.isEmpty()
            ? List.of()
            : skillProgressService.getSkillProgressDetails(skillProgresses),
        declaredExperiences);
  }

  private void validateActivityDates(LocalDate startDate, LocalDate endDate, Instant subscribedAt) {

    if ((startDate == null) != (endDate == null)) {
      throw new DeclaredActivityDatesException();
    }

    if (startDate != null) {

      validateDateOrder(startDate, endDate);

      LocalDate subscriptionDate = subscribedAt.atZone(ZoneId.systemDefault()).toLocalDate();

      if (startDate.isBefore(subscriptionDate)) {
        throw new DeclaredActivityStartDateBeforeSubscriptionException();
      }
    }
  }

  @Override
  public void updateDeclaredActivity(
      UUID declaredActivityId, LocalDate startDate, LocalDate endDate, Boolean valorized) {

    var student = loggedInUserService.getLoggedInStudent();
    log.debug("Authenticated student id: {}", student.getId());

    log.debug("Fetching DeclaredActivity with id: {}", declaredActivityId);
    var declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId)
            .orElseThrow(
                () ->
                    new DeclaredActivityNotFoundException(
                        "DeclaredActivity not found with id: " + declaredActivityId));
    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }

    if (declaredActivity.isUnsubscribed()) {
      throw new DeclaredActivityUnsubscribedException();
    }

    if (startDate != null || endDate != null) {
      validateActivityDates(startDate, endDate, declaredActivity.getCreatedAt());
      declaredActivity.setStartDate(startDate);
      declaredActivity.setEndDate(endDate);
    }

    if (valorized != null) {
      declaredActivity.setValorized(valorized);
    }

    declaredActivityRepository.save(declaredActivity);
  }

  @Override
  public PagedResult<DeclaredActivity> searchDeclaredActivity(
      String keyword, PageCriteria pageCriteria) {
    var student = loggedInUserService.getLoggedInStudent();
    var graph = FetchGraph.init().fetch("activity");
    return declaredActivityRepository.findAllByStudent(student, keyword, pageCriteria, graph);
  }

  @Override
  public List<DeclaredActivity> findAllDeclaredActivitiesByIds(List<UUID> ids) {
    return declaredActivityRepository.findAllById(ids);
  }

  @Override
  public List<DeclaredActivity> findAllNotCompletedActivitiesByIds(List<UUID> ids) {
    var graph = FetchGraph.init().fetch("activity");
    return declaredActivityRepository.findAllNotCompletedActivitiesByIds(ids, graph);
  }

  @Override
  public DeclaredActivity fetchActivityAndCheckLoggedInStudentAuthorization(
      UUID declaredActivityId) {
    Student student = loggedInUserService.getLoggedInStudent();
    var graph =
        FetchGraph.init()
            .add("student")
            .fetch("user")
            .root()
            .add("activity")
            .fetch("author")
            .fetch("files");

    DeclaredActivity declaredActivity =
        declaredActivityRepository
            .findById(declaredActivityId, graph)
            .orElseThrow(DeclaredActivityNotFoundException::new);

    if (!declaredActivity.getStudent().equals(student)) {
      throw new UserNotAuthorizedException();
    }
    return declaredActivity;
  }

  @Override
  public boolean areDeclaredActivitiesUnlocked(List<UUID> declaredActivityIds) {
    if (declaredActivityIds.isEmpty()) {
      return true;
    }

    var declaredActivities = findAllDeclaredActivitiesByIds(declaredActivityIds);
    var declaredActivityIdsWithFeedback =
        findFeedbackPresenceByDeclaredActivityId(declaredActivityIds);

    return declaredActivities.stream()
        .noneMatch(
            declaredActivity ->
                isSubmittedOrFinished(
                    declaredActivity,
                    declaredActivityIdsWithFeedback.contains(declaredActivity.getId())));
  }

  @Override
  public void checkDeclaredActivitiesUnlocked(List<UUID> declaredActivityIds) {
    if (!areDeclaredActivitiesUnlocked(declaredActivityIds)) {
      throw new DeclaredActivityLockedException();
    }
  }

  @Override
  public EDeclaredActivityStatus getDeclaredActivityStatus(DeclaredActivity declaredActivity) {
    boolean hasActiveFeedback =
        feedbackRepository
            .findDeclaredActivityIdsHavingActiveFeedbacks(List.of(declaredActivity.getId()))
            .contains(declaredActivity.getId());

    return getDeclaredActivityStatus(declaredActivity, hasActiveFeedback);
  }

  @Override
  public Map<DeclaredActivity, EDeclaredActivityStatus> getDeclaredActivityStatus(
      List<DeclaredActivity> declaredActivities) {
    var declaredActivityIds = declaredActivities.stream().map(DeclaredActivity::getId).toList();

    var idsWithFeedback =
        new HashSet<>(findFeedbackPresenceByDeclaredActivityId(declaredActivityIds));

    return declaredActivities.stream()
        .collect(
            Collectors.toMap(
                Function.identity(),
                declaredActivity ->
                    getDeclaredActivityStatus(
                        declaredActivity, idsWithFeedback.contains(declaredActivity.getId()))));
  }

  private EDeclaredActivityStatus getDeclaredActivityStatus(
      DeclaredActivity declaredActivity, boolean hasActiveFeedback) {
    if (declaredActivity.isUnsubscribed()) {
      return EDeclaredActivityStatus.UNSUBSCRIBED;
    }

    if (declaredActivity.getFinishedAt().isPresent()) {
      return EDeclaredActivityStatus.COMPLETED;
    }

    if (declaredActivity.getSubmittedAt().isPresent()) {
      return EDeclaredActivityStatus.SUBMITTED_FOR_ASSESSMENT;
    }

    if (hasActiveFeedback) {
      return EDeclaredActivityStatus.SUBMITTED_FOR_FEEDBACK;
    }

    if (declaredActivity.getStartedAt().isPresent()
        && declaredActivity.getStartedAt().get().isBefore(Instant.now())) {
      return EDeclaredActivityStatus.IN_PROGRESS;
    }

    return EDeclaredActivityStatus.SUBSCRIBED;
  }

  private List<UUID> findFeedbackPresenceByDeclaredActivityId(List<UUID> declaredActivityIds) {
    if (declaredActivityIds.isEmpty()) {
      return List.of();
    }

    return feedbackRepository.findDeclaredActivityIdsHavingActiveFeedbacks(declaredActivityIds);
  }

  @Override
  public int countEnrolledStudents(Activity activity) {
    return declaredActivityRepository.countEnrolledByActivity(activity);
  }

  @Override
  public int countUnsubscriptionsSince(Activity activity, Instant since) {
    return declaredActivityRepository.countUnsubscribedByActivitySince(activity, since);
  }

  @Override
  public List<DeclaredActivity> getEnrolledStudents(Activity activity) {
    return declaredActivityRepository.findAllEnrolledByActivity(activity, enrolledStudentGraph());
  }

  @Override
  public List<DeclaredActivity> getEnrolledStudentsNotViewedSince(
      Activity activity, Instant since) {
    return declaredActivityRepository.findAllEnrolledNotViewedSince(
        activity, since, enrolledStudentGraph());
  }

  private static FetchGraph enrolledStudentGraph() {
    return FetchGraph.init().add("student").fetch("user").root().add("activity").fetch("author");
  }

  @Override
  public List<UUID> getEnrolledStudentIds(Activity activity) {
    return declaredActivityRepository.findEnrolledStudentIdsByActivity(activity);
  }

  @Override
  public boolean isEnrolled(Activity activity, Student student) {
    return declaredActivityRepository
        .findByActivity(student, activity)
        .filter(declaredActivity -> !declaredActivity.isUnsubscribed())
        .isPresent();
  }

  @Override
  public void delete(UUID declaredActivityId) {
    DeclaredActivity declaredActivity =
        fetchActivityAndCheckLoggedInStudentAuthorization(declaredActivityId);

    if (!declaredActivity.isUnsubscribed()) {
      throw new DeclaredActivityNotUnsubscribedException();
    }

    var feedbackIds =
        feedbackRepository.findAllByDeclaredActivityId(declaredActivityId).stream()
            .map(Feedback::getId)
            .toList();

    associationService.deleteAllOf(List.of(declaredActivityId), DeclaredActivity.class);
    notificationService.deleteNotificationsOf(ENotificationType.ASK_FOR_FEEDBACK, feedbackIds);
    notificationService.deleteNotificationsOf(
        ENotificationType.ASSESSMENT_SENT, List.of(declaredActivityId));
    feedbackService.deleteByDeclaredActivityId(declaredActivity);
    declaredActivityRepository.removeFromDatabase(declaredActivity);

    log.debug(
        "Deleted the declared activity {} of student {} with its associations, notifications"
            + " and feedbacks",
        declaredActivityId,
        declaredActivity.getStudent().getId());
  }

  @Override
  public void deleteAllOfActivity(Activity activity) {
    var graph =
        FetchGraph.init().add("student").fetch("user").root().add("activity").fetch("author");
    var declaredActivities = declaredActivityRepository.findAllByActivity(activity, graph);

    if (declaredActivities.isEmpty()) {
      return;
    }

    var declaredActivityIds = declaredActivities.stream().map(DeclaredActivity::getId).toList();
    var feedbackIds = feedbackService.deleteAllOfDeclaredActivities(declaredActivityIds);

    associationService.deleteAllOf(declaredActivityIds, DeclaredActivity.class);
    notificationService.deleteNotificationsOf(ENotificationType.ASK_FOR_FEEDBACK, feedbackIds);
    notificationService.deleteNotificationsOf(
        ENotificationType.ACTIVITY_MODIFIED, declaredActivityIds);
    notificationService.deleteNotificationsOf(
        ENotificationType.ASSESSMENT_SENT, declaredActivityIds);
    declaredActivityRepository.removeAllFromDatabase(declaredActivities);

    log.debug(
        "Deleted the {} declared activities of activity {} with their associations, notifications"
            + " and feedbacks",
        declaredActivities.size(),
        activity.getId());
  }

  private boolean isSubmittedOrFinished(
      DeclaredActivity declaredActivity, boolean hasActiveFeedback) {
    return isSubmittedOrFinished(getDeclaredActivityStatus(declaredActivity, hasActiveFeedback));
  }

  private boolean isSubmittedOrFinished(EDeclaredActivityStatus status) {
    return status == EDeclaredActivityStatus.SUBMITTED_FOR_FEEDBACK
        || status == EDeclaredActivityStatus.SUBMITTED_FOR_ASSESSMENT
        || status == EDeclaredActivityStatus.COMPLETED;
  }
}
