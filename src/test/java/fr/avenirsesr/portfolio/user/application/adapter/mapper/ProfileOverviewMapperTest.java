package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.user.application.adapter.dto.ProfileOverviewDTO;
import fr.avenirsesr.portfolio.user.domain.data.UserPhotosData;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ProfileOverviewMapperTest {

  private final ProfileOverviewMapper mapper = Mappers.getMapper(ProfileOverviewMapper.class);

  @Test
  void shouldMapUserProfileOverviewDataToDTO() {
    BddLogger.given("a user profile overview data and user photos data");
    UserProfileOverviewData overview =
        new UserProfileOverviewData("Jane", "Doe", "jane@example.com", "My bio");
    UUID profileFileId = UUID.randomUUID();
    UUID coverFileId = UUID.randomUUID();
    UserPhotosData photos =
        new UserPhotosData(
            Optional.of(profileFileId),
            Optional.of("profile.jpg"),
            "https://cdn.example.com/profile.jpg",
            Optional.of(coverFileId),
            Optional.of("cover.jpg"),
            "https://cdn.example.com/cover.jpg");

    BddLogger.when("mapping to ProfileOverviewDTO");
    ProfileOverviewDTO dto = mapper.userDomainToDto(overview, photos);

    BddLogger.then("it should map all fields including file DTOs");
    assertNotNull(dto);
    assertEquals("Jane", dto.firstname());
    assertEquals("Doe", dto.lastname());
    assertEquals("jane@example.com", dto.email());
    assertEquals("My bio", dto.bio());
    assertNotNull(dto.profilePicture());
    assertEquals(profileFileId, dto.profilePicture().fileId());
    assertEquals("profile.jpg", dto.profilePicture().fileName());
    assertNotNull(dto.coverPicture());
    assertEquals(coverFileId, dto.coverPicture().fileId());
  }

  @Test
  void shouldHandleEmptyOptionalPhotoIds() {
    BddLogger.given("a user profile with no photo IDs");
    UserProfileOverviewData overview =
        new UserProfileOverviewData("John", "Smith", "john@example.com", null);
    UserPhotosData photos =
        new UserPhotosData(
            Optional.empty(),
            Optional.empty(),
            "https://cdn.example.com/default.jpg",
            Optional.empty(),
            Optional.empty(),
            "https://cdn.example.com/default-cover.jpg");

    BddLogger.when("mapping to ProfileOverviewDTO");
    ProfileOverviewDTO dto = mapper.userDomainToDto(overview, photos);

    BddLogger.then("it should return null for optional file IDs and names");
    assertNotNull(dto);
    assertNotNull(dto.profilePicture());
    assertNull(dto.profilePicture().fileId());
    assertNull(dto.profilePicture().fileName());
    assertEquals("https://cdn.example.com/default.jpg", dto.profilePicture().url());
  }
}
