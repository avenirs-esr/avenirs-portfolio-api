package fr.avenirsesr.portfolio.student.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper.ActivityContentDtoMapper;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.FeedbackStaffListItemDTO;
import fr.avenirsesr.portfolio.student.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {ActivityContentDtoMapper.class})
public interface FeedbackStaffListItemDTOMapper {

  @Mapping(source = "feedback.declaredActivity.student.user", target = "student")
  @Mapping(source = "feedback.declaredActivity.activity", target = "activity")
  @Mapping(source = "latestFeedbackId", target = "latestFeedbackId")
  FeedbackStaffListItemDTO toDTO(Feedback feedback, UUID latestFeedbackId);

  UserInfoDTO toUserInfoDTO(User user);
}
