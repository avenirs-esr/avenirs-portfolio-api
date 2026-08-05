package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityStaffOverviewDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.AuthorDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityStaffOverviewDtoMapper {
  ActivityStaffOverviewDTO toDTO(ActivityStaffOverviewData activityData);

  default AuthorDTO unwrap(Staff author) {
    return new AuthorDTO(
        author.getId(), author.getUser().getFirstName(), author.getUser().getLastName());
  }
}
