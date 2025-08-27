package fr.avenirsesr.portfolio.file.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.UuidV7Generator;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserResourceServiceImplTest {
  @InjectMocks private UserResourceServiceImpl service;
  @Mock private UserPhotoRepository userPhotoRepository;
  @Mock private FileStorageService fileStorageService;
  @Spy private UuidGenerator uuidGenerator = new UuidV7Generator();

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
            uuidGenerator.generate(),
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

  @Test
  void deletePhoto_shouldDeletePhotoSuccessfully() throws IOException {
    // Given
    var user = student.getUser();
    var photoId = UUID.randomUUID();
    var photo =
        UserPhoto.create(
            photoId,
            EFileType.PNG,
            456,
            1,
            true,
            "uri/to/photo.png",
            user,
            user,
            EUserCategory.STUDENT,
            EUserPhotoType.PROFILE);
    when(userPhotoRepository.findById(photoId)).thenReturn(Optional.of(photo));

    // When
    service.deletePhoto(photoId, user);

    // Then
    verify(fileStorageService).delete(photo.getId());
    verify(userPhotoRepository).removeFromDatabase(photo);
  }

  @Test
  void deletePhoto_shouldThrowFileNotFoundException_whenPhotoDoesNotExist() {
    // Given
    var user = student.getUser();
    var photoId = UUID.randomUUID();
    when(userPhotoRepository.findById(photoId)).thenReturn(Optional.empty());

    // Then
    org.junit.jupiter.api.Assertions.assertThrows(
        FileNotFoundException.class, () -> service.deletePhoto(photoId, user));
  }

  @Test
  void deletePhoto_shouldThrowUserNotAuthorizedException_whenPhotoBelongsToAnotherUser() {
    // Given
    var user = student.getUser();
    var otherUser = UserFixture.createStudent().toModel().toStudent().getUser();
    var photoId = UUID.randomUUID();
    var photo =
        UserPhoto.create(
            photoId,
            EFileType.PNG,
            456,
            1,
            true,
            "uri/to/photo.png",
            otherUser,
            otherUser,
            EUserCategory.STUDENT,
            EUserPhotoType.PROFILE);
    when(userPhotoRepository.findById(photoId)).thenReturn(Optional.of(photo));

    // Then
    org.junit.jupiter.api.Assertions.assertThrows(
        UserNotAuthorizedException.class, () -> service.deletePhoto(photoId, user));
  }

  @Test
  void deletePhoto_shouldWrapIOExceptionInRuntimeException() throws IOException {
    // Given
    var user = student.getUser();
    var photoId = UUID.randomUUID();
    var photo =
        UserPhoto.create(
            photoId,
            EFileType.PNG,
            456,
            1,
            true,
            "uri/to/photo.png",
            user,
            user,
            EUserCategory.STUDENT,
            EUserPhotoType.PROFILE);
    when(userPhotoRepository.findById(photoId)).thenReturn(Optional.of(photo));
    doThrow(new IOException("IO error")).when(fileStorageService).delete(photo.getId());

    // Then
    org.junit.jupiter.api.Assertions.assertThrows(
        RuntimeException.class, () -> service.deletePhoto(photoId, user));
    verify(userPhotoRepository, never()).removeFromDatabase(photo);
  }
}
