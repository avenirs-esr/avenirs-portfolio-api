package fr.avenirsesr.portfolio.authorization.application.adapter.mapper;

import fr.avenirsesr.portfolio.authorization.application.adapter.dto.UserEntityAuthorizationsDTO;
import fr.avenirsesr.portfolio.authorization.domain.data.UserEntityAuthorizationsData;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityAuthorizationDtoMapper {
  default UserEntityAuthorizationsDTO toDTO(UserEntityAuthorizationsData data) {
    return new UserEntityAuthorizationsDTO(
        data.entityId(), data.entityName(), data.authorizations().stream().sorted().toList());
  }

  List<UserEntityAuthorizationsDTO> toDTOs(List<UserEntityAuthorizationsData> data);
}
