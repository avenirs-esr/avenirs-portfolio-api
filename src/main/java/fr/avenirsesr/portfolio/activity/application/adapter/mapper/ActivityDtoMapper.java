package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;

public interface ActivityDtoMapper {
  static ActivityDTO toDTO(Activity activity) {
    return new ActivityDTO(
        activity.getId(),
        activity.getTitle(),
        activity.getThematic(),
        activity.getSummary(),
        activity.getDescription(),
        activity.getExecutionPeriodInfo(),
        activity.getCreatedAt(),
        activity.getUpdatedAt());
  }
}
