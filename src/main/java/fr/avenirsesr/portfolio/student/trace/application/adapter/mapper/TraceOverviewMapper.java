package fr.avenirsesr.portfolio.student.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceOverviewDTO;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {OptionalMapper.class})
public interface TraceOverviewMapper {

  @Mapping(source = "aiUseJustification", target = "aiUseJustification")
  TraceOverviewDTO toDTO(Trace trace);
}
