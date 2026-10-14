package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityStaffOverviewDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityStaffOverviewData;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {StaffInfoDtoMapper.class})
public interface ActivityStaffOverviewDtoMapper {
  ActivityStaffOverviewDTO toDTO(ActivityStaffOverviewData activityData);
}
