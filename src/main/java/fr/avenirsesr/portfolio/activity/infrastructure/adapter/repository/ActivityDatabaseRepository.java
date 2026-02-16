package fr.avenirsesr.portfolio.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper.ActivityMapper;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.specification.ActivitySpecification;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
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
    var specification = thematic != null ? ActivitySpecification.withThematic(thematic) : null;

    var sort = Sort.by(Sort.Direction.DESC, "createdAt");

    return findAll(
        specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), sort));
  }
}
