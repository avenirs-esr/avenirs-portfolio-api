package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.user.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
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
public class UserServiceImplTest {

  @Mock private UserRepository userRepository;

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
        EUserCategory.STUDENT,
        student.getUser(),
        "RandomFirstname",
        "RandomLastname",
        "RandomEmail",
        "RandomBio");

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
        EUserCategory.STUDENT, student.getUser(), "RandomEmail", "RandomEmail", null, null);

    // Then
    ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
    verify(userRepository).save(captor.capture());

    Student savedStudent = captor.getValue();
    assertEquals("RandomEmail", savedStudent.getUser().getFirstName());
    assertEquals("RandomEmail", savedStudent.getUser().getLastName());
    assertEquals(saveEmail, savedStudent.getUser().getEmail());
    assertEquals(saveBio, savedStudent.getBio());
  }

  @Test
  void getUser_shouldThrowException_whenUserNotFound() {
    // Arrange
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act + Assert
    assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
    verify(userRepository).findById(userId);
  }
}
