package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper.ActivityDraftAuthorizationMapper;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityDraftAuthorizationEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityDraftAuthorizationDatabaseRepository
    extends GenericJpaRepositoryAdapter<ActivityAuthorization, ActivityDraftAuthorizationEntity>
    implements ActivityDraftAuthorizationRepository {
  private final ActivityDraftAuthorizationJpaRepository jpaRepository;

  protected ActivityDraftAuthorizationDatabaseRepository(
      ActivityDraftAuthorizationJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        ActivityDraftAuthorizationEntity.class,
        ActivityDraftAuthorizationMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<ActivityAuthorization> findAllByActivityId(UUID activityDraftId) {
    return jpaRepository.findAllByActivityDraftId(activityDraftId).stream()
        .map(ActivityDraftAuthorizationMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public Optional<ActivityAuthorization> findByActivityIdAndStaffId(
      UUID activityDraftId, UUID staffId) {
    return jpaRepository
        .findByActivityDraftIdAndStaff_Id(activityDraftId, staffId)
        .map(ActivityDraftAuthorizationMapper.INSTANCE::toDomain);
  }

  @Override
  public List<ActivityAuthorization> findAllByActivityIds(Collection<UUID> activityDraftIds) {
    if (activityDraftIds.isEmpty()) {
      return List.of();
    }
    return jpaRepository.findAllByActivityDraftIdIn(activityDraftIds).stream()
        .map(ActivityDraftAuthorizationMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public void deleteAllByActivityId(UUID activityDraftId) {
    jpaRepository.deleteAllByActivityDraftId(activityDraftId);
  }
}
