package fr.avenirsesr.portfolio.student.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.DeclaredActivityAssessmentDTO;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.DeclaredActivityAssessmentDetailsDTO;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssessmentDetailsData;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivityAssessment;
import fr.avenirsesr.portfolio.student.skill.application.adapter.mapper.SkillProgressMapper;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.TraceDetailMapper;
import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import java.time.Instant;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {OptionalMapper.class, TraceDetailMapper.class, SkillProgressMapper.class})
public interface DeclaredActivityAssessmentDetailsDTOMapper {

  @Mapping(source = "data.declaredActivity.id", target = "declaredActivityId")
  @Mapping(source = "activity", target = "activity")
  @Mapping(source = "data.declaredActivity.student.user", target = "student")
  @Mapping(source = "data.declaredActivity.reflection", target = "reflexion")
  @Mapping(source = "data.associations", target = "associations")
  @Mapping(source = "data.declaredActivity.assessment", target = "assessment")
  @Mapping(source = "data.declaredActivity.submittedAt", target = "submittedAt")
  @Mapping(source = "data.status", target = "status")
  DeclaredActivityAssessmentDetailsDTO toDTO(
      DeclaredActivityAssessmentDetailsData data, ActivityContentDTO activity);

  UserInfoDTO toUserInfoDTO(User user);

  default DeclaredActivityAssessmentDTO toAssessmentDTO(
      Optional<DeclaredActivityAssessment> assessment) {
    return assessment == null
        ? null
        : assessment
            .map(
                value ->
                    new DeclaredActivityAssessmentDTO(
                        value.comment(),
                        value.grade(),
                        value.validation(),
                        value.requestedAt(),
                        value.assessedAt(),
                        value.assessedBy() == null
                            ? null
                            : toUserInfoDTO(value.assessedBy().getUser())))
            .orElse(null);
  }

  default Instant unwrap(Optional<Instant> value) {
    return value == null ? null : value.orElse(null);
  }
}
