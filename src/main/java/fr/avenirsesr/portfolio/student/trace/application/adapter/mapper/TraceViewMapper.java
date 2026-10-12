package fr.avenirsesr.portfolio.student.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.mapper.FileDtoMapper;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceViewDTO;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceViewData;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {FileDtoMapper.class})
public interface TraceViewMapper {

  TraceViewDTO toDTO(TraceViewData trace);

  List<TraceViewDTO> toDTOs(List<TraceViewData> traces);

  default String unwrapString(Optional<String> value) {
    return value.orElse(null);
  }

  default LocalDate unwrap(Optional<LocalDate> value) {
    return value.orElse(null);
  }
}
