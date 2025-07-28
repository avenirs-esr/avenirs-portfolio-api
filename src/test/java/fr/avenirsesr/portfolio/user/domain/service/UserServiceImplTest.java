package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.shared.domain.exception.BadImageSizeException;
import fr.avenirsesr.portfolio.shared.domain.exception.BadImageTypeException;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.RessourceService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private RessourceService ressourceService;

  @InjectMocks private UserServiceImpl userService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @Test
  void shouldUpdateUserFirstnameLastnameEmailAndBio() {
    // When
    userService.updateProfile(
        student.getUser(),
        "RandomFirstname",
        "RandomLastname",
        "RandomEmail",
        "RandomBio",
        "https://RandomProfilePictureUrl.com",
        "https://RandomCoverPictureUrl.com");

    // Then
    ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
    verify(userRepository).save(captor.capture());

    Student savedStudent = captor.getValue();
    assertEquals("RandomFirstname", savedStudent.getUser().getFirstName());
    assertEquals("RandomLastname", savedStudent.getUser().getLastName());
    assertEquals("RandomEmail", savedStudent.getUser().getEmail());
    assertEquals("RandomBio", savedStudent.getBio());
  }

  @Test
  void shouldUpdateUserFirstNameLastNameProfileAndCoverOnly() {
    // Given
    String saveEmail = student.getUser().getEmail();
    String saveBio = student.getUser().toStudent().getBio();

    // When
    userService.updateProfile(
        student.getUser(),
        "RandomEmail",
        "RandomEmail",
        null,
        null,
        "https://RandomProfilePictureUrl.com",
        "https://RandomCoverPictureUrl.com");

    // Then
    ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
    verify(userRepository).save(captor.capture());

    Student savedStudent = captor.getValue();
    assertEquals("RandomEmail", savedStudent.getUser().getFirstName());
    assertEquals("RandomEmail", savedStudent.getUser().getLastName());
    assertEquals(saveEmail, savedStudent.getUser().getEmail());
    assertEquals(saveBio, savedStudent.getBio());
    assertEquals("https://RandomProfilePictureUrl.com", savedStudent.getProfilePicture());
    assertEquals("https://RandomCoverPictureUrl.com", savedStudent.getCoverPicture());
  }

  @Test
  void shouldUpdateUserProfilePicture() throws IOException {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/jpeg");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);

    when(ressourceService.uploadStudentProfilePicture(student.getUser(), mockFile))
        .thenReturn(
            "https://baseUrl.com/photo/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg");

    userService.uploadStudentProfilePicture(student.getUser(), mockFile);

    // Then
    verify(ressourceService).uploadStudentProfilePicture(student.getUser(), mockFile);
  }

  @Test
  void shouldThrowBadImageSizeExceptionDuringProfilePictureUpdate() {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/jpeg");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE + 1);

    // Then
    assertThrows(
        BadImageSizeException.class,
        () -> {
          userService.uploadStudentProfilePicture(student.getUser(), mockFile);
        });
  }

  @Test
  void shouldThrowBadImageTypeExceptionDuringProfilePictureUpdate() {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/pdf");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);

    // Then
    assertThrows(
        BadImageTypeException.class,
        () -> {
          userService.uploadStudentProfilePicture(student.getUser(), mockFile);
        });
  }

  @Test
  void shouldUpdateUserCoverPicture() throws IOException {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/jpeg");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);

    when(ressourceService.uploadStudentCoverPicture(student.getUser(), mockFile))
        .thenReturn(
            "https://baseUrl.com/cover/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg");

    userService.uploadStudentCoverPicture(student.getUser(), mockFile);

    // Then
    verify(ressourceService).uploadStudentCoverPicture(student.getUser(), mockFile);
  }

  @Test
  void shouldThrowBadImageSizeExceptionDuringCoverPictureUpdate() {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/jpeg");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE + 1);

    // Then
    assertThrows(
        BadImageSizeException.class,
        () -> {
          userService.uploadStudentCoverPicture(student.getUser(), mockFile);
        });
  }

  @Test
  void shouldThrowBadImageTypeExceptionDuringCoverPictureUpdate() {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/pdf");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);

    // Then
    assertThrows(
        BadImageTypeException.class,
        () -> {
          userService.uploadStudentCoverPicture(student.getUser(), mockFile);
        });
  }

  @Test
  void getProfile_shouldThrowException_whenUserNotFound() {
    // Arrange
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act + Assert
    assertThrows(UserNotFoundException.class, () -> userService.getProfile(userId));
    verify(userRepository).findById(userId);
  }
}
