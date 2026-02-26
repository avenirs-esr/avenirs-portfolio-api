package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityDtoMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;

public interface DeclaredActivityDetailsDTOMapper {
  static DeclaredActivityDetailsDTO toDTO(DeclaredActivity declaredActivity) {
    return new DeclaredActivityDetailsDTO(
        declaredActivity.getId(),
        ActivityDtoMapper.toDTO(declaredActivity.getActivity()),
        declaredActivity.getStatus(),
        declaredActivity.getReflection(),
        declaredActivity.getStartDate(),
        declaredActivity.getEndDate(),
        declaredActivity.getFinishedAt().orElse(null),
        declaredActivity.getCreatedAt(),
        declaredActivity.getUpdatedAt());
  }
}
