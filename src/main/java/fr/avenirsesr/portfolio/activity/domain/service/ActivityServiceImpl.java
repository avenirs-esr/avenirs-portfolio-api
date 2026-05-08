package fr.avenirsesr.portfolio.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.*;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityDetailData;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.file.domain.model.ActivityBanner;
import fr.avenirsesr.portfolio.file.domain.port.input.ActivityResourceService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {
  private static final String ACTIVITY_BANNER_PATH = "/storage/activities/";

  private static final Duration DURATION_FOR_LATEST = Duration.ofDays(90);
  private final ActivityRepository activityRepository;
  private final ActivityDraftRepository activityDraftRepository;
  private final DeclaredActivityService declaredActivityService;
  private final LoggedInUserService loggedInUserService;
  private final ActivityResourceService activityResourceService;

  @Override
  public Activity create(
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary) {
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
            title,
            thematic,
            summary,
            description,
            executionPeriodInfo,
            executionPeriodInfoSummary);
    activityRepository.save(activity);
    return activity;
  }

  @Override
  public Activity getActivityById(UUID id) {
    return activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
  }

  @Override
  public ActivityDetailData getActivityDetail(UUID id) {
    Activity activity = activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
    ActivityBanner activityBanner = activityResourceService.getActivityBanner(activity);
    return new ActivityDetailData(
        activity.getId(),
        activity.getTitle(),
        activity.getThematic(),
        declaredActivityService.getByActivity(activity).map(DeclaredActivity::getId),
        new FileData(
            Optional.of(activityBanner.getId()),
            Optional.of(activityBanner.getFileName()),
            ACTIVITY_BANNER_PATH + activityBanner.getId()),
        activity.getSummary(),
        activity.getDescription(),
        activity.getExecutionPeriodInfo(),
        activity.getCreatedAt(),
        activity.getUpdatedAt());
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
    var draft = ActivityDraft.create(title, staff);
    return activityDraftRepository.save(draft);
  }
}
