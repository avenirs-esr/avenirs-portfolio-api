package fr.avenirsesr.portfolio.student.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.FeedbackOverviewDTO;
import fr.avenirsesr.portfolio.student.activity.domain.data.FeedbackData;
import fr.avenirsesr.portfolio.student.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedbackOverviewDTOMapper {

  @Mapping(source = "declaredActivity.student.user", target = "student")
  @Mapping(source = "declaredActivity.activity.author.user", target = "staff")
  FeedbackOverviewDTO toDTO(Feedback feedback);

  @Mapping(source = "declaredActivity.student.user", target = "student")
  @Mapping(source = "declaredActivity.activity.author.user", target = "staff")
  FeedbackOverviewDTO toDTO(FeedbackData feedbackData);

  UserInfoDTO toUserInfoDTO(User user);

  default String unwrap(Optional<String> value) {
    return value == null ? null : value.orElse(null);
  }
}
