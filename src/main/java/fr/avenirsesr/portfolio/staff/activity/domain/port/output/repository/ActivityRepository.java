package fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ActivityRepository extends GenericRepositoryPort<Activity> {
  boolean existsById(UUID id);

  boolean existsSubActivityById(UUID id);

  List<Activity> findAllSubActivitiesOf(UUID mainActivityId);

  Map<UUID, UUID> findMainActivityIdsOfSubActivities(Collection<UUID> ids);

  PagedResult<Activity> findAll(
      EActivityThematic thematic,
      PageCriteria pageCriteria,
      List<UUID> studentInstitutionIds,
      List<UUID> studentGroupIds);

  PagedResult<ActivityStaffOverviewData> findAllStaffOverview(
      EActivityThematic thematic, PageCriteria pageCriteria);

  PagedResult<Activity> findLatest(
      Duration durationForLate,
      List<Activity> activityToExclude,
      PageCriteria pageCriteria,
      List<UUID> studentInstitutionIds,
      List<UUID> studentGroupIds);

  PagedResult<Activity> findWithFeedbacks(
      UUID staffId, PageCriteria pageCriteria, EFeedbackStatus... status);
}
