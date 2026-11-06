package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityNode;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper.ActivityMapper;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.specification.ActivitySpecification;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityDatabaseRepository
    extends GenericJpaRepositoryAdapter<Activity, ActivityEntity> implements ActivityRepository {
  private final ActivityJpaRepository activityJpaRepository;

  protected ActivityDatabaseRepository(ActivityJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ActivityEntity.class, ActivityMapper.INSTANCE);
    this.activityJpaRepository = jpaRepository;
  }

  @Override
  public boolean existsById(UUID id) {
    return jpaRepository.existsById(id);
  }

  @Override
  public boolean existsSubActivityById(UUID id) {
    return activityJpaRepository.existsByIdAndMainActivityIdIsNotNull(id);
  }

  @Override
  public List<Activity> findAllSubActivitiesOf(UUID mainActivityId) {
    return ActivityNode.orderedFrom(
        mainActivityId,
        activityJpaRepository.findAllByMainActivityId(mainActivityId).stream()
            .map(ActivityMapper.INSTANCE::toDomain)
            .toList());
  }

  @Override
  public Map<UUID, UUID> findMainActivityIdsOfSubActivities(Collection<UUID> ids) {
    if (ids.isEmpty()) {
      return Map.of();
    }
    return activityJpaRepository.findAllByIdInAndMainActivityIdIsNotNull(ids).stream()
        .collect(Collectors.toMap(ActivityEntity::getId, ActivityEntity::getMainActivityId));
  }

  @Override
  public PagedResult<Activity> findAll(
      EActivityThematic thematic,
      PageCriteria pageCriteria,
      List<UUID> studentInstitutionIds,
      List<UUID> studentGroupIds) {
    var specification =
        ActivitySpecification.isPublished()
            .and(ActivitySpecification.isMainActivity())
            .and(ActivitySpecification.visibleByInstitutions(studentInstitutionIds))
            .and(ActivitySpecification.visibleByGroups(studentGroupIds));
    if (thematic != null)
      specification = specification.and(ActivitySpecification.withThematic(thematic));

    var sort = Sort.by(Sort.Direction.DESC, "createdAt");

    return findAll(
        specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), sort));
  }

  @Override
  public PagedResult<ActivityStaffOverviewData> findAllStaffOverview(
      EActivityThematic thematic, PageCriteria pageCriteria) {
    var specification =
        thematic != null
            ? ActivitySpecification.isMainActivity()
                .and(ActivitySpecification.withThematic(thematic))
            : ActivitySpecification.isMainActivity();
    var sort = Sort.by(Sort.Direction.DESC, "updatedAt");
    var pagedActivities =
        findAll(specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), sort));
    return new PagedResult<>(
        pagedActivities.content().stream()
            .map(
                a ->
                    new ActivityStaffOverviewData(
                        a.getId(),
                        a.getTitle(),
                        a.getThematic(),
                        List.of(),
                        a.getStatus(),
                        a.getUpdatedAt()))
            .toList(),
        pagedActivities.pageInfo());
  }

  @Override
  public PagedResult<Activity> findLatest(
      Duration durationForLate,
      List<Activity> activityToExclude,
      PageCriteria pageCriteria,
      List<UUID> studentInstitutionIds,
      List<UUID> studentGroupIds) {
    var sort = Sort.by(Sort.Direction.DESC, "createdAt");
    var specification =
        ActivitySpecification.isPublished()
            .and(ActivitySpecification.isMainActivity())
            .and(ActivitySpecification.latest(durationForLate))
            .and(ActivitySpecification.visibleByInstitutions(studentInstitutionIds))
            .and(ActivitySpecification.visibleByGroups(studentGroupIds))
            .and(
                ActivitySpecification.exclude(
                    activityToExclude.stream().map(ActivityMapper.INSTANCE::fromDomain).toList()));
    return findAll(
        specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), sort));
  }

  @Override
  public PagedResult<Activity> findWithFeedbacks(
      UUID staffId, PageCriteria pageCriteria, EFeedbackStatus... status) {
    var specification = ActivitySpecification.hasFeedback(staffId, status);
    var sort = Sort.by(Sort.Direction.DESC, "createdAt");
    return findAll(
        specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), sort));
  }
}
