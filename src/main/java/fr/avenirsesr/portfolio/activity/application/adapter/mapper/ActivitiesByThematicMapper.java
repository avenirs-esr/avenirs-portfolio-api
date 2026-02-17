package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivitiesByThematicDTO;
import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ActivitiesByThematicMapper {
  static ActivitiesByThematicDTO toActivitiesByThematicDTO(
      Map<EActivityThematic, List<Activity>> activitiesByThematic) {
    Map<EActivityThematic, List<ActivityOverviewDTO>> dtoMap =
        activitiesByThematic.entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry ->
                        entry.getValue().stream()
                            .map(ActivityOverviewMapper::toActivityOverviewDTO)
                            .toList()));
    return new ActivitiesByThematicDTO(dtoMap);
  }
}
