package fr.avenirsesr.portfolio.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper.ActivityMapper;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<Activity, ActivityEntity>
    implements ActivityRepository {

  protected ActivityDatabaseRepository(ActivityJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ActivityEntity.class, ActivityMapper.INSTANCE);
  }
}
