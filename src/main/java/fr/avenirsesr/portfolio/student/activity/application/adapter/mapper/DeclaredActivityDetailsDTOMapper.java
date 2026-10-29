package fr.avenirsesr.portfolio.student.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper.ActivityContentDtoMapper;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAssessment;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.ReceivedAssessmentDTO;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivityAssessment;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {
      ActivityContentDtoMapper.class,
      FeedbackOverviewDTOMapper.class,
      OptionalMapper.class,
    })
public interface DeclaredActivityDetailsDTOMapper {

  @Mapping(source = "data.declaredActivity.id", target = "id")
  @Mapping(source = "data.declaredActivity.activity", target = "activity")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "data.declaredActivity.reflection", target = "reflection")
  @Mapping(source = "data.declaredActivity.startDate", target = "startDate")
  @Mapping(source = "data.declaredActivity.endDate", target = "endDate")
  @Mapping(source = "data.declaredActivity.finishedAt", target = "finishedAt")
  @Mapping(source = "data.declaredActivity.valorized", target = "valorized")
  @Mapping(source = "data.declaredActivity.createdAt", target = "createdAt")
  @Mapping(source = "data.declaredActivity.updatedAt", target = "updatedAt")
  @Mapping(
      target = "assessment",
      expression = "java(toReceivedAssessment(data.declaredActivity()))")
  DeclaredActivityDetailsDTO toDTO(
      DeclaredActivityDetailsData data, EDeclaredActivityStatus status);

  @Mapping(source = "data.declaredActivity.id", target = "id")
  @Mapping(
      target = "activity",
      expression =
          "java(activityContentDtoMapper.toDTO(data.declaredActivity().getActivity(), files))")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "data.declaredActivity.reflection", target = "reflection")
  @Mapping(source = "data.declaredActivity.startDate", target = "startDate")
  @Mapping(source = "data.declaredActivity.endDate", target = "endDate")
  @Mapping(source = "data.declaredActivity.finishedAt", target = "finishedAt")
  @Mapping(source = "data.declaredActivity.valorized", target = "valorized")
  @Mapping(source = "data.declaredActivity.createdAt", target = "createdAt")
  @Mapping(source = "data.declaredActivity.updatedAt", target = "updatedAt")
  @Mapping(
      target = "assessment",
      expression = "java(toReceivedAssessment(data.declaredActivity()))")
  DeclaredActivityDetailsDTO toDTO(
      DeclaredActivityDetailsData data, EDeclaredActivityStatus status, List<FileDTO> files);

  @Mapping(source = "activity", target = "activity")
  @Mapping(source = "valorized", target = "valorized")
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "feedbacks", ignore = true)
  @Mapping(target = "assessment", ignore = true)
  DeclaredActivityDetailsDTO toDTO(DeclaredActivity declaredActivity);

  UserInfoDTO toUserInfoDTO(User user);

  /**
   * The assessment is exposed as soon as the student submits the activity for assessment, but its
   * content stays hidden until the staff sends it.
   */
  default ReceivedAssessmentDTO toReceivedAssessment(DeclaredActivity declaredActivity) {
    return declaredActivity
        .getAssessment()
        .map(
            assessment ->
                assessment.isSent()
                    ? toSentAssessment(declaredActivity, assessment)
                    : new ReceivedAssessmentDTO(
                        null, null, null, null, assessment.requestedAt(), null, null, null))
        .orElse(null);
  }

  private ReceivedAssessmentDTO toSentAssessment(
      DeclaredActivity declaredActivity, DeclaredActivityAssessment assessment) {
    return new ReceivedAssessmentDTO(
        assessment.grade(),
        declaredActivity.getActivity().getAssessment().map(ActivityAssessment::grade).orElse(null),
        assessment.validation(),
        assessment.comment(),
        assessment.requestedAt(),
        assessment.assessedAt(),
        assessment.modifiedAt(),
        assessment.assessedBy() == null ? null : toUserInfoDTO(assessment.assessedBy().getUser()));
  }

  default Instant unwrap(Optional<Instant> value) {
    return value == null ? null : value.orElse(null);
  }
}
