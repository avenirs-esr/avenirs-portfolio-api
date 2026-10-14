package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.StaffInfoDTO;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StaffInfoDtoMapper {
  default StaffInfoDTO toDTO(Staff staff) {
    if (staff == null) {
      return null;
    }
    return new StaffInfoDTO(
        staff.getId(), staff.getUser().getFirstName(), staff.getUser().getLastName());
  }
}
