package fr.avenirsesr.portfolio.student.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.DeclaredActivityAssociationDTO;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.DeclaredActivityViewDTO;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeclaredActivityAssociationMapper {

  @Mapping(
      target = "declaredActivity",
      expression = "java(toDeclaredActivityViewDTO(data.declaredActivity(), data.status()))")
  DeclaredActivityAssociationDTO toDTO(DeclaredActivityAssociationData data);

  default DeclaredActivityViewDTO toDeclaredActivityViewDTO(
      DeclaredActivity declaredActivity, EDeclaredActivityStatus status) {
    return new DeclaredActivityViewDTO(
        declaredActivity.getId(),
        declaredActivity.getActivity().getId(),
        declaredActivity.getActivity().getTitle(),
        declaredActivity.getActivity().getThematic(),
        declaredActivity.getActivity().getSummary(),
        declaredActivity.getActivity().getDescription(),
        declaredActivity.getActivity().getStartDate().orElse(null),
        declaredActivity.getActivity().getEndDate().orElse(null),
        status,
        declaredActivity.getStartDate(),
        declaredActivity.getEndDate(),
        declaredActivity.isValorized(),
        declaredActivity.getUpdatedAt());
  }
}
