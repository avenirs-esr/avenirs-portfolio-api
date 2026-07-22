package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceOverviewDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TraceWithProjectNameData;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {OptionalMapper.class})
public interface TraceOverviewMapper {

  @Mapping(source = "trace.id", target = "id")
  @Mapping(source = "trace.aiUseJustification", target = "aiUseJustification")
  TraceOverviewDTO toDTO(Trace trace, String programName);

  default TraceOverviewDTO toDTO(Trace trace) {
    return trace == null ? null : toDTO(trace, null);
  }

  default TraceOverviewDTO toDTO(TraceWithProjectNameData data) {
    return data == null ? null : toDTO(data.trace(), data.programName());
  }
}
