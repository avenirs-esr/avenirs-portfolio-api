package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityItemNavigationDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public interface ActivityNavigationMapper {
  static Map<EActivityThematic, List<ActivityItemNavigationDTO>> toDTO(
      Map<EActivityThematic, List<Activity>> activitiesByThematic) {
    if (activitiesByThematic == null || activitiesByThematic.isEmpty()) {
      return new EnumMap<>(EActivityThematic.class);
    }

    Map<EActivityThematic, List<ActivityItemNavigationDTO>> result =
        new EnumMap<>(EActivityThematic.class);

    activitiesByThematic.forEach(
        (thematic, activities) -> {
          result.put(
              thematic, activities.stream().map(ActivityItemNavigationMapper::toDTO).toList());
        });

    return result;
  }
}
