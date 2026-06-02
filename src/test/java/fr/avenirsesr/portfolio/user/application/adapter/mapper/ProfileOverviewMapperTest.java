package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.user.application.adapter.dto.ProfileOverviewDTO;
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
    UUID profileFileId = UUID.randomUUID();
    UUID coverFileId = UUID.randomUUID();
    var profile =
        new FileData(Optional.of(profileFileId), Optional.of("profile.jpg"), "profile.jpg");

    var cover = new FileData(Optional.of(coverFileId), Optional.of("cover.jpg"), "cover.jpg");
    UserProfileOverviewData overview =
        new UserProfileOverviewData(
            UUID.randomUUID(), "Jane", "Doe", "jane@example.com", "My bio", cover, profile);

    BddLogger.when("mapping to ProfileOverviewDTO");
    ProfileOverviewDTO dto = mapper.userDomainToDto(overview, "https://cdn.example.com/");

    BddLogger.then("it should map all fields including file DTOs");
    assertNotNull(dto);
    assertEquals("Jane", dto.firstname());
    assertEquals("Doe", dto.lastname());
    assertEquals("jane@example.com", dto.email());
    assertEquals("My bio", dto.bio());
    assertNotNull(dto.profilePicture());
    assertEquals(profileFileId, dto.profilePicture().id());
    assertEquals("profile.jpg", dto.profilePicture().fileName());
    assertNotNull(dto.coverPicture());
    assertEquals(coverFileId, dto.coverPicture().id());
  }

  @Test
  void shouldHandleEmptyOptionalPhotoIds() {
    BddLogger.given("a user profile with no photo IDs");
    var profile = new FileData(Optional.empty(), Optional.empty(), "default.jpg");

    var cover = new FileData(Optional.empty(), Optional.empty(), "default-cover.jpg");
    UserProfileOverviewData overview =
        new UserProfileOverviewData(
            UUID.randomUUID(), "John", "Smith", "john@example.com", null, cover, profile);

    BddLogger.when("mapping to ProfileOverviewDTO");
    ProfileOverviewDTO dto = mapper.userDomainToDto(overview, "https://cdn.example.com/");

    BddLogger.then("it should return null for optional file IDs and names");
    assertNotNull(dto);
    assertNotNull(dto.profilePicture());
    assertNull(dto.profilePicture().id());
    assertNull(dto.profilePicture().fileName());
    assertEquals("https://cdn.example.com/default.jpg", dto.profilePicture().url());
  }
}
