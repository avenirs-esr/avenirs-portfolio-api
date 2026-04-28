package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityItemNavigationDTO;
import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityNavigationDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ActivityItemNavigationMapper.class)
public interface ActivityNavigationMapper {

  ActivityItemNavigationDTO toItemDTO(Activity activity);

  default List<ActivityNavigationDTO> toDTO(
      Map<EActivityThematic, List<Activity>> activitiesByThematic) {

    if (activitiesByThematic == null || activitiesByThematic.isEmpty()) {
      return List.of();
    }

    return activitiesByThematic.entrySet().stream()
        .map(
            entry ->
                new ActivityNavigationDTO(
                    entry.getKey().name(), entry.getValue().stream().map(this::toItemDTO).toList()))
        .toList();
  }
}
