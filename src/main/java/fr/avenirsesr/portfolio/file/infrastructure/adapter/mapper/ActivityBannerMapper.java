package fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper.ActivityMapper;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.file.domain.model.ActivityBanner;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.ActivityBannerEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;

public class ActivityBannerMapper implements Mapper<ActivityBannerEntity, ActivityBanner> {
  public static ActivityBannerMapper INSTANCE = new ActivityBannerMapper();

  @Override
  public ActivityBannerEntity fromDomain(ActivityBanner domain) {
    return ActivityBannerEntity.of(
        domain.getId(),
        ActivityMapper.INSTANCE.fromDomain(domain.getActivity()),
        domain.getFileName(),
        domain.getFileType(),
        domain.getSize(),
        domain.getVersion(),
        domain.isActiveVersion(),
        domain.getUri(),
        UserMapper.INSTANCE.fromDomain(domain.getUploadedBy()),
        domain.getUploadedAt());
  }

  @Override
  public ActivityBanner toDomain(ActivityBannerEntity entity) {
    return ActivityBanner.toDomain(
        entity.getId(),
        entity.getFileName(),
        entity.getFileType(),
        entity.getSize(),
        entity.getVersion(),
        entity.isActiveVersion(),
        entity.getUri(),
        UserMapper.INSTANCE.toDomain(entity.getUploadedBy()),
        entity.getUploadedAt(),
        ActivityMapper.INSTANCE.toDomain(entity.getActivity()),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public ActivityBanner toDomain(ActivityBannerEntity entity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return ActivityBanner.toDomain(
        entity.getId(),
        entity.getFileName(),
        entity.getFileType(),
        entity.getSize(),
        entity.getVersion(),
        entity.isActiveVersion(),
        entity.getUri(),
        attributes.contains("uploadedBy")
            ? UserMapper.INSTANCE.toDomain(entity.getUploadedBy())
            : null,
        entity.getUploadedAt(),
        attributes.contains("activity")
            ? ActivityMapper.INSTANCE.toDomain(entity.getActivity())
            : null,
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
