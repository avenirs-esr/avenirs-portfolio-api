package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityItemNavigationDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;

public interface ActivityItemNavigationMapper {
  static ActivityItemNavigationDTO toDTO(Activity activity) {
    return new ActivityItemNavigationDTO(activity.getId(), activity.getTitle());
  }
}
