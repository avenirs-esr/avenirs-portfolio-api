package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityNode;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper.ActivityDraftMapper;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityDraftEntity;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityDraftDatabaseRepository
    extends GenericJpaRepositoryAdapter<ActivityDraft, ActivityDraftEntity>
    implements ActivityDraftRepository {
  private final ActivityDraftJpaRepository activityDraftJpaRepository;

  protected ActivityDraftDatabaseRepository(ActivityDraftJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ActivityDraftEntity.class, ActivityDraftMapper.INSTANCE);
    this.activityDraftJpaRepository = jpaRepository;
  }

  @Override
  public boolean existsById(UUID id) {
    return jpaRepository.existsById(id);
  }

  @Override
  public boolean existsSubActivityById(UUID id) {
    return activityDraftJpaRepository.existsByIdAndMainActivityIdIsNotNull(id);
  }

  @Override
  public List<ActivityDraft> findAllSubActivitiesOf(UUID mainActivityId) {
    return ActivityNode.orderedFrom(
        mainActivityId,
        activityDraftJpaRepository.findAllByMainActivityId(mainActivityId).stream()
            .map(ActivityDraftMapper.INSTANCE::toDomain)
            .toList());
  }

  @Override
  public Map<UUID, UUID> findMainActivityIdsOfSubActivities(Collection<UUID> ids) {
    if (ids.isEmpty()) {
      return Map.of();
    }
    return activityDraftJpaRepository.findAllByIdInAndMainActivityIdIsNotNull(ids).stream()
        .collect(
            Collectors.toMap(ActivityDraftEntity::getId, ActivityDraftEntity::getMainActivityId));
  }
}
