package fr.avenirsesr.portfolio.file.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.UserPhotoRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
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
    BddLogger.given("a UserResourceServiceImpl service");
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
            "filename",
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

    BddLogger.when("uploading a correct photo");
    UserPhoto result =
        service.uploadPhoto(user, category, photoType, fileName, mimeType, size, content);

    BddLogger.then("it should save the new photo and return it");
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
    BddLogger.given("a UserResourceServiceImpl service");
    var user = student.getUser();
    var category = EUserCategory.STUDENT;
    var photoType = EUserPhotoType.PROFILE;
    var fileName = "avatar.tiff"; // Unsupported type
    var mimeType = "image/tiff";
    var size = 1234L;
    var content = "fake-image-data".getBytes();

    BddLogger.when("uploading a photo which file type is not supported");
    BddLogger.then("it should throw a FileTypeNotSupportedException");
    org.junit.jupiter.api.Assertions.assertThrows(
        FileTypeNotSupportedException.class,
        () -> service.uploadPhoto(user, category, photoType, fileName, mimeType, size, content));
  }

  @Test
  void uploadPhoto_shouldThrowFileSizeTooBigException() {
    BddLogger.given("a UserResourceServiceImpl service");
    var user = student.getUser();
    var category = EUserCategory.STUDENT;
    var photoType = EUserPhotoType.PROFILE;
    var fileName = "avatar.png";
    var mimeType = "image/png";
    var size = EFileType.PNG.getSizeLimit().bytes() + 1; // Exceeds limit
    var content = new byte[(int) size];

    BddLogger.when("uploading a photo which file size is too big");
    BddLogger.then("it should throw a FileSizeTooBigException");
    org.junit.jupiter.api.Assertions.assertThrows(
        FileSizeTooBigException.class,
        () -> service.uploadPhoto(user, category, photoType, fileName, mimeType, size, content));
  }

  @Test
  void deletePhoto_shouldDeletePhotoSuccessfully() throws IOException {
    BddLogger.given("a UserResourceServiceImpl service");
    var user = student.getUser();
    var photoId = UUID.randomUUID();
    var photo =
        UserPhoto.create(
            photoId,
            "filename",
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

    BddLogger.when("deleting a photo");
    service.deletePhoto(photoId, user);

    BddLogger.then("it should delete the photo successfully and remove it from database");
    verify(fileStorageService).delete(photo.getId());
    verify(userPhotoRepository).removeFromDatabase(photo);
  }

  @Test
  void deletePhoto_shouldThrowFileNotFoundException_whenPhotoDoesNotExist() {
    BddLogger.given("a UserResourceServiceImpl service");
    var user = student.getUser();
    var photoId = UUID.randomUUID();
    when(userPhotoRepository.findById(photoId)).thenReturn(Optional.empty());

    BddLogger.when("deleting a non existing photo");
    BddLogger.then("it should throw a FileNotFoundException");
    org.junit.jupiter.api.Assertions.assertThrows(
        FileNotFoundException.class, () -> service.deletePhoto(photoId, user));
  }

  @Test
  void deletePhoto_shouldThrowUserNotAuthorizedException_whenPhotoBelongsToAnotherUser() {
    BddLogger.given("a UserResourceServiceImpl service");
    var user = student.getUser();
    var otherUser = UserFixture.createStudent().toModel().toStudent().getUser();
    var photoId = UUID.randomUUID();
    var photo =
        UserPhoto.create(
            photoId,
            "filename",
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

    BddLogger.when("deleting a photo that belongs to another user");
    BddLogger.then("it should throw a UserNotAuthorizedException");
    org.junit.jupiter.api.Assertions.assertThrows(
        UserNotAuthorizedException.class, () -> service.deletePhoto(photoId, user));
  }

  @Test
  void deletePhoto_shouldWrapIOExceptionInRuntimeException() throws IOException {
    BddLogger.given("a UserResourceServiceImpl service");
    var user = student.getUser();
    var photoId = UUID.randomUUID();
    var photo =
        UserPhoto.create(
            photoId,
            "filename",
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

    BddLogger.when("and IO occurs");
    doThrow(new IOException("IO error")).when(fileStorageService).delete(photo.getId());

    BddLogger.then("it should wrap an IOException in RuntimeException");
    org.junit.jupiter.api.Assertions.assertThrows(
        RuntimeException.class, () -> service.deletePhoto(photoId, user));
    verify(userPhotoRepository).removeFromDatabase(photo);
  }
}
