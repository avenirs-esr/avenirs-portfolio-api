package fr.avenirsesr.portfolio.student.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceLockedDeclaredActivitiesDTO;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceLockedDeclaredActivitiesData;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TraceLockedDeclaredActivitiesMapper {
  TraceLockedDeclaredActivitiesDTO toDTO(
      TraceLockedDeclaredActivitiesData traceLockedDeclaredActivitiesData);

  List<TraceLockedDeclaredActivitiesDTO> toDTOs(
      List<TraceLockedDeclaredActivitiesData> traceLockedDeclaredActivitiesData);
}
