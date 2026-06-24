package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityContentDtoMapper;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.FeedbackDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.FeedbackData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceDetailMapper;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {
      OptionalMapper.class,
      TraceDetailMapper.class,
      DeclaredSkillProgressMapper.class,
      ActivityContentDtoMapper.class
    })
public interface FeedbackDetailsDTOMapper {

  @Mapping(source = "declaredActivity.activity", target = "activity")
  @Mapping(source = "declaredActivity.student.user", target = "student")
  FeedbackDetailsDTO toDTO(Feedback feedback);

  @Mapping(source = "declaredActivity.activity", target = "activity")
  @Mapping(source = "declaredActivity.student.user", target = "student")
  FeedbackDetailsDTO toDTO(FeedbackData feedbackData);

  UserInfoDTO toUserInfoDTO(User user);
}
