package fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends GenericRepositoryPort<Activity> {
  PagedResult<Activity> findAll(EActivityThematic thematic, PageCriteria pageCriteria);

  PagedResult<ActivityStaffOverviewData> findAllStaffOverview(
      EActivityThematic thematic, PageCriteria pageCriteria);

  PagedResult<Activity> findLatest(
      Duration durationForLate, List<Activity> activityToExclude, PageCriteria pageCriteria);

  PagedResult<Activity> findWithFeedbacks(
      UUID authorId, PageCriteria pageCriteria, EFeedbackStatus... status);
}
