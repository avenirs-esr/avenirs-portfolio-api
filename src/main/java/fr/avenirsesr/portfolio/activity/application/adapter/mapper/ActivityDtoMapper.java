package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityDtoMapper {

  ActivityDTO toDTO(Activity activity);
}
