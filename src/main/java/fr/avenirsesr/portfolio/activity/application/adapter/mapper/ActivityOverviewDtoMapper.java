package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityOverviewDtoMapper {

  @Mapping(source = "activity.id", target = "id")
  @Mapping(source = "activity.title", target = "title")
  @Mapping(source = "activity.thematic", target = "thematic")
  @Mapping(source = "activity.summary", target = "summary")
  @Mapping(source = "activity.executionPeriodInfoSummary", target = "executionPeriodInfoSummary")
  ActivityOverviewDTO toDTO(ActivityWithStudentStatusData activityStatus);

  default String unwrap(Optional<String> value) {
    return value == null ? null : value.orElse(null);
  }
}
