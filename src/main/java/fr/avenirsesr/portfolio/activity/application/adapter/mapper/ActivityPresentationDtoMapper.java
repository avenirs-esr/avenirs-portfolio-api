package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityPresentationDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import java.util.Optional;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityPresentationDtoMapper {
  default ActivityPresentationDTO toDTO(
      Activity activity,
      FileData banner,
      Optional<UUID> subscribedDeclaredActivity,
      String baseUrl) {
    return new ActivityPresentationDTO(
        activity.getId(),
        activity.getTitle(),
        activity.getThematic(),
        subscribedDeclaredActivity.orElse(null),
        new FileDTO(banner.id().orElse(null), banner.name().orElse(null), baseUrl + banner.url()),
        activity.getSummary(),
        activity.getDescription(),
        activity.getExecutionPeriodInfo(),
        activity.getCreatedAt(),
        activity.getUpdatedAt());
  }

  default ActivityPresentationDTO toDTO(ActivityDraft draft) {
    return new ActivityPresentationDTO(
        draft.getId(),
        draft.getTitle(),
        draft.getThematic(),
        null,
        null,
        draft.getSummary().orElse(null),
        draft.getDescription().orElse(null),
        draft.getExecutionPeriodInfo().orElse(null),
        draft.getCreatedAt(),
        draft.getUpdatedAt());
  }
}
