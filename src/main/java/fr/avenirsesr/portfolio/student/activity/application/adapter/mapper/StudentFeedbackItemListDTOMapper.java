package fr.avenirsesr.portfolio.student.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.StudentFeedbackItemListDTO;
import fr.avenirsesr.portfolio.student.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.user.application.adapter.mapper.StudentInfoDTOMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {StudentInfoDTOMapper.class})
public interface StudentFeedbackItemListDTOMapper {

  @Mapping(source = "declaredActivity.student", target = "student")
  @Mapping(source = "feedback.id", target = "feedbackId")
  StudentFeedbackItemListDTO toDTO(Feedback feedback);
}
