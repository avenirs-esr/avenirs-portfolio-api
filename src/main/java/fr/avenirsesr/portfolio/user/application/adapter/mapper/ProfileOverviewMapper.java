package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.user.application.adapter.dto.ProfileOverviewDTO;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileOverviewMapper {

  default ProfileOverviewDTO userDomainToDto(UserProfileOverviewData overview, String baseUrl) {
    return new ProfileOverviewDTO(
        overview.firstName(),
        overview.lastName(),
        overview.bio(),
        overview.email(),
        new FileDTO(
            overview.profilePhoto().id().orElse(null),
            overview.profilePhoto().name().orElse(null),
            baseUrl + overview.profilePhoto().url()),
        new FileDTO(
            overview.coverPhoto().id().orElse(null),
            overview.coverPhoto().name().orElse(null),
            baseUrl + overview.coverPhoto().url()));
  }
}
