package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public interface ActivityContentDtoMapper {

  ActivityContentDTO toDTO(Activity activity);

  ActivityContentDTO toDTO(ActivityDraft activity);
}
