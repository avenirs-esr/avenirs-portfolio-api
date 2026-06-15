package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.application.adapter.dto.ExternalUserDTO;
import fr.avenirsesr.portfolio.common.user.domain.exceptions.ExternalUserNotFoundException;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.port.input.StaffService;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.output.client.ExternalUserClient;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
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
  @Mock private StaffService staffService;
  @Mock private StudentService studentService;
  @Mock private ExternalUserClient externalUserClient;
  @Mock private LoggedInUserService loggedInUserService;

  private UserServiceImpl userService;
  private User loggedUser;

  @BeforeEach
  void setUp() {
    loggedUser = StudentFixture.create().toModel().getUser();

    userService =
        new UserServiceImpl(
            userRepository,
            userPrincipalRepository,
            staffService,
            studentService,
            externalUserClient,
            loggedInUserService);
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
    void shouldThrowExceptionWhenUserDoesNotExist() {
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
    void shouldReturnUserWhenUserPrincipalExists() {
      String eppn = "lucas.tessier@university.com";

      BddLogger.given("an existing user principal for the eppn");
      when(userPrincipalRepository.findByEppn(eppn)).thenReturn(Optional.of(loggedUser));

      BddLogger.when("getting the user by eppn");
      User result = userService.getUserByEppn(eppn);

      BddLogger.then("it should return the matching user");
      assertEquals(loggedUser, result);
      verify(userPrincipalRepository).findByEppn(eppn);
      verifyNoInteractions(externalUserClient);
    }

    @Test
    void shouldCreateStudentUserWhenUserPrincipalDoesNotExistAndExternalUserExists() {
      String eppn = "lucas.tessier@university.com";
      ExternalUserDTO externalUser = externalUser(EUserCategory.STUDENT, EUserStatus.ACTIVE, eppn);

      BddLogger.given("no user principal exists for the eppn");
      when(userPrincipalRepository.findByEppn(eppn)).thenReturn(Optional.empty());

      BddLogger.given("an enabled student external user exists for the eppn");
      when(externalUserClient.getByEppn(eppn)).thenReturn(Optional.of(externalUser));

      BddLogger.when("getting the user by eppn");
      User result = userService.getUserByEppn(eppn);

      BddLogger.then("it should create and save the user");
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertEquals(externalUser.firstName(), savedUser.getFirstName());
      assertEquals(externalUser.lastName(), savedUser.getLastName());
      assertEquals(externalUser.email(), savedUser.getEmail());
      assertEquals(savedUser, result);

      BddLogger.then("it should create the user principal");
      verify(userPrincipalRepository).saveOrUpdate(savedUser, eppn);

      BddLogger.then("it should create the student profile");
      verify(studentService).createStudent(savedUser.getId(), externalUser.email(), null);
      verifyNoInteractions(staffService);

      BddLogger.then("it should activate the external user");
      verify(externalUserClient).activateByEppn(eppn);
    }

    @Test
    void shouldCreateStaffUserWhenUserPrincipalDoesNotExistAndExternalUserExists() {
      String eppn = "staff@university.com";
      ExternalUserDTO externalUser = externalUser(EUserCategory.STAFF, EUserStatus.ACTIVE, eppn);

      BddLogger.given("no user principal exists for the eppn");
      when(userPrincipalRepository.findByEppn(eppn)).thenReturn(Optional.empty());

      BddLogger.given("an enabled staff external user exists for the eppn");
      when(externalUserClient.getByEppn(eppn)).thenReturn(Optional.of(externalUser));

      BddLogger.when("getting the user by eppn");
      User result = userService.getUserByEppn(eppn);

      BddLogger.then("it should create and save the user");
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertEquals(savedUser, result);

      BddLogger.then("it should create the staff profile");
      verify(staffService).createStaff(savedUser.getId(), externalUser.email(), null);
      verifyNoInteractions(studentService);

      BddLogger.then("it should activate the external user");
      verify(externalUserClient).activateByEppn(eppn);
    }

    @Test
    void shouldThrowExceptionWhenExternalUserDoesNotExist() {
      String eppn = "unknown@university.com";

      BddLogger.given("no user principal exists for the eppn");
      when(userPrincipalRepository.findByEppn(eppn)).thenReturn(Optional.empty());

      BddLogger.given("no external user exists for the eppn");
      when(externalUserClient.getByEppn(eppn)).thenReturn(Optional.empty());

      BddLogger.when("getting the user by eppn");
      BddLogger.then("it should throw ExternalUserNotFoundException");
      assertThrows(ExternalUserNotFoundException.class, () -> userService.getUserByEppn(eppn));

      verify(userRepository, never()).save(any());
      verify(userPrincipalRepository, never()).saveOrUpdate(any(), anyString());
      verifyNoInteractions(studentService, staffService);
      verify(externalUserClient, never()).activateByEppn(anyString());
    }

    @Test
    void shouldThrowExceptionWhenExternalUserIsBlocked() {
      String eppn = "blocked@university.com";
      ExternalUserDTO externalUser = externalUser(EUserCategory.STUDENT, EUserStatus.BLOCKED, eppn);

      BddLogger.given("no user principal exists for the eppn");
      when(userPrincipalRepository.findByEppn(eppn)).thenReturn(Optional.empty());

      BddLogger.given("a blocked external user exists for the eppn");
      when(externalUserClient.getByEppn(eppn)).thenReturn(Optional.of(externalUser));

      BddLogger.when("getting the user by eppn");
      BddLogger.then("it should throw ExternalUserNotFoundException");
      assertThrows(ExternalUserNotFoundException.class, () -> userService.getUserByEppn(eppn));

      verify(userRepository, never()).save(any());
      verify(userPrincipalRepository, never()).saveOrUpdate(any(), anyString());
      verifyNoInteractions(studentService, staffService);
      verify(externalUserClient, never()).activateByEppn(anyString());
    }

    @Test
    void shouldThrowExceptionWhenExternalUserIsRemoved() {
      String eppn = "removed@university.com";
      ExternalUserDTO externalUser = externalUser(EUserCategory.STUDENT, EUserStatus.REMOVED, eppn);

      BddLogger.given("no user principal exists for the eppn");
      when(userPrincipalRepository.findByEppn(eppn)).thenReturn(Optional.empty());

      BddLogger.given("a removed external user exists for the eppn");
      when(externalUserClient.getByEppn(eppn)).thenReturn(Optional.of(externalUser));

      BddLogger.when("getting the user by eppn");
      BddLogger.then("it should throw ExternalUserNotFoundException");
      assertThrows(ExternalUserNotFoundException.class, () -> userService.getUserByEppn(eppn));

      verify(userRepository, never()).save(any());
      verify(userPrincipalRepository, never()).saveOrUpdate(any(), anyString());
      verifyNoInteractions(studentService, staffService);
      verify(externalUserClient, never()).activateByEppn(anyString());
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

    @BeforeEach
    void setUp() {
      when(loggedInUserService.getLoggedInUser()).thenReturn(loggedUser);
    }

    @Test
    void shouldUpdateStudentEmailAndBio() {
      BddLogger.given("a logged student user");

      BddLogger.when("updating student email and bio");
      userService.updateProfile(EUserCategory.STUDENT, "random@email.com", "RandomBio");

      BddLogger.then("it should save the updated user");
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertEquals("random@email.com", savedUser.getEmail());

      BddLogger.then("it should update the student profile");
      verify(studentService).updateProfile(savedUser, "RandomBio");
      verifyNoInteractions(staffService);
    }

    @Test
    void shouldUpdateStaffProfileWithoutChangingEmail() {
      String initialEmail = loggedUser.getEmail();

      BddLogger.given("a logged staff user");

      BddLogger.when("updating staff profile without email");
      userService.updateProfile(EUserCategory.STAFF, null, "RandomBio");

      BddLogger.then("it should save the user without changing email");
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertEquals(initialEmail, savedUser.getEmail());

      BddLogger.then("it should update the staff profile");
      verify(staffService).updateProfile(savedUser, "RandomBio");
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

  @Nested
  class UpdateNotificationPreferences {

    @BeforeEach
    void setUp() {
      when(loggedInUserService.getLoggedInUser()).thenReturn(loggedUser);
    }

    @Test
    void shouldEnableNotificationsForLoggedInUser() {
      loggedUser = UserFixture.create().withNotificationEnabled(false).toModel();
      when(loggedInUserService.getLoggedInUser()).thenReturn(loggedUser);

      BddLogger.given("a logged user with notifications disabled");

      BddLogger.when("enabling notifications");
      userService.updateNotificationPreferences(true);

      BddLogger.then("it should save the user with notifications enabled");
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      assertTrue(userCaptor.getValue().isNotificationEnabled());
    }

    @Test
    void shouldDisableNotificationsForLoggedInUser() {
      loggedUser = UserFixture.create().withNotificationEnabled(true).toModel();
      when(loggedInUserService.getLoggedInUser()).thenReturn(loggedUser);

      BddLogger.given("a logged user with notifications enabled");

      BddLogger.when("disabling notifications");
      userService.updateNotificationPreferences(false);

      BddLogger.then("it should save the user with notifications disabled");
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      assertFalse(userCaptor.getValue().isNotificationEnabled());
    }
  }

  private ExternalUserDTO externalUser(EUserCategory category, EUserStatus status, String eppn) {
    return new ExternalUserDTO(eppn, "Lucas", "Tessier", eppn, category, eppn, "PEGASE", status);
  }
}
