package fr.avenirsesr.portfolio.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.*;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.activity.domain.exception.ActivityDraftNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.file.domain.port.input.ActivityResourceService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

  private static final Duration DURATION_FOR_LATEST = Duration.ofDays(90);
  private final ActivityRepository activityRepository;
  private final ActivityDraftRepository activityDraftRepository;
  private final DeclaredActivityService declaredActivityService;
  private final LoggedInUserService loggedInUserService;
  private final ActivityResourceService activityResourceService;

  @Override
  public Activity create(
      UUID id,
      Staff author,
      String title,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      boolean enableReflection,
      int traceAllowedAssociations,
      int feedbackAllowedIterations) {
    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    requireNotNull("thematic", thematic);
    requireNotBlankAndMaxLength("summary", summary, SUMMARY_LENGTH);
    requireNotBlankAndMaxLength("description", description, RICH_TEXT_LENGTH);
    requireNotBlankAndMaxLength(
        "executionPeriodInfo", executionPeriodInfo, ACTIVITY_EXECUTION_PERIOD_INFO);
    validateOptionalTextMaxLength(
        "executionPeriodInfoSummary", executionPeriodInfoSummary, TITLE_LENGTH);

    var activity =
        Activity.create(
            id,
            author,
            title,
            thematic,
            summary,
            description,
            executionPeriodInfo,
            executionPeriodInfoSummary,
            enableReflection,
            traceAllowedAssociations,
            feedbackAllowedIterations);
    activityRepository.save(activity);
    return activity;
  }

  @Override
  public Activity publish(UUID activityDraftId) {
    var staff = loggedInUserService.getLoggedInStaff();
    var draft =
        activityDraftRepository
            .findById(activityDraftId)
            .orElseThrow(ActivityDraftNotFoundException::new);
    if (!draft.getAuthor().equals(staff)) {
      throw new UserNotAuthorizedException();
    }

    if (draft.getSummary().isEmpty()) {
      throw new FieldValidationException(
          EErrorCode.NOT_BLANK, "summary must be defined to publish an activity");
    }

    var activity =
        activityRepository.save(
            Activity.create(
                draft.getId(),
                draft.getAuthor(),
                draft.getTitle(),
                draft.getThematic(),
                draft.getSummary().get(),
                draft.getDescription().orElse(null),
                draft.getExecutionPeriodInfo().orElse(null),
                draft.getExecutionPeriodInfoSummary().orElse(null),
                draft.isEnableReflection(),
                draft.getTraceAllowedAssociations(),
                draft.getFeedbackAllowedIterations()));

    activityDraftRepository.removeFromDatabase(draft);
    return activity;
  }

  @Override
  public Activity getActivityById(UUID id) {
    return activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
  }

  @Override
  public ActivityDraft getActivityDraftById(UUID id) {
    var staff = loggedInUserService.getLoggedInStaff();
    var draft = activityDraftRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
    if (!draft.getAuthor().equals(staff)) {
      throw new UserNotAuthorizedException();
    }
    return draft;
  }

  @Override
  public FileData getActivityBanner(Activity activity) {
    return activityResourceService.getActivityBanner(activity);
  }

  @Override
  public FileData getActivityBanner(ActivityDraft activity) {
    // todo will be refactored when draft banner will supported
    return null;
  }

  @Override
  public ActivityPresentationData getActivityPresentation(EActivityStatus activityStatus, UUID id) {
    return switch (activityStatus) {
      case PUBLISHED -> {
        Activity activity =
            activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
        yield new ActivityPresentationData(
            activity, declaredActivityService.getByActivity(activity).map(DeclaredActivity::getId));
      }
      case DRAFT -> null;
    };
  }

  @Override
  public Map<EActivityThematic, List<Activity>> getActivityNavigation() {
    return activityRepository.findAll().stream()
        .collect(
            Collectors.groupingBy(
                Activity::getThematic,
                () -> new EnumMap<>(EActivityThematic.class),
                Collectors.toList()));
  }

  @Override
  public PagedResult<ActivityWithStudentStatusData> activitiesView(
      EActivityThematic thematic, PageCriteria pageCriteria) {
    var pagedActivities = activityRepository.findAll(thematic, pageCriteria);
    var student = loggedInUserService.getLoggedInStudent();
    var subscribedActivities = declaredActivityService.getAllDeclaredActivitiesOf(student);
    return new PagedResult<>(
        pagedActivities.content().stream()
            .map(
                activity ->
                    new ActivityWithStudentStatusData(
                        activity,
                        activity.getCreatedAt().isAfter(Instant.now().minus(DURATION_FOR_LATEST)),
                        subscribedActivities.stream()
                            .filter(a -> a.getActivity().equals(activity))
                            .map(DeclaredActivity::getStatus)
                            .findFirst()
                            .orElse(null)))
            .toList(),
        pagedActivities.pageInfo());
  }

  @Override
  public PagedResult<ActivityWithStudentStatusData> latestActivitiesView(
      PageCriteria pageCriteria) {
    var student = loggedInUserService.getLoggedInStudent();
    var subscribedActivities = declaredActivityService.getAllDeclaredActivitiesOf(student);
    var pagedActivities =
        activityRepository.findLatest(
            DURATION_FOR_LATEST,
            subscribedActivities.stream().map(DeclaredActivity::getActivity).toList(),
            pageCriteria);
    return new PagedResult<>(
        pagedActivities.content().stream()
            .map(activity -> new ActivityWithStudentStatusData(activity, true, null))
            .toList(),
        pagedActivities.pageInfo());
  }

  @Override
  public ActivityDraft createActivityDraft(String title) {
    var staff = loggedInUserService.getLoggedInStaff();
    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    var draft = ActivityDraft.create(title, staff);
    return activityDraftRepository.save(draft);
  }

  @Override
  public ActivityDraft updateActivity(
      EActivityStatus status,
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      Integer traceAllowedAssociations,
      Integer feedbackAllowedIterations,
      Boolean enableReflection) {
    return switch (status) {
      case DRAFT ->
          updateActivityDraft(
              id,
              title,
              thematic,
              summary,
              description,
              executionPeriodInfo,
              executionPeriodInfoSummary,
              traceAllowedAssociations,
              feedbackAllowedIterations,
              enableReflection);
      case PUBLISHED -> throw new UnsupportedOperationException();
    };
  }

  private ActivityDraft updateActivityDraft(
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      Integer traceAllowedAssociations,
      Integer feedbackAllowedIterations,
      Boolean enableReflection) {
    var loggedInStaff = loggedInUserService.getLoggedInStaff();
    var draft =
        activityDraftRepository.findById(id).orElseThrow(ActivityDraftNotFoundException::new);

    if (!draft.getAuthor().equals(loggedInStaff)) {
      throw new UserNotAuthorizedException();
    }

    validateOptionalTextMaxLength("summary", summary, SUMMARY_LENGTH);
    validateOptionalTextMaxLength("description", description, RICH_TEXT_LENGTH);
    validateOptionalTextMaxLength(
        "executionPeriodInfo", executionPeriodInfo, ACTIVITY_EXECUTION_PERIOD_INFO);
    validateOptionalTextMaxLength(
        "executionPeriodInfoSummary", executionPeriodInfoSummary, TITLE_LENGTH);

    if (title != null) {
      requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
      draft.setTitle(title);
    }
    if (thematic != null) draft.setThematic(thematic);
    if (summary != null) draft.setSummary(summary);
    if (description != null) draft.setDescription(description);
    if (executionPeriodInfo != null) draft.setExecutionPeriodInfo(executionPeriodInfo);
    if (executionPeriodInfoSummary != null)
      draft.setExecutionPeriodInfoSummary(executionPeriodInfoSummary);
    if (traceAllowedAssociations != null)
      draft.setTraceAllowedAssociations(traceAllowedAssociations);
    if (feedbackAllowedIterations != null)
      draft.setFeedbackAllowedIterations(feedbackAllowedIterations);
    if (enableReflection != null) draft.setEnableReflection(enableReflection);

    var updatedDraft = activityDraftRepository.save(draft);
    log.info("Updated activity draft with id: {}", id);
    return updatedDraft;
  }
}
