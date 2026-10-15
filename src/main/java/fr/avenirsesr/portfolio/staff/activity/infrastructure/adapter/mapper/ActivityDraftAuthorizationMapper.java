package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityDraftAuthorizationEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;

public class ActivityDraftAuthorizationMapper
    implements Mapper<ActivityDraftAuthorizationEntity, ActivityAuthorization> {
  public static final ActivityDraftAuthorizationMapper INSTANCE =
      new ActivityDraftAuthorizationMapper();

  @Override
  public ActivityDraftAuthorizationEntity fromDomain(ActivityAuthorization domain) {
    return ActivityDraftAuthorizationEntity.of(
        domain.getId(),
        domain.getActivityId(),
        StaffMapper.INSTANCE.fromDomain(domain.getStaff()),
        domain.getRights(),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }

  @Override
  public ActivityAuthorization toDomain(ActivityDraftAuthorizationEntity entity) {
    return ActivityAuthorization.toDomain(
        entity.getId(),
        entity.getActivityDraftId(),
        StaffMapper.INSTANCE.toDomain(entity.getStaff()),
        entity.getRights(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
