package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.InactiveStudentDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.data.InactiveStudentData;
import fr.avenirsesr.portfolio.user.application.adapter.mapper.StudentInfoDTOMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {StudentInfoDTOMapper.class})
public interface InactiveStudentDtoMapper {
  InactiveStudentDTO toDTO(InactiveStudentData inactiveStudent);
}
