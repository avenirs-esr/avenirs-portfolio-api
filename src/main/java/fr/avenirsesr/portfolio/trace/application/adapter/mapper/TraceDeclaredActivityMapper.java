package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.mapper.FileDtoMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceDeclaredActivityDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TraceDeclaredActivityData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = FileDtoMapper.class)
public interface TraceDeclaredActivityMapper {

  TraceDeclaredActivityDTO toDTO(TraceDeclaredActivityData traceDeclaredActivityData);
}
