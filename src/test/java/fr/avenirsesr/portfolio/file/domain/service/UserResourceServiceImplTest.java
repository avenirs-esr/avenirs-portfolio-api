package fr.avenirsesr.portfolio.file.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserResourceServiceImplTest {
  @InjectMocks private UserResourceServiceImpl service;
  @Mock private UserPhotoRepository userPhotoRepository;
  @Mock private FileStorageService fileStorageService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @Test
  void uploadPhoto_shouldSaveNewPhotoAndReturnIt() throws IOException {
    // Given
    var user = student.getUser();
    var category = EUserCategory.STUDENT;
    var photoType = EUserPhotoType.PROFILE;
    var fileName = "avatar.png";
    var mimeType = "image/png";
    var size = 1234L;
    var content = "fake-image-data".getBytes();

    // Setup mocks
    var expectedUri = "uri/to/avatar.png";
    when(fileStorageService.upload(any(FileResource.class))).thenReturn(expectedUri);

    var existingPhoto =
        UserPhoto.create(
            UUID.randomUUID(),
            EFileType.PNG,
            456,
            1,
            true,
            "old-uri",
            user,
            user,
            category,
            photoType);
    when(userPhotoRepository.findAllByUser(user, category, photoType))
        .thenReturn(List.of(existingPhoto));

    ArgumentCaptor<List<UserPhoto>> captor = ArgumentCaptor.forClass(List.class);

    // When
    UserPhoto result =
        service.uploadPhoto(user, category, photoType, fileName, mimeType, size, content);

    // Then
    verify(userPhotoRepository).saveAll(captor.capture());
    var savedPhotos = captor.getValue();

    assertEquals(2, savedPhotos.size());
    var newPhoto = savedPhotos.get(1);
    assertEquals(2, newPhoto.getVersion());
    assertEquals(true, newPhoto.isActiveVersion());
    assertEquals(expectedUri, newPhoto.getUri());
    assertEquals(result, newPhoto);

    // Old one should be deactivated
    assertEquals(false, savedPhotos.get(0).isActiveVersion());
  }

  @Test
  void uploadPhoto_shouldThrowFileTypeNotSupportedException() {
    // Given
    var user = student.getUser();
    var category = EUserCategory.STUDENT;
    var photoType = EUserPhotoType.PROFILE;
    var fileName = "avatar.tiff"; // Unsupported type
    var mimeType = "image/tiff";
    var size = 1234L;
    var content = "fake-image-data".getBytes();

    // Then
    org.junit.jupiter.api.Assertions.assertThrows(
        FileTypeNotSupportedException.class,
        () -> service.uploadPhoto(user, category, photoType, fileName, mimeType, size, content));
  }

  @Test
  void uploadPhoto_shouldThrowFileSizeTooBigException() {
    // Given
    var user = student.getUser();
    var category = EUserCategory.STUDENT;
    var photoType = EUserPhotoType.PROFILE;
    var fileName = "avatar.png";
    var mimeType = "image/png";
    var size = EFileType.PNG.getSizeLimit().bytes() + 1; // Exceeds limit
    var content = new byte[(int) size];

    // Then
    org.junit.jupiter.api.Assertions.assertThrows(
        FileSizeTooBigException.class,
        () -> service.uploadPhoto(user, category, photoType, fileName, mimeType, size, content));
  }
}
