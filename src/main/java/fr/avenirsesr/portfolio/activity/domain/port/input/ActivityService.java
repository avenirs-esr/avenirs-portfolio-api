package fr.avenirsesr.portfolio.activity.domain.port.input;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityDetailData;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ActivityService {
  Activity create(
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String executionPeriodInfo);

  ActivityDetailData getActivityDetail(UUID id);

  Map<EActivityThematic, List<Activity>> getActivityNavigation();

  PagedResult<ActivityWithStudentStatusData> activitiesView(
      EActivityThematic thematic, PageCriteria pageCriteria);

  PagedResult<ActivityWithStudentStatusData> latestActivitiesView(PageCriteria pageCriteria);
}
