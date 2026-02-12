package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.imported.domain.model.ActivityProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.ActivityProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper.ActivityProgressMapper;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.ActivityProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityProgressDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<ActivityProgress, ActivityProgressEntity>
    implements ActivityProgressRepository {
  private final ActivityProgressJpaRepository jpaRepository;

  public ActivityProgressDatabaseRepository(ActivityProgressJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        ActivityProgressEntity.class,
        ActivityProgressMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }
}
