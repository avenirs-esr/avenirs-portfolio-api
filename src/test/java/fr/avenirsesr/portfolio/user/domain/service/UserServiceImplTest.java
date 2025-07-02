package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.shared.domain.exception.BadImageSizeException;
import fr.avenirsesr.portfolio.shared.domain.exception.BadImageTypeException;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.RessourceRepository;
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

  @Mock private RessourceRepository ressourceRepository;

  @InjectMocks private UserServiceImpl userService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @Test
  void shouldUpdateUserFirstnameLastnameEmailAndBio() {
    // When
    when(userRepository.findById(student.getId())).thenReturn(Optional.of(student.getUser()));

    userService.updateProfile(
        student.getId(), "RandomFirstname", "RandomLastname", "RandomEmail", "RandomBio");

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
  void shouldUpdateUserEmailAndBioOnly() {
    // Given
    String saveFirstname = student.getUser().getFirstName();
    String saveLastname = student.getUser().getLastName();

    // When
    when(userRepository.findById(student.getId())).thenReturn(Optional.of(student.getUser()));

    userService.updateProfile(student.getId(), null, null, "RandomEmail", "RandomBio");

    // Then
    ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
    verify(userRepository).save(captor.capture());

    Student savedStudent = captor.getValue();
    assertEquals(saveFirstname, savedStudent.getUser().getFirstName());
    assertEquals(saveLastname, savedStudent.getUser().getLastName());
    assertEquals("RandomEmail", savedStudent.getUser().getEmail());
    assertEquals("RandomBio", savedStudent.getBio());
  }

  @Test
  void shouldUpdateUserProfilePicture() throws IOException {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/jpeg");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);

    when(userRepository.findById(student.getId())).thenReturn(Optional.of(student.getUser()));
    when(ressourceRepository.storeStudentProfilePicture(student.getId(), mockFile))
        .thenReturn(
            "https://baseUrl.com/photo/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg");

    userService.updateProfilePicture(student.getId(), mockFile);

    // Then
    ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
    verify(userRepository).save(captor.capture());

    Student savedStudent = captor.getValue();
    assertEquals(
        "https://baseUrl.com/photo/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg",
        savedStudent.getProfilePicture());
  }

  @Test
  void shouldThrowBadImageSizeExceptionDuringProfilePictureUpdate() {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/jpeg");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE + 1);
    when(userRepository.findById(student.getId())).thenReturn(Optional.of(student.getUser()));

    // Then
    assertThrows(
        BadImageSizeException.class,
        () -> {
          userService.updateProfilePicture(student.getId(), mockFile);
        });
  }

  @Test
  void shouldThrowBadImageTypeExceptionDuringProfilePictureUpdate() {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/pdf");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);
    when(userRepository.findById(student.getId())).thenReturn(Optional.of(student.getUser()));

    // Then
    assertThrows(
        BadImageTypeException.class,
        () -> {
          userService.updateProfilePicture(student.getId(), mockFile);
        });
  }

  @Test
  void shouldUpdateUserCoverPicture() throws IOException {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/jpeg");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(student.getUser()));
    when(ressourceRepository.storeStudentCoverPicture(student.getId(), mockFile))
        .thenReturn(
            "https://baseUrl.com/cover/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg");

    userService.updateCoverPicture(student.getId(), mockFile);

    // Then
    ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
    verify(userRepository).save(captor.capture());

    Student savedStudent = captor.getValue();
    assertEquals(
        "https://baseUrl.com/cover/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg",
        savedStudent.getCoverPicture());
  }

  @Test
  void shouldThrowBadImageSizeExceptionDuringCoverPictureUpdate() {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/jpeg");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE + 1);
    when(userRepository.findById(student.getId())).thenReturn(Optional.of(student.getUser()));

    // Then
    assertThrows(
        BadImageSizeException.class,
        () -> {
          userService.updateCoverPicture(student.getId(), mockFile);
        });
  }

  @Test
  void shouldThrowBadImageTypeExceptionDuringCoverPictureUpdate() {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getContentType()).thenReturn("image/pdf");
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);
    when(userRepository.findById(student.getId())).thenReturn(Optional.of(student.getUser()));

    // Then
    assertThrows(
        BadImageTypeException.class,
        () -> {
          userService.updateCoverPicture(student.getId(), mockFile);
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
