package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.FeedbackDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceDetailMapper;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {TraceDetailMapper.class, DeclaredSkillProgressMapper.class})
public interface FeedbackDetailsDTOMapper {

  @Mapping(source = "declaredActivity.id", target = "declaredActivityId")
  @Mapping(source = "declaredActivity.student.user", target = "student")
  FeedbackDetailsDTO toDTO(Feedback feedback);

  UserInfoDTO toUserInfoDTO(User user);
}
