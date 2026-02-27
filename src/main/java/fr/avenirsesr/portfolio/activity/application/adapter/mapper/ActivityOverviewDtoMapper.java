package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;

public interface ActivityOverviewDtoMapper {
  static ActivityOverviewDTO toDTO(ActivityWithStudentStatusData activityStatus) {
    return new ActivityOverviewDTO(
        activityStatus.activity().getId(),
        activityStatus.activity().getTitle(),
        activityStatus.activity().getThematic(),
        activityStatus.status(),
        activityStatus.activity().getSummary(),
        activityStatus.activity().getExecutionPeriodInfoSummary().orElse(null),
        activityStatus.isNew());
  }
}
