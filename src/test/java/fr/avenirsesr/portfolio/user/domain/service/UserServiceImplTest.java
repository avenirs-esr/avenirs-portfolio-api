package fr.avenirsesr.portfolio.user.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.cgu.application.adapter.dto.CguDTO;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.user.application.adapter.dto.ExternalUserDTO;
import fr.avenirsesr.portfolio.common.user.domain.exceptions.ExternalUserNotFoundException;
import fr.avenirsesr.portfolio.common.user.domain.model.enums.EUserStatus;
import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.data.CguAcceptanceData;
import fr.avenirsesr.portfolio.user.domain.data.UserQuickLinksData;
import fr.avenirsesr.portfolio.user.domain.exception.CguNotFoundException;
import fr.avenirsesr.portfolio.user.domain.model.Cgu;
import fr.avenirsesr.portfolio.user.domain.port.input.StaffService;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.output.client.CguClient;
import fr.avenirsesr.portfolio.user.domain.port.output.client.ExternalUserClient;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.CguRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StaffFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private CguRepository cguRepository;
  @Mock private UserPrincipalRepository userPrincipalRepository;
  @Mock private StaffService staffService;
  @Mock private StudentService studentService;
  @Mock private ExternalUserClient externalUserClient;
  @Mock private CguClient cguClient;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private NotificationRepository notificationRepository;

  private UserServiceImpl userService;
  private User loggedUser;

  @BeforeEach
  void setUp() {
    loggedUser = StudentFixture.create().toModel().getUser();

    userService =
        new UserServiceImpl(
            userRepository,
            cguRepository,
            userPrincipalRepository,
            staffService,
            studentService,
            externalUserClient,
            cguClient,
            loggedInUserService,
            notificationRepository);
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

      BddLogger.given("an existing user principal for the eppn with a matching student record");
      when(userPrincipalRepository.findByEppn(eppn)).thenReturn(Optional.of(loggedUser));
      when(studentService.existsById(loggedUser.getId())).thenReturn(true);

      BddLogger.when("getting the user by eppn");
      User result = userService.getUserByEppn(eppn);

      BddLogger.then("it should return the matching user");
      assertEquals(loggedUser, result);
      verify(userPrincipalRepository).findByEppn(eppn);
      verifyNoInteractions(externalUserClient);
    }

    @Test
    void shouldReconcileCategoryRecordWhenUserPrincipalExistsWithoutStudentOrStaffRecord() {
      String eppn = "lucas.tessier@university.com";
      ExternalUserDTO externalUser = externalUser(EUserCategory.STUDENT, EUserStatus.ACTIVE, eppn);

      BddLogger.given("an existing user principal for the eppn with no student or staff record");
      when(userPrincipalRepository.findByEppn(eppn)).thenReturn(Optional.of(loggedUser));
      when(studentService.existsById(loggedUser.getId())).thenReturn(false);
      when(staffService.existsById(loggedUser.getId())).thenReturn(false);
      when(externalUserClient.getByEppn(eppn)).thenReturn(Optional.of(externalUser));

      BddLogger.when("getting the user by eppn");
      User result = userService.getUserByEppn(eppn);

      BddLogger.then("it should return the matching user");
      assertEquals(loggedUser, result);

      BddLogger.then("it should create the missing student profile");
      verify(studentService).createStudent(loggedUser.getId(), externalUser.email(), null);
    }

    @Test
    void
        shouldSkipReconciliationWhenUserPrincipalExistsWithoutStudentOrStaffRecordAndNoExternalUser() {
      String eppn = "lucas.tessier@university.com";

      BddLogger.given(
          "an existing user principal for the eppn with no student or staff record and no"
              + " matching external user");
      when(userPrincipalRepository.findByEppn(eppn)).thenReturn(Optional.of(loggedUser));
      when(studentService.existsById(loggedUser.getId())).thenReturn(false);
      when(staffService.existsById(loggedUser.getId())).thenReturn(false);
      when(externalUserClient.getByEppn(eppn)).thenReturn(Optional.empty());

      BddLogger.when("getting the user by eppn");
      User result = userService.getUserByEppn(eppn);

      BddLogger.then("it should return the matching user without reconciling");
      assertEquals(loggedUser, result);
      verify(studentService, never()).createStudent(any(), any(), any());
      verify(staffService, never()).createStaff(any(), any(), any());
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

  @Nested
  class GetQuickLinks {

    @Test
    void shouldReturnQuickLinksDataForStudent() {
      var user =
          UserFixture.create()
              .withFirstName("Lucas")
              .withLastName("Tessier")
              .withNotificationEnabled(true)
              .toModel();
      var student =
          StudentFixture.create()
              .withId(user.getId())
              .withUser(user)
              .withHasUnseenNotification(true)
              .toModel();

      BddLogger.given("a logged student with unseen notifications and 3 unread messages");
      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(notificationRepository.countUnreadByUserAndCategory(user.getId(), EUserCategory.STUDENT))
          .thenReturn(3L);

      BddLogger.when("getting quick links for STUDENT category");
      UserQuickLinksData result = userService.getQuickLinks(EUserCategory.STUDENT);

      BddLogger.then("it should return the correct quick links data");
      assertThat(result.userId()).isEqualTo(user.getId());
      assertThat(result.firstname()).isEqualTo("Lucas");
      assertThat(result.lastname()).isEqualTo("Tessier");
      assertThat(result.hasUnseenNotification()).isTrue();
      assertThat(result.unreadNotifications()).isEqualTo(3);
      assertThat(result.notificationEnabled()).isTrue();

      verify(loggedInUserService).getLoggedInStudent();
      verify(notificationRepository)
          .countUnreadByUserAndCategory(user.getId(), EUserCategory.STUDENT);
      verifyNoInteractions(staffService);
    }

    @Test
    void shouldReturnQuickLinksDataForStaff() {
      var user =
          UserFixture.create()
              .withFirstName("Marie")
              .withLastName("Dupont")
              .withNotificationEnabled(false)
              .toModel();
      var staff =
          StaffFixture.create()
              .withId(user.getId())
              .withUser(user)
              .withHasUnseenNotification(false)
              .toModel();

      BddLogger.given("a logged staff member with no unseen notifications and 0 unread messages");
      when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
      when(notificationRepository.countUnreadByUserAndCategory(user.getId(), EUserCategory.STAFF))
          .thenReturn(0L);

      BddLogger.when("getting quick links for STAFF category");
      UserQuickLinksData result = userService.getQuickLinks(EUserCategory.STAFF);

      BddLogger.then("it should return the correct quick links data");
      assertThat(result.userId()).isEqualTo(user.getId());
      assertThat(result.firstname()).isEqualTo("Marie");
      assertThat(result.lastname()).isEqualTo("Dupont");
      assertThat(result.hasUnseenNotification()).isFalse();
      assertThat(result.unreadNotifications()).isZero();
      assertThat(result.notificationEnabled()).isFalse();

      verify(loggedInUserService).getLoggedInStaff();
      verify(notificationRepository)
          .countUnreadByUserAndCategory(user.getId(), EUserCategory.STAFF);
      verifyNoInteractions(studentService);
    }

    @Test
    void shouldReturnZeroUnreadNotificationsWhenRepositoryReturnsZero() {
      var student = StudentFixture.create().withHasUnseenNotification(false).toModel();
      var user = UserFixture.create().withId(student.getId()).toModel();

      BddLogger.given("a student with no unread notifications");
      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      when(notificationRepository.countUnreadByUserAndCategory(user.getId(), EUserCategory.STUDENT))
          .thenReturn(0L);

      BddLogger.when("getting quick links for STUDENT");
      UserQuickLinksData result = userService.getQuickLinks(EUserCategory.STUDENT);

      BddLogger.then(
          "unreadNotifications should be zero and hasUnseenNotification should be false");
      assertThat(result.unreadNotifications()).isZero();
      assertThat(result.hasUnseenNotification()).isFalse();
    }
  }

  private ExternalUserDTO externalUser(EUserCategory category, EUserStatus status, String eppn) {
    return new ExternalUserDTO(
        eppn,
        "Lucas",
        "Tessier",
        eppn,
        Set.of(category),
        eppn,
        "PEGASE",
        List.of(UUID.randomUUID()),
        List.of(UUID.randomUUID()),
        status);
  }

  @Nested
  class AcceptCgu {

    private CguDTO publishedCgu() {
      return new CguDTO(
          UUID.randomUUID(), 2, Instant.parse("2026-09-01T10:00:00Z"), "<html></html>");
    }

    private Cgu acceptanceOf(UUID versionId, Instant acceptedAt) {
      return Cgu.toDomain(
          UUID.randomUUID(), acceptedAt, acceptedAt, loggedUser, versionId, acceptedAt);
    }

    @Test
    void shouldRecordTheAcceptanceOfTheCurrentTermsOfUse() {
      BddLogger.given("a published terms of use version not accepted yet by the logged-in user");
      CguDTO current = publishedCgu();
      when(loggedInUserService.getLoggedInUser()).thenReturn(loggedUser);
      when(cguClient.getLatest()).thenReturn(Optional.of(current));
      when(cguRepository.findByUserAndVersion(loggedUser.getId(), current.id()))
          .thenReturn(Optional.empty());
      when(cguRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      BddLogger.when("accepting the current version");
      CguAcceptanceData result = userService.acceptCgu();

      BddLogger.then("the acceptance is saved for that user and returned as the last version");
      ArgumentCaptor<Cgu> captor = ArgumentCaptor.forClass(Cgu.class);
      verify(cguRepository).save(captor.capture());
      Cgu saved = captor.getValue();
      assertThat(saved.getUser()).isEqualTo(loggedUser);
      assertThat(saved.getVersionId()).isEqualTo(current.id());
      assertThat(saved.getAcceptedAt()).isNotNull();
      assertThat(result)
          .isEqualTo(new CguAcceptanceData(current.id(), saved.getAcceptedAt(), true));
    }

    @Test
    void shouldKeepTheFirstAcceptanceWhenTheVersionIsAlreadyAccepted() {
      BddLogger.given("a terms of use version already accepted by the logged-in user");
      CguDTO current = publishedCgu();
      Instant firstAcceptance = Instant.parse("2026-09-02T08:00:00Z");
      when(loggedInUserService.getLoggedInUser()).thenReturn(loggedUser);
      when(cguClient.getLatest()).thenReturn(Optional.of(current));
      when(cguRepository.findByUserAndVersion(loggedUser.getId(), current.id()))
          .thenReturn(Optional.of(acceptanceOf(current.id(), firstAcceptance)));

      BddLogger.when("accepting it again");
      CguAcceptanceData result = userService.acceptCgu();

      BddLogger.then("the first acceptance date is returned and no row is added");
      assertThat(result).isEqualTo(new CguAcceptanceData(current.id(), firstAcceptance, true));
      verify(cguRepository, never()).save(any());
    }

    @Test
    void shouldKeepTheHistoryWhenANewerVersionIsAccepted() {
      BddLogger.given("a logged-in user who accepted an outdated version");
      CguDTO current = publishedCgu();
      when(loggedInUserService.getLoggedInUser()).thenReturn(loggedUser);
      when(cguClient.getLatest()).thenReturn(Optional.of(current));
      when(cguRepository.findByUserAndVersion(loggedUser.getId(), current.id()))
          .thenReturn(Optional.empty());
      when(cguRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      BddLogger.when("accepting the newer version");
      CguAcceptanceData result = userService.acceptCgu();

      BddLogger.then("a row is added for it and no previous acceptance is removed");
      assertThat(result.versionId()).isEqualTo(current.id());
      verify(cguRepository).save(any());
      verify(cguRepository, never()).removeFromDatabase(any());
      verify(cguRepository, never()).removeAllFromDatabase(any());
    }

    @Test
    void shouldFailWhenNoTermsOfUseIsPublished() {
      BddLogger.given("no published terms of use version");
      when(loggedInUserService.getLoggedInUser()).thenReturn(loggedUser);
      when(cguClient.getLatest()).thenReturn(Optional.empty());

      BddLogger.when("accepting the current version");
      BddLogger.then("it should fail without touching the database");
      assertThrows(CguNotFoundException.class, () -> userService.acceptCgu());

      verifyNoInteractions(cguRepository);
    }
  }

  @Nested
  class GetAcceptedCgu {

    private Cgu acceptanceOf(UUID versionId) {
      Instant acceptedAt = Instant.parse("2026-09-03T09:00:00Z");
      return Cgu.toDomain(
          UUID.randomUUID(), acceptedAt, acceptedAt, loggedUser, versionId, acceptedAt);
    }

    @Test
    void shouldFlagTheLastAcceptedCguAsTheLastVersion() {
      BddLogger.given("a logged-in user who accepted the published version");
      UUID published = UUID.randomUUID();
      when(loggedInUserService.getLoggedInUser()).thenReturn(loggedUser);
      when(cguRepository.findLastAcceptedByUser(loggedUser.getId()))
          .thenReturn(Optional.of(acceptanceOf(published)));
      when(cguClient.getLatestId()).thenReturn(Optional.of(published));

      BddLogger.when("getting the accepted terms of use of the logged-in user");
      var result = userService.getAcceptedCgu();

      BddLogger.then("the acceptance is returned, flagged as the last version");
      assertThat(result).isPresent();
      assertThat(result.get().versionId()).isEqualTo(published);
      assertThat(result.get().isLastVersion()).isTrue();
    }

    @Test
    void shouldFlagAnOutdatedAcceptance() {
      BddLogger.given("a logged-in user whose last acceptance is not the published version");
      when(loggedInUserService.getLoggedInUser()).thenReturn(loggedUser);
      when(cguRepository.findLastAcceptedByUser(loggedUser.getId()))
          .thenReturn(Optional.of(acceptanceOf(UUID.randomUUID())));
      when(cguClient.getLatestId()).thenReturn(Optional.of(UUID.randomUUID()));

      BddLogger.when("getting the accepted terms of use of the logged-in user");
      var result = userService.getAcceptedCgu();

      BddLogger.then("the acceptance is not flagged as the last version");
      assertThat(result).isPresent();
      assertThat(result.get().isLastVersion()).isFalse();
    }

    @Test
    void shouldExposeNoAcceptedCguWhenTheUserNeverAcceptedAnything() {
      BddLogger.given("a logged-in user who never accepted a version");
      when(loggedInUserService.getLoggedInUser()).thenReturn(loggedUser);
      when(cguRepository.findLastAcceptedByUser(loggedUser.getId())).thenReturn(Optional.empty());

      BddLogger.when("getting the accepted terms of use of the logged-in user");
      var result = userService.getAcceptedCgu();

      BddLogger.then("nothing is returned and the back-office is not called");
      assertThat(result).isEmpty();
      verifyNoInteractions(cguClient);
    }
  }

  @Nested
  class WhenLookingUpProfilesByEppn {

    private static final String CONNECTED_EPPN = "lucas.tessier@university.com";
    private static final String UNKNOWN_EPPN = "never.connected@university.com";

    @Test
    void shouldReportNoConnectionWhenNoAccountExistsForTheEppn() {
      BddLogger.given("an eppn no account was ever created for");
      when(userPrincipalRepository.findAllByEppns(List.of(UNKNOWN_EPPN))).thenReturn(Map.of());
      when(staffService.findProfilePictureIdsByIds(Set.of())).thenReturn(Map.of());
      when(studentService.findProfilePictureIdsByIds(Set.of())).thenReturn(Map.of());

      BddLogger.when("looking up its profile");
      var profiles = userService.findProfilesByEppns(List.of(UNKNOWN_EPPN));

      BddLogger.then("it should report the eppn as never connected, without user nor picture");
      assertThat(profiles).hasSize(1);
      assertThat(profiles.getFirst().eppn()).isEqualTo(UNKNOWN_EPPN);
      assertThat(profiles.getFirst().hasConnected()).isFalse();
      assertThat(profiles.getFirst().userId()).isNull();
      assertThat(profiles.getFirst().profilePictureId()).isNull();
    }

    @Test
    void shouldReturnTheProfilePictureOfAConnectedUser() {
      BddLogger.given("a connected user owning a profile picture");
      User user = UserFixture.create().toModel();
      UUID pictureId = UUID.randomUUID();
      when(userPrincipalRepository.findAllByEppns(List.of(CONNECTED_EPPN)))
          .thenReturn(Map.of(CONNECTED_EPPN, user));
      when(staffService.findProfilePictureIdsByIds(Set.of(user.getId()))).thenReturn(Map.of());
      when(studentService.findProfilePictureIdsByIds(Set.of(user.getId())))
          .thenReturn(Map.of(user.getId(), pictureId));

      BddLogger.when("looking up its profile");
      var profiles = userService.findProfilesByEppns(List.of(CONNECTED_EPPN));

      BddLogger.then("it should report the connection and the picture");
      assertThat(profiles).hasSize(1);
      assertThat(profiles.getFirst().hasConnected()).isTrue();
      assertThat(profiles.getFirst().userId()).isEqualTo(user.getId());
      assertThat(profiles.getFirst().profilePictureId()).isEqualTo(pictureId);
    }

    @Test
    void shouldReportWhetherAConnectedUserIsStaff() {
      BddLogger.given("a connected user owning a staff record");
      User user = UserFixture.create().toModel();
      when(userPrincipalRepository.findAllByEppns(List.of(CONNECTED_EPPN)))
          .thenReturn(Map.of(CONNECTED_EPPN, user));
      when(staffService.findExistingIds(Set.of(user.getId()))).thenReturn(Set.of(user.getId()));

      BddLogger.when("looking up its profile");
      var profiles = userService.findProfilesByEppns(List.of(CONNECTED_EPPN));

      BddLogger.then("it should be reported as staff");
      assertThat(profiles.getFirst().staff()).isTrue();
    }

    @Test
    void shouldReportWhetherAConnectedUserIsStudent() {
      BddLogger.given("a connected user owning a student record");
      User user = UserFixture.create().toModel();
      when(userPrincipalRepository.findAllByEppns(List.of(CONNECTED_EPPN)))
          .thenReturn(Map.of(CONNECTED_EPPN, user));
      when(studentService.findExistingIds(Set.of(user.getId()))).thenReturn(Set.of(user.getId()));

      BddLogger.when("looking up its profile");
      var profiles = userService.findProfilesByEppns(List.of(CONNECTED_EPPN));

      BddLogger.then("it should be reported as student and not as staff");
      assertThat(profiles.getFirst().student()).isTrue();
      assertThat(profiles.getFirst().staff()).isFalse();
    }

    @Test
    void shouldNotReportAsStaffAConnectedUserWithoutStaffRecord() {
      BddLogger.given("a connected user without staff record");
      User user = UserFixture.create().toModel();
      when(userPrincipalRepository.findAllByEppns(List.of(CONNECTED_EPPN)))
          .thenReturn(Map.of(CONNECTED_EPPN, user));

      BddLogger.when("looking up its profile");
      var profiles = userService.findProfilesByEppns(List.of(CONNECTED_EPPN));

      BddLogger.then("it should be connected but not staff");
      assertThat(profiles.getFirst().hasConnected()).isTrue();
      assertThat(profiles.getFirst().staff()).isFalse();
    }

    @Test
    void shouldReportTheConnectionWithoutPictureWhenTheUserUploadedNone() {
      BddLogger.given("a connected user who never uploaded a profile picture");
      User user = UserFixture.create().toModel();
      when(userPrincipalRepository.findAllByEppns(List.of(CONNECTED_EPPN)))
          .thenReturn(Map.of(CONNECTED_EPPN, user));
      when(staffService.findProfilePictureIdsByIds(Set.of(user.getId()))).thenReturn(Map.of());
      when(studentService.findProfilePictureIdsByIds(Set.of(user.getId()))).thenReturn(Map.of());

      BddLogger.when("looking up its profile");
      var profiles = userService.findProfilesByEppns(List.of(CONNECTED_EPPN));

      BddLogger.then("it should report the connection with a null picture");
      assertThat(profiles.getFirst().hasConnected()).isTrue();
      assertThat(profiles.getFirst().profilePictureId()).isNull();
    }

    @Test
    void shouldPreferTheStudentPictureWhenTheUserHoldsBothProfiles() {
      BddLogger.given("a user holding both a student and a staff profile, each with a picture");
      User user = UserFixture.create().toModel();
      UUID studentPictureId = UUID.randomUUID();
      UUID staffPictureId = UUID.randomUUID();
      when(userPrincipalRepository.findAllByEppns(List.of(CONNECTED_EPPN)))
          .thenReturn(Map.of(CONNECTED_EPPN, user));
      when(staffService.findProfilePictureIdsByIds(Set.of(user.getId())))
          .thenReturn(Map.of(user.getId(), staffPictureId));
      when(studentService.findProfilePictureIdsByIds(Set.of(user.getId())))
          .thenReturn(Map.of(user.getId(), studentPictureId));

      BddLogger.when("looking up its profile");
      var profiles = userService.findProfilesByEppns(List.of(CONNECTED_EPPN));

      BddLogger.then("it should return the picture of its student profile");
      assertThat(profiles.getFirst().profilePictureId()).isEqualTo(studentPictureId);
    }

    @Test
    void shouldQueryEachRepositoryOnceAndKeepTheRequestedOrder() {
      BddLogger.given("a batch repeating one eppn and mixing a connected and an unknown one");
      User user = UserFixture.create().toModel();
      when(userPrincipalRepository.findAllByEppns(List.of(CONNECTED_EPPN, UNKNOWN_EPPN)))
          .thenReturn(Map.of(CONNECTED_EPPN, user));
      when(staffService.findProfilePictureIdsByIds(Set.of(user.getId()))).thenReturn(Map.of());
      when(studentService.findProfilePictureIdsByIds(Set.of(user.getId()))).thenReturn(Map.of());

      BddLogger.when("looking up the profiles");
      var profiles =
          userService.findProfilesByEppns(List.of(CONNECTED_EPPN, UNKNOWN_EPPN, CONNECTED_EPPN));

      BddLogger.then("it should deduplicate the eppns, keep their order and batch the lookups");
      assertThat(profiles).hasSize(2);
      assertThat(profiles.get(0).eppn()).isEqualTo(CONNECTED_EPPN);
      assertThat(profiles.get(0).hasConnected()).isTrue();
      assertThat(profiles.get(1).eppn()).isEqualTo(UNKNOWN_EPPN);
      assertThat(profiles.get(1).hasConnected()).isFalse();
      verify(userPrincipalRepository, times(1)).findAllByEppns(any());
      verify(staffService, times(1)).findProfilePictureIdsByIds(any());
      verify(studentService, times(1)).findProfilePictureIdsByIds(any());
    }

    @Test
    void shouldReturnNothingWithoutQueryingAnythingWhenNoEppnIsGiven() {
      BddLogger.given("no eppn at all");

      BddLogger.when("looking up the profiles");
      BddLogger.then("it should return nothing without querying the repositories");
      assertThat(userService.findProfilesByEppns(List.of())).isEmpty();
      assertThat(userService.findProfilesByEppns(null)).isEmpty();
      verifyNoInteractions(userPrincipalRepository, staffService, studentService);
    }
  }
}
