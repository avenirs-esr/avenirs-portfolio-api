package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityPresentationDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityDetailData;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityPresentationDtoMapper {
  default ActivityPresentationDTO toDTO(ActivityDetailData activityDetail, String baseUrl) {
    return new ActivityPresentationDTO(
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
