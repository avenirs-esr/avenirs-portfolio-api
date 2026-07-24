package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {OptionalMapper.class})
public interface DeclaredActivityViewDTOMapper {

  @Mapping(source = "declaredActivity.activity.id", target = "activityId")
  @Mapping(source = "declaredActivity.activity.title", target = "title")
  @Mapping(source = "declaredActivity.activity.thematic", target = "thematic")
  @Mapping(source = "declaredActivity.activity.summary", target = "summary")
  @Mapping(source = "declaredActivity.activity.description", target = "description")
  @Mapping(
      source = "declaredActivity.activity.executionPeriodInfoSummary",
      target = "executionPeriodInfoSummary")
  @Mapping(source = "declaredActivity.activity.startDate", target = "optionalStartDate")
  @Mapping(source = "declaredActivity.activity.endDate", target = "optionalEndDate")
  @Mapping(source = "declaredActivity.startDate", target = "startDate")
  @Mapping(source = "declaredActivity.endDate", target = "endDate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "declaredActivity.valorized", target = "valorized")
  DeclaredActivityViewDTO toDTO(DeclaredActivity declaredActivity, EDeclaredActivityStatus status);
}
