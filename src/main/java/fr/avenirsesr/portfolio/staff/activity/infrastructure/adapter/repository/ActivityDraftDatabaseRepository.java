package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper.ActivityDraftMapper;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityDraftEntity;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityDraftDatabaseRepository
    extends GenericJpaRepositoryAdapter<ActivityDraft, ActivityDraftEntity>
    implements ActivityDraftRepository {
  protected ActivityDraftDatabaseRepository(ActivityDraftJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ActivityDraftEntity.class, ActivityDraftMapper.INSTANCE);
  }
}
