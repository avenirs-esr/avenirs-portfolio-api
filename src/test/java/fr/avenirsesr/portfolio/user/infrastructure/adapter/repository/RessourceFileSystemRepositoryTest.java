package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class RessourceFileSystemRepositoryTest {

  @InjectMocks private RessourceFileSystemRepository ressourceFileSystemRepository;

  private void setUpRepositoryValue(String fieldName, String fieldValue) throws Exception {
    Field field = RessourceFileSystemRepository.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(ressourceFileSystemRepository, fieldValue);
  }

  @BeforeEach
  void setUp() throws Exception {
    setUpRepositoryValue("baseUrl", "https://baseUrl.com");
    setUpRepositoryValue("photoStudentPath", "./photo/student");
    setUpRepositoryValue("coverStudentPath", "./cover/student");
    setUpRepositoryValue("photoTeacherPath", "./photo/teacher");
    setUpRepositoryValue("coverTeacherPath", "./cover/teacher");
  }

  @Test
  void shouldStoreStudentProfilePicture() throws IOException {
    // Given
    UUID userId = UUID.randomUUID();
    MultipartFile mockFile = mock(MultipartFile.class);
    Path cheminMock = Path.of("/path/fake");
    Long longMock = 1000L;
    InputStream fakeInputStream = new ByteArrayInputStream("fake data".getBytes());
    Instant fixedInstant = Instant.parse("2025-03-04T12:00:00Z");
    String storedFileName;

    // When
    when(mockFile.getOriginalFilename()).thenReturn("fakeFileName.jpg");
    when(mockFile.getInputStream()).thenReturn(fakeInputStream);

    try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class);
        MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedInstant.when(Instant::now).thenReturn(fixedInstant);
      mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(cheminMock);
      mockedFiles
          .when(() -> Files.copy(nullable(InputStream.class), any(Path.class)))
          .thenReturn(longMock);

      storedFileName = ressourceFileSystemRepository.storeStudentProfilePicture(userId, mockFile);
    }

    // Then
    assertEquals(
        "https://baseUrl.com/photo/student/"
            + userId
            + "_"
            + fixedInstant.toEpochMilli()
            + "_fakeFileName.jpg",
        storedFileName);
  }

  @Test
  void shouldStoreStudentCoverPicture() throws IOException {
    // Given
    UUID userId = UUID.randomUUID();
    MultipartFile mockFile = mock(MultipartFile.class);
    Path cheminMock = Path.of("/path/fake");
    Long longMock = 1000L;
    InputStream fakeInputStream = new ByteArrayInputStream("fake data".getBytes());
    Instant fixedInstant = Instant.parse("2025-03-04T12:00:00Z");
    String storedFileName;

    // When
    when(mockFile.getOriginalFilename()).thenReturn("fakeFileName.jpg");
    when(mockFile.getInputStream()).thenReturn(fakeInputStream);

    try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class);
        MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedInstant.when(Instant::now).thenReturn(fixedInstant);
      mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(cheminMock);
      mockedFiles
          .when(() -> Files.copy(nullable(InputStream.class), any(Path.class)))
          .thenReturn(longMock);

      storedFileName = ressourceFileSystemRepository.storeStudentCoverPicture(userId, mockFile);
    }

    // Then
    assertEquals(
        "https://baseUrl.com/cover/student/"
            + userId
            + "_"
            + fixedInstant.toEpochMilli()
            + "_fakeFileName.jpg",
        storedFileName);
  }

  @Test
  void shouldStoreTeacherProfilePicture() throws IOException {
    // Given
    UUID userId = UUID.randomUUID();
    MultipartFile mockFile = mock(MultipartFile.class);
    Path cheminMock = Path.of("/path/fake");
    Long longMock = 1000L;
    InputStream fakeInputStream = new ByteArrayInputStream("fake data".getBytes());
    Instant fixedInstant = Instant.parse("2025-03-04T12:00:00Z");
    String storedFileName;

    // When
    when(mockFile.getOriginalFilename()).thenReturn("fakeFileName.jpg");
    when(mockFile.getInputStream()).thenReturn(fakeInputStream);

    try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class);
        MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedInstant.when(Instant::now).thenReturn(fixedInstant);
      mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(cheminMock);
      mockedFiles
          .when(() -> Files.copy(nullable(InputStream.class), any(Path.class)))
          .thenReturn(longMock);

      storedFileName = ressourceFileSystemRepository.storeTeacherProfilePicture(userId, mockFile);
    }

    // Then
    assertEquals(
        "https://baseUrl.com/photo/teacher/"
            + userId
            + "_"
            + fixedInstant.toEpochMilli()
            + "_fakeFileName.jpg",
        storedFileName);
  }

  @Test
  void shouldStoreTeacherCoverPicture() throws IOException {
    // Given
    UUID userId = UUID.randomUUID();
    MultipartFile mockFile = mock(MultipartFile.class);
    Path cheminMock = Path.of("/path/fake");
    Long longMock = 1000L;
    InputStream fakeInputStream = new ByteArrayInputStream("fake data".getBytes());
    Instant fixedInstant = Instant.parse("2025-03-04T12:00:00Z");
    String storedFileName;

    // When
    when(mockFile.getOriginalFilename()).thenReturn("fakeFileName.jpg");
    when(mockFile.getInputStream()).thenReturn(fakeInputStream);

    try (MockedStatic<Instant> mockedInstant = mockStatic(Instant.class);
        MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
      mockedInstant.when(Instant::now).thenReturn(fixedInstant);
      mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(cheminMock);
      mockedFiles
          .when(() -> Files.copy(nullable(InputStream.class), any(Path.class)))
          .thenReturn(longMock);

      storedFileName = ressourceFileSystemRepository.storeTeacherCoverPicture(userId, mockFile);
    }

    // Then
    assertEquals(
        "https://baseUrl.com/cover/teacher/"
            + userId
            + "_"
            + fixedInstant.toEpochMilli()
            + "_fakeFileName.jpg",
        storedFileName);
  }
}
