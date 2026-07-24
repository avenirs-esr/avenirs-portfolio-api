package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityContentDtoMapper;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
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
  DeclaredActivityDetailsDTO toDTO(
      DeclaredActivityDetailsData data, EDeclaredActivityStatus status, List<FileDTO> files);

  @Mapping(source = "activity", target = "activity")
  @Mapping(source = "valorized", target = "valorized")
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "feedbacks", ignore = true)
  DeclaredActivityDetailsDTO toDTO(DeclaredActivity declaredActivity);

  default Instant unwrap(Optional<Instant> value) {
    return value == null ? null : value.orElse(null);
  }
}
