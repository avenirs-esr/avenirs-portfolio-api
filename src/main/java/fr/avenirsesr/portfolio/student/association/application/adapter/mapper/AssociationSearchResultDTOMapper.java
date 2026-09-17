package fr.avenirsesr.portfolio.student.association.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDTO;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssociationSearchResultDTOMapper {

  AssociationSearchResultDTO toDTO(AssociationSearchResultData associationSearchResultData);
}
