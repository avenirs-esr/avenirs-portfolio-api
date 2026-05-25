package fr.avenirsesr.portfolio.file.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.application.adapter.dto.UserPhotoUploadDTO;
import fr.avenirsesr.portfolio.file.domain.model.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserPhotoUploadDTOMapperTest {

  private final UserPhotoUploadDTOMapper mapper = Mappers.getMapper(UserPhotoUploadDTOMapper.class);

  @Test
  void shouldMapUserPhotoToDTO() {
    BddLogger.given("a user photo");
    User user = UserFixture.create().toModel();
    UUID id = UUID.randomUUID();
    long size = 2048L;
    UserPhoto photo =
        UserPhoto.create(
            id,
            "profile.jpg",
            EFileType.PNG,
            size,
            1,
            true,
            "photos/profile.jpg",
            user,
            user,
            EUserCategory.STUDENT,
            EUserPhotoType.PROFILE);

    BddLogger.when("mapping to UserPhotoUploadDTO");
    UserPhotoUploadDTO dto = mapper.fromDomain(photo);

    BddLogger.then("it should rename size to fileSize and map all fields");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals(EFileType.PNG, dto.fileType());
    assertEquals(size, dto.fileSize());
    assertEquals(1, dto.version());
  }
}
