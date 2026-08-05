package fr.avenirsesr.portfolio.student.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TracesSummaryDTO;
import fr.avenirsesr.portfolio.student.trace.domain.data.TracesSummaryData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TracesSummaryMapper {

  TracesSummaryDTO toDTO(TracesSummaryData tracesSummary);
}
