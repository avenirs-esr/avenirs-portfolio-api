package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityAuthorizationEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;

public class ActivityAuthorizationMapper
    implements Mapper<ActivityAuthorizationEntity, ActivityAuthorization> {
  public static final ActivityAuthorizationMapper INSTANCE = new ActivityAuthorizationMapper();

  @Override
  public ActivityAuthorizationEntity fromDomain(ActivityAuthorization domain) {
    return ActivityAuthorizationEntity.of(
        domain.getId(),
        domain.getActivityId(),
        StaffMapper.INSTANCE.fromDomain(domain.getStaff()),
        domain.getRights(),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }

  @Override
  public ActivityAuthorization toDomain(ActivityAuthorizationEntity entity) {
    return ActivityAuthorization.toDomain(
        entity.getId(),
        entity.getActivityId(),
        StaffMapper.INSTANCE.toDomain(entity.getStaff()),
        entity.getRights(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
