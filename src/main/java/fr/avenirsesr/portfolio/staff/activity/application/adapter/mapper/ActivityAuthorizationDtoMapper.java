package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityAuthorizationDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.request.ActivityAuthorizationsRequest;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.request.StaffAuthorizationRequest;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityAuthorizationDefinition;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAuthorization;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {StaffInfoDtoMapper.class})
public interface ActivityAuthorizationDtoMapper {
  ActivityAuthorizationDTO toDTO(ActivityAuthorization authorization);

  List<ActivityAuthorizationDTO> toDTOs(List<ActivityAuthorization> authorizations);

  default List<ActivityAuthorizationDefinition> toDefinitions(
      ActivityAuthorizationsRequest request) {
    if (request == null || request.authorizations() == null) {
      return List.of();
    }
    return request.authorizations().stream()
        .filter(Objects::nonNull)
        .map(this::toDefinition)
        .toList();
  }

  default ActivityAuthorizationDefinition toDefinition(StaffAuthorizationRequest request) {
    var rights = EnumSet.noneOf(EActivityRight.class);
    if (request.rights() != null) {
      request.rights().stream().filter(Objects::nonNull).forEach(rights::add);
    }
    return new ActivityAuthorizationDefinition(request.staffId(), rights);
  }
}
