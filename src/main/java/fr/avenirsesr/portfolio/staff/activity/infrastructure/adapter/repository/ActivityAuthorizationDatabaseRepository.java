package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorizationSummary;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityAuthorizationRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper.ActivityAuthorizationMapper;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityAuthorizationEntity;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.specification.ActivityAuthorizationSpecification;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.specification.ActivitySpecification;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityAuthorizationDatabaseRepository
    extends GenericJpaRepositoryAdapter<ActivityAuthorization, ActivityAuthorizationEntity>
    implements ActivityAuthorizationRepository {
  private final ActivityAuthorizationJpaRepository jpaRepository;
  private final ActivityJpaRepository activityJpaRepository;

  protected ActivityAuthorizationDatabaseRepository(
      ActivityAuthorizationJpaRepository jpaRepository,
      ActivityJpaRepository activityJpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        ActivityAuthorizationEntity.class,
        ActivityAuthorizationMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
    this.activityJpaRepository = activityJpaRepository;
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

  @Override
  public boolean existsByStaffIdAndTargetInstitutionIds(
      UUID staffId, Collection<UUID> institutionIds) {
    return !institutionIds.isEmpty()
        && activityJpaRepository.exists(visibleToStaff(staffId, institutionIds));
  }

  @Override
  public PagedResult<ActivityAuthorizationSummary> findPageByStaffIdAndTargetInstitutionIds(
      UUID staffId, Collection<UUID> institutionIds, PageCriteria pageCriteria) {
    var pageRequest =
        PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), Sort.by("title", "id"));
    if (institutionIds.isEmpty()) {
      return new PagedResult<>(
          List.of(), new PageInfo(pageRequest.getPageNumber(), pageRequest.getPageSize(), 0));
    }
    Page<ActivityEntity> page =
        activityJpaRepository.findAll(visibleToStaff(staffId, institutionIds), pageRequest);
    Map<UUID, Set<EActivityRight>> rightsByActivityId =
        findAllByActivityIdsAndStaffId(
                page.getContent().stream().map(ActivityEntity::getId).toList(), staffId)
            .stream()
            .collect(
                Collectors.toMap(
                    ActivityAuthorization::getActivityId, ActivityAuthorization::getRights));
    return new PagedResult<>(
        page.getContent().stream()
            .map(
                row ->
                    new ActivityAuthorizationSummary(
                        row.getId(),
                        row.getTitle(),
                        rightsByActivityId.getOrDefault(row.getId(), Set.of())))
            .toList(),
        new PageInfo(
            pageRequest.getPageNumber(), pageRequest.getPageSize(), page.getTotalElements()));
  }

  private static Specification<ActivityEntity> visibleToStaff(
      UUID staffId, Collection<UUID> institutionIds) {
    return ActivitySpecification.withAuthorizationOfStaff(staffId)
        .and(ActivitySpecification.targetingAnyInstitution(institutionIds));
  }
}
