package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.mapper.FileDtoMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceDetailDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TraceDetailData;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = FileDtoMapper.class)
public interface TraceDetailMapper {

  TraceDetailDTO toDTO(TraceDetailData traceDetail);

  @Mapping(target = "aiUseJustification", source = "aiUseJustification")
  @Mapping(target = "personalNote", source = "personalNote")
  @Mapping(target = "link", source = "link")
  TraceDetailDTO toTraceDetailsDTO(Trace trace);

  default String unwrap(Optional<String> value) {
    return value == null ? null : value.orElse(null);
  }
}
