package fr.avenirsesr.portfolio.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper.ActivityDraftMapper;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityDraftEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityDraftDatabaseRepository
    extends GenericJpaRepositoryAdapter<ActivityDraft, ActivityDraftEntity>
    implements ActivityDraftRepository {
  protected ActivityDraftDatabaseRepository(ActivityDraftJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ActivityDraftEntity.class, ActivityDraftMapper.INSTANCE);
  }
}
