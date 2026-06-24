package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityContentDtoMapper;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.FeedbackStaffListItemDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {ActivityContentDtoMapper.class})
public interface FeedbackStaffListItemDTOMapper {

  @Mapping(source = "declaredActivity.student.user", target = "student")
  @Mapping(source = "declaredActivity.activity", target = "activity")
  FeedbackStaffListItemDTO toDTO(Feedback feedback);

  UserInfoDTO toUserInfoDTO(User user);
}
