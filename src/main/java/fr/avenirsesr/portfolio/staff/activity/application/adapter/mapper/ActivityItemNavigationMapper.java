package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityItemNavigationDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityItemNavigationMapper {
  ActivityItemNavigationDTO toDTO(Activity activity);
}
