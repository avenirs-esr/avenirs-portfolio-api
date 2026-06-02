package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityContentDtoMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityDetailsData;
import java.time.Instant;
import java.util.Optional;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ActivityContentDtoMapper.class)
public interface DeclaredActivityDetailsDTOMapper {
  DeclaredActivityDetailsDTO toDTO(DeclaredActivityDetailsData data, @Context String baseUrl);

  default Instant unwrap(Optional<Instant> value) {
    return value == null ? null : value.orElse(null);
  }
}
