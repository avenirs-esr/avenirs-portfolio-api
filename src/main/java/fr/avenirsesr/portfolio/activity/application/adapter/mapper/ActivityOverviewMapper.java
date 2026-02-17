package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;

public interface ActivityOverviewMapper {
  static ActivityOverviewDTO toActivityOverviewDTO(Activity activity) {
    return new ActivityOverviewDTO(activity.getId(), activity.getTitle());
  }
}
