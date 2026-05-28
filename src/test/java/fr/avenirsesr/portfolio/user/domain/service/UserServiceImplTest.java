package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.file.domain.port.input.UserResourceService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private UserPrincipalRepository userPrincipalRepository;
  @Mock private UserResourceService userResourceService;
  @Mock private StaffServiceImpl staffService;
  @Mock private StudentServiceImpl studentService;
  @Mock private LoggedInUserService loggedInUserService;

  private UserServiceImpl userService;
  private Student student;
  private User loggedUser;
  private MockedStatic<RequestContext> mockedRequestContext;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    loggedUser = student.getUser();

    userService =
        new UserServiceImpl(
            userRepository,
            userPrincipalRepository,
            userResourceService,
            staffService,
            studentService,
            loggedInUserService);

    mockedRequestContext = mockStatic(RequestContext.class);
    mockedRequestContext
        .when(RequestContext::get)
        .thenReturn(new RequestData(Optional.of(loggedUser), ELanguage.FRENCH));
  }

  @AfterEach
  void tearDown() {
    mockedRequestContext.close();
  }

  @Nested
  class GetUser {

    @Test
    void shouldReturnUserWhenUserExists() {
      UUID userId = UUID.randomUUID();

      BddLogger.given("an existing user id");
      when(userRepository.findById(userId)).thenReturn(Optional.of(loggedUser));

      BddLogger.when("getting the user by id");
      User result = userService.getUser(userId);

      BddLogger.then("it should return the matching user");
      assertEquals(loggedUser, result);
      verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
      UUID userId = UUID.randomUUID();

      BddLogger.given("an unknown user id");
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      BddLogger.when("getting the user by id");
      BddLogger.then("it should throw UserNotFoundException");
      assertThrows(UserNotFoundException.class, () -> userService.getUser(userId));

      verify(userRepository).findById(userId);
    }
  }

  @Nested
  class GetUserByEppn {

    @Test
    void shouldReturnUserWhenEppnExists() {
      String eppn = "lucas.tessier@university.com";

      BddLogger.given("an existing eppn");
      when(userPrincipalRepository.findByEppn(eppn)).thenReturn(Optional.of(loggedUser));

      BddLogger.when("getting the user by eppn");
      User result = userService.getUserByEppn(eppn);

      BddLogger.then("it should return the matching user");
      assertEquals(loggedUser, result);
      verify(userPrincipalRepository).findByEppn(eppn);
    }

    @Test
    void shouldThrowExceptionWhenEppnNotFound() {
      String eppn = "unknown@university.com";

      BddLogger.given("an unknown eppn");
      when(userPrincipalRepository.findByEppn(eppn)).thenReturn(Optional.empty());

      BddLogger.when("getting the user by eppn");
      BddLogger.then("it should throw UserNotFoundException");
      assertThrows(UserNotFoundException.class, () -> userService.getUserByEppn(eppn));

      verify(userPrincipalRepository).findByEppn(eppn);
    }
  }

  @Nested
  class CreateUser {

    @Test
    void shouldCreateUserAndSaveOrUpdateUserPrincipal() {
      UUID userId = UUID.randomUUID();
      String firstName = "Lucas";
      String lastName = "Tessier";
      String email = "lucas.tessier@email.com";
      String eppn = "lucas.tessier@university.com";

      BddLogger.given("valid user data and an eppn");

      BddLogger.when("creating the user");
      User result = userService.createUser(userId, firstName, lastName, email, eppn);

      BddLogger.then("it should save the user");
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertEquals(userId, savedUser.getId());
      assertEquals(firstName, savedUser.getFirstName());
      assertEquals(lastName, savedUser.getLastName());
      assertEquals(email, savedUser.getEmail());

      BddLogger.then("it should save or update the user principal link");
      verify(userPrincipalRepository).saveOrUpdate(savedUser, eppn);

      BddLogger.then("it should return the created user");
      assertEquals(savedUser, result);
    }
  }

  @Nested
  class UpdateProfile {

    @Test
    void shouldUpdateStudentEmailAndBio() {
      BddLogger.given("a logged student user");

      BddLogger.when("updating student email and bio");
      userService.updateProfile(EUserCategory.STUDENT, "RandomEmail", "RandomBio");

      BddLogger.then("it should save the updated user");
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertEquals("RandomEmail", savedUser.getEmail());

      BddLogger.then("it should update the student profile");
      verify(studentService).updateProfile(savedUser, "RandomBio");
      verifyNoInteractions(staffService);
    }

    @Test
    void shouldUpdateStaffProfileWithoutChangingEmail() {
      String savedEmail = loggedUser.getEmail();

      BddLogger.given("a logged staff user");

      BddLogger.when("updating staff profile without email");
      userService.updateProfile(EUserCategory.STAFF, null, null);

      BddLogger.then("it should save the user without changing email");
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertEquals(savedEmail, savedUser.getEmail());

      BddLogger.then("it should update the staff profile");
      verify(staffService).updateProfile(savedUser, null);
      verifyNoInteractions(studentService);
    }

    @Test
    void shouldThrowExceptionWhenStaffTriesToUpdateEmail() {
      BddLogger.given("a logged staff user");

      BddLogger.when("updating staff email");
      BddLogger.then("it should throw UserNotAuthorizedException");
      assertThrows(
          UserNotAuthorizedException.class,
          () -> userService.updateProfile(EUserCategory.STAFF, "staff@email.com", null));

      verify(userRepository, never()).save(any());
      verifyNoInteractions(staffService, studentService);
    }
  }
}
