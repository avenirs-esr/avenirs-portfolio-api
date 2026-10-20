package fr.avenirsesr.portfolio.authorization.application.adapter.mapper;

import fr.avenirsesr.portfolio.authorization.application.adapter.dto.EntityAuthorizationsDTO;
import fr.avenirsesr.portfolio.authorization.domain.data.EntityAuthorizationsData;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EntityAuthorizationDtoMapper {
  default EntityAuthorizationsDTO toDTO(EntityAuthorizationsData data) {
    return new EntityAuthorizationsDTO(
        data.entityId(), data.authorizations().stream().sorted().toList());
  }

  List<EntityAuthorizationsDTO> toDTOs(List<EntityAuthorizationsData> data);
}
