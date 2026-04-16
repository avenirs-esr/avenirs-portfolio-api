package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;

public interface DeclaredActivityViewDTOMapper {
  static DeclaredActivityViewDTO toDTO(DeclaredActivity declaredActivity) {
    return new DeclaredActivityViewDTO(
        declaredActivity.getId(),
        declaredActivity.getActivity().getId(),
        declaredActivity.getActivity().getTitle(),
        declaredActivity.getActivity().getThematic(),
        declaredActivity.getActivity().getSummary(),
        declaredActivity.getActivity().getDescription(),
        declaredActivity.getActivity().getExecutionPeriodInfoSummary().orElse(null),
        declaredActivity.getStatus(),
        declaredActivity.getStartDate(),
        declaredActivity.getEndDate(),
        declaredActivity.getUpdatedAt());
  }
}
