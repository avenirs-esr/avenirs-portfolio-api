package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceViewDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TraceViewData;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TraceViewMapper {

  TraceViewDTO toDTO(TraceViewData trace);

  List<TraceViewDTO> toDTOs(List<TraceViewData> traces);

  default LocalDate unwrap(Optional<LocalDate> value) {
    return value.orElse(null);
  }
}
