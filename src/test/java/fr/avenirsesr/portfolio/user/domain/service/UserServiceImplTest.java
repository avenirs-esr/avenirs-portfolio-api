package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.user.domain.exception.FirstnameIsNullException;
import fr.avenirsesr.portfolio.user.domain.exception.LastnameIsNullException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private StudentRepository studentRepository;

  @Mock private StudentServiceImpl studentService;
  @Mock private TeacherServiceImpl teacherService;
  @InjectMocks private UserServiceImpl userService;

  private Student student;
  private MockedStatic<RequestContext> mockedRequestContext;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    mockedRequestContext = mockStatic(RequestContext.class);
    mockedRequestContext
        .when(RequestContext::get)
        .thenReturn(new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
  }

  @AfterEach
  void tearDown() {
    mockedRequestContext.close();
  }

  @Test
  void shouldUpdateUserFirstnameLastnameEmailAndBio() {
    BddLogger.given("a UserServiceImpl service");
    BddLogger.when("updating firstname, lastname, email and bio");
    userService.updateProfile(
        EUserCategory.STUDENT, "RandomFirstname", "RandomLastname", "RandomEmail", "RandomBio");

    BddLogger.when("it should update firstname, lastname, email and bio");
    ArgumentCaptor<User> captorUser = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captorUser.capture());

    User savedUser = captorUser.getValue();
    assertEquals("RandomFirstname", savedUser.getFirstName());
    assertEquals("RandomLastname", savedUser.getLastName());
    assertEquals("RandomEmail", savedUser.getEmail());

    verify(studentService).updateProfile(any(User.class), anyString());
  }

  @Test
  void shouldUpdateUserFirstNameLastNameProfileAndCoverOnly() {
    BddLogger.given("a UserServiceImpl service");
    String saveEmail = student.getUser().getEmail();

    BddLogger.when("only updating firstname and lastname");
    userService.updateProfile(
        EUserCategory.TEACHER, "RandomFirstname", "RandomLastname", null, null);

    BddLogger.then("it should only update firstname and lastname");
    ArgumentCaptor<User> captorUser = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captorUser.capture());

    User savedUser = captorUser.getValue();
    assertEquals("RandomFirstname", savedUser.getFirstName());
    assertEquals("RandomLastname", savedUser.getLastName());
    assertEquals(saveEmail, savedUser.getEmail());

    verify(teacherService).updateProfile(any(User.class), isNull());
  }

  @Test
  void shouldUpdateUserWithNullFirstname() {
    BddLogger.given("a UserServiceImpl service");
    BddLogger.when("updating with null firstname");
    BddLogger.then("it should throw FirstnameIsNullException");
    assertThrows(
        FirstnameIsNullException.class,
        () -> {
          userService.updateProfile(
              EUserCategory.STUDENT, null, "RandomLastname", "RandomEmail", "RandomBio");
        });
  }

  @Test
  void shouldUpdateUserWithNullLastname() {
    BddLogger.given("a UserServiceImpl service");
    BddLogger.when("updating with null firstname");
    BddLogger.then("it should throw FirstnameIsNullException");
    assertThrows(
        LastnameIsNullException.class,
        () -> {
          userService.updateProfile(
              EUserCategory.STUDENT, "RandomFirstname", null, "RandomEmail", "RandomBio");
        });
  }

  @Test
  void getUser_shouldThrowException_whenUserNotFound() {
    BddLogger.given("a UserServiceImpl service");
    // Arrange
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    BddLogger.when("getting an unknwon user");
    BddLogger.then("it should throw UserNotFoundException");
    // Act + Assert
    assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));
    verify(userRepository).findById(userId);
  }
}
