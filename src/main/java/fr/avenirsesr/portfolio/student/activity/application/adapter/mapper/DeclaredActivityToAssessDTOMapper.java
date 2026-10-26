package fr.avenirsesr.portfolio.student.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityAssessmentDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityPreviewDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAssessment;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.DeclaredActivityToAssessDTO;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityToAssessData;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import java.time.Instant;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeclaredActivityToAssessDTOMapper {

  @Mapping(source = "declaredActivity.id", target = "id")
  @Mapping(source = "declaredActivity.student.user", target = "student")
  @Mapping(source = "declaredActivity.activity", target = "activity")
  @Mapping(source = "declaredActivity.activity.assessment", target = "assessment")
  @Mapping(source = "declaredActivity.submittedAt", target = "submittedAt")
  @Mapping(
      target = "assessmentRequestedAt",
      expression =
          "java(declaredActivityToAssess.declaredActivity().getAssessment()"
              + ".map(value -> value.requestedAt()).orElse(null))")
  @Mapping(
      target = "assessedAt",
      expression =
          "java(declaredActivityToAssess.declaredActivity().getAssessment()"
              + ".map(value -> value.assessedAt()).orElse(null))")
  @Mapping(
      target = "assessedBy",
      expression =
          "java(declaredActivityToAssess.declaredActivity().getAssessment()"
              + ".map(value -> value.assessedBy())"
              + ".map(assessor -> toUserInfoDTO(assessor.getUser())).orElse(null))")
  DeclaredActivityToAssessDTO toDTO(DeclaredActivityToAssessData declaredActivityToAssess);

  UserInfoDTO toUserInfoDTO(User user);

  ActivityPreviewDTO toActivityPreviewDTO(Activity activity);

  default ActivityAssessmentDTO toAssessmentDTO(Optional<ActivityAssessment> assessment) {
    return assessment == null
        ? null
        : assessment
            .map(
                value ->
                    new ActivityAssessmentDTO(value.comment(), value.grade(), value.validation()))
            .orElse(null);
  }

  default Instant unwrap(Optional<Instant> value) {
    return value == null ? null : value.orElse(null);
  }
}
