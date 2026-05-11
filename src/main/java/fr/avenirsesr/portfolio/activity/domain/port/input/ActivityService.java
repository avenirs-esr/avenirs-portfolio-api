package fr.avenirsesr.portfolio.activity.domain.port.input;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ActivityService {
  Activity create(
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
      int feedbackAllowedIterations);

  FileData getActivityBanner(Activity activity);

  FileData getActivityBanner(ActivityDraft activity);

  Activity getActivityById(UUID id);

  ActivityDraft getActivityDraftById(UUID id);

  ActivityPresentationData getActivityPresentation(EActivityStatus activityStatus, UUID id);

  Map<EActivityThematic, List<Activity>> getActivityNavigation();

  PagedResult<ActivityWithStudentStatusData> activitiesView(
      EActivityThematic thematic, PageCriteria pageCriteria);

  PagedResult<ActivityWithStudentStatusData> latestActivitiesView(PageCriteria pageCriteria);

  ActivityDraft createActivityDraft(String title);

  ActivityDraft updateActivity(
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
      Boolean enableReflection);
}
