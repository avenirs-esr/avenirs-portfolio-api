package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityDtoMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import java.time.Instant;
import java.util.Optional;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ActivityDtoMapper.class)
public interface DeclaredActivityDetailsDTOMapper {

  DeclaredActivityDetailsDTO toDTO(DeclaredActivity declaredActivity);

  default Instant unwrap(Optional<Instant> value) {
    return value == null ? null : value.orElse(null);
  }
}
