package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper.ActivityAuthorizationMapper;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityAuthorizationEntity;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.specification.ActivityAuthorizationSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityAuthorizationDatabaseRepository
    extends GenericJpaRepositoryAdapter<ActivityAuthorization, ActivityAuthorizationEntity>
    implements ActivityAuthorizationRepository {
  private final ActivityAuthorizationJpaRepository jpaRepository;

  protected ActivityAuthorizationDatabaseRepository(
      ActivityAuthorizationJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        ActivityAuthorizationEntity.class,
        ActivityAuthorizationMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<ActivityAuthorization> findAllByActivityId(UUID activityId) {
    return jpaRepository.findAllByActivityId(activityId).stream()
        .map(ActivityAuthorizationMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public Optional<ActivityAuthorization> findByActivityIdAndStaffId(UUID activityId, UUID staffId) {
    return jpaRepository
        .findByActivityIdAndStaff_Id(activityId, staffId)
        .map(ActivityAuthorizationMapper.INSTANCE::toDomain);
  }

  @Override
  public List<ActivityAuthorization> findAllByActivityIds(Collection<UUID> activityIds) {
    if (activityIds.isEmpty()) {
      return List.of();
    }
    return jpaRepository.findAllByActivityIdIn(activityIds).stream()
        .map(ActivityAuthorizationMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public List<ActivityAuthorization> findAllByActivityIdsAndStaffId(
      Collection<UUID> activityIds, UUID staffId) {
    if (activityIds.isEmpty()) {
      return List.of();
    }
    return jpaRepository
        .findAll(
            ActivityAuthorizationSpecification.<ActivityAuthorizationEntity>withEntityIdIn(
                    "activityId", activityIds)
                .and(ActivityAuthorizationSpecification.withStaffId(staffId)))
        .stream()
        .map(ActivityAuthorizationMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public boolean existsByStaffId(UUID staffId) {
    return jpaRepository.exists(
        ActivityAuthorizationSpecification.<ActivityAuthorizationEntity>withStaffId(staffId));
  }

  @Override
  public boolean existsByStaffIdAndRight(UUID staffId, EActivityRight right) {
    return jpaRepository.exists(
        ActivityAuthorizationSpecification.<ActivityAuthorizationEntity>withStaffId(staffId)
            .and(ActivityAuthorizationSpecification.withRight(right)));
  }

  @Override
  public void deleteAllByActivityId(UUID activityId) {
    jpaRepository.deleteAllByActivityId(activityId);
  }
}
