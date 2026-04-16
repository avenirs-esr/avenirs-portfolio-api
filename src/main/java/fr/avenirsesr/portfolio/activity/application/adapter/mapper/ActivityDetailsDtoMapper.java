package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityDetailsDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityDetailData;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;

public interface ActivityDetailsDtoMapper {
  static ActivityDetailsDTO toDTO(ActivityDetailData activityDetail, String baseUrl) {
    return new ActivityDetailsDTO(
        activityDetail.id(),
        activityDetail.title(),
        activityDetail.thematic(),
        activityDetail.subscribedDeclaredActivity().orElse(null),
        new FileDTO(
            activityDetail.activityBanner().id().orElse(null),
            activityDetail.activityBanner().name().orElse(null),
            baseUrl + activityDetail.activityBanner().url()),
        activityDetail.summary(),
        activityDetail.description(),
        activityDetail.executionPeriodInfo(),
        activityDetail.createdAt(),
        activityDetail.updatedAt());
  }
}
