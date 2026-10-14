package fr.avenirsesr.portfolio.student.association.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationLimitDTO;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationLimitData;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssociationLimitDTOMapper {

  @Mapping(target = "remaining", expression = "java(associationLimitData.remaining())")
  AssociationLimitDTO toDTO(AssociationLimitData associationLimitData);

  List<AssociationLimitDTO> toDTOs(List<AssociationLimitData> associationLimitData);
}
