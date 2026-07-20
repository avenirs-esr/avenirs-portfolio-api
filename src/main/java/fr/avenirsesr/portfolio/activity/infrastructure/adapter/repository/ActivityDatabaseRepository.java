package fr.avenirsesr.portfolio.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper.ActivityMapper;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.specification.ActivitySpecification;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import java.time.Duration;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityDatabaseRepository
    extends GenericJpaRepositoryAdapter<Activity, ActivityEntity> implements ActivityRepository {
  protected ActivityDatabaseRepository(ActivityJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ActivityEntity.class, ActivityMapper.INSTANCE);
  }

  @Override
  public PagedResult<Activity> findAll(EActivityThematic thematic, PageCriteria pageCriteria) {
    var specification = ActivitySpecification.isPublished();
    if (thematic != null)
      specification = specification.and(ActivitySpecification.withThematic(thematic));

    var sort = Sort.by(Sort.Direction.DESC, "createdAt");

    return findAll(
        specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), sort));
  }

  @Override
  public PagedResult<ActivityStaffOverviewData> findAllStaffOverview(
      EActivityThematic thematic, PageCriteria pageCriteria) {
    var specification = thematic != null ? ActivitySpecification.withThematic(thematic) : null;
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
                        a.getAuthor(),
                        a.getStatus(),
                        a.getUpdatedAt()))
            .toList(),
        pagedActivities.pageInfo());
  }

  @Override
  public PagedResult<Activity> findLatest(
      Duration durationForLate, List<Activity> activityToExclude, PageCriteria pageCriteria) {
    var sort = Sort.by(Sort.Direction.DESC, "createdAt");
    var specification =
        ActivitySpecification.isPublished()
            .and(ActivitySpecification.latest(durationForLate))
            .and(
                ActivitySpecification.exclude(
                    activityToExclude.stream().map(ActivityMapper.INSTANCE::fromDomain).toList()));
    return findAll(
        specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), sort));
  }
}
