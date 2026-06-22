package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.StudentFeedbackItemListDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentFeedbackItemListDTOMapper {

  @Mapping(source = "declaredActivity.student.user", target = "student")
  @Mapping(source = "feedback.id", target = "feedbackId")
  StudentFeedbackItemListDTO toDTO(Feedback feedback);
}
