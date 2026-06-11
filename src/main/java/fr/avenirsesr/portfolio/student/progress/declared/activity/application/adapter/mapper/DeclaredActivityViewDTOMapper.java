package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeclaredActivityViewDTOMapper {

  @Mapping(source = "declaredActivity.activity.id", target = "activityId")
  @Mapping(source = "declaredActivity.activity.title", target = "title")
  @Mapping(source = "declaredActivity.activity.thematic", target = "thematic")
  @Mapping(source = "declaredActivity.activity.summary", target = "summary")
  @Mapping(source = "declaredActivity.activity.description", target = "description")
  @Mapping(
      source = "declaredActivity.activity.executionPeriodInfoSummary",
      target = "executionPeriodInfoSummary")
  @Mapping(source = "status", target = "status")
  DeclaredActivityViewDTO toDTO(DeclaredActivity declaredActivity, EDeclaredActivityStatus status);

  default String unwrap(Optional<String> value) {
    return value == null ? null : value.orElse(null);
  }
}
