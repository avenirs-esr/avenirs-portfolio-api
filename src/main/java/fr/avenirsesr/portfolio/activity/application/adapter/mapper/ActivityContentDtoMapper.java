package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import java.util.Optional;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityContentDtoMapper {

  ActivityContentDTO toDTO(Activity activity);

  ActivityContentDTO toDTO(ActivityDraft activity);

  default String unwrapString(Optional<String> value) {
    return value.orElse(null);
  }

  default Integer unwrapInteger(Optional<Integer> value) {
    return value.orElse(null);
  }
}
