package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityPresentationDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityPresentationDtoMapper {
  default ActivityPresentationDTO toDTO(
      ActivityPresentationData activityData, String baseUrl, List<FileDTO> files) {
    var banner = activityData.banner();
    return new ActivityPresentationDTO(
        activityData.id(),
        activityData.title(),
        activityData.thematic(),
        activityData.subscribedDeclaredActivity().orElse(null),
        new FileDTO(banner.id().orElse(null), banner.name().orElse(null), baseUrl + banner.url()),
        activityData.summary(),
        activityData.description(),
        activityData.executionPeriodInfo(),
        files,
        activityData.links(),
        activityData.createdAt(),
        activityData.updatedAt());
  }
}
