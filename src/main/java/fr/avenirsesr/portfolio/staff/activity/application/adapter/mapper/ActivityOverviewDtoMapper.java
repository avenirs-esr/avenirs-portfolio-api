package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityWithStudentStatusData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {OptionalMapper.class, StaffInfoDtoMapper.class})
public interface ActivityOverviewDtoMapper {

  @Mapping(source = "activity.id", target = "id")
  @Mapping(source = "activity.title", target = "title")
  @Mapping(source = "activity.thematic", target = "thematic")
  @Mapping(source = "activity.summary", target = "summary")
  @Mapping(source = "activity.startDate", target = "startDate")
  @Mapping(source = "activity.endDate", target = "endDate")
  @Mapping(source = "activity.author", target = "author")
  ActivityOverviewDTO toDTO(ActivityWithStudentStatusData activityStatus);
}
