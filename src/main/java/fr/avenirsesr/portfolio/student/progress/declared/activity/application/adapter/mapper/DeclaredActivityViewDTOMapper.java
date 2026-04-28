package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeclaredActivityViewDTOMapper {

  @Mapping(source = "activity.id", target = "activityId")
  @Mapping(source = "activity.title", target = "title")
  @Mapping(source = "activity.thematic", target = "thematic")
  @Mapping(source = "activity.summary", target = "summary")
  @Mapping(source = "activity.description", target = "description")
  @Mapping(source = "activity.executionPeriodInfoSummary", target = "executionPeriodInfoSummary")
  DeclaredActivityViewDTO toDTO(DeclaredActivity declaredActivity);

  default String unwrap(Optional<String> value) {
    return value == null ? null : value.orElse(null);
  }
}
