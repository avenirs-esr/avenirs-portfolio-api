package fr.avenirsesr.portfolio.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public record ActivitiesByThematicDTO(
    Map<EActivityThematic, List<ActivityOverviewDTO>> activities) {
  public ActivitiesByThematicDTO {
    activities =
        activities == null ? new EnumMap<>(EActivityThematic.class) : new EnumMap<>(activities);
  }
}
