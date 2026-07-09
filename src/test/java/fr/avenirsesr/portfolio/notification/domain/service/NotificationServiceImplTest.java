package fr.avenirsesr.portfolio.notification.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.BaseNotification;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.AskForFeedbackParameters;
import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StaffFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

  @Mock private NotificationRepository notificationRepository;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private StudentRepository studentRepository;
  @Mock private StaffRepository staffRepository;

  private NotificationServiceImpl service;

  @BeforeEach
  void setUp() {
    service =
        new NotificationServiceImpl(
            notificationRepository, studentRepository, staffRepository, loggedInUserService);
  }

  private Notification buildNotification(User user, EUserCategory category) {
    return Notification.toDomain(
        UUID.randomUUID(),
        Instant.now(),
        Instant.now(),
        ENotificationType.ASK_FOR_FEEDBACK,
        UUID.randomUUID(),
        user,
        category,
        new AskForFeedbackParameters("Alice", "Martin", "Activité de test"),
        false);
  }

  @Nested
  class NotifySave {

    @Test
    void should_save_the_notification_built_from_the_base_notification() {
      BddLogger.given("A BaseNotification that builds a specific Notification");
      User user = UserFixture.create().withNotificationEnabled(true).toModel();
      Notification expectedNotification = buildNotification(user, EUserCategory.STUDENT);
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(expectedNotification);
      when(notificationRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("The repository saves exactly the notification produced by build()");
      ArgumentCaptor<List<Notification>> captor = forClass(List.class);
      verify(notificationRepository).saveAll(captor.capture());
      assertThat(captor.getValue()).containsExactly(expectedNotification);
    }

    @Test
    void should_call_build_exactly_once() {
      BddLogger.given("A BaseNotification mock");
      User user = UserFixture.create().withNotificationEnabled(true).toModel();
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(buildNotification(user, EUserCategory.STUDENT));
      when(notificationRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("build() is invoked exactly once");
      verify(baseNotification, times(1)).build();
    }

    @Test
    void getNotifications_should_resolve_user_id_from_logged_in_user_service() {
      BddLogger.given("A logged-in user with a known ID");
      UUID userId = UUID.randomUUID();
      User user = mock(User.class);
      when(user.getId()).thenReturn(userId);
      when(loggedInUserService.getLoggedInUser()).thenReturn(user);
      when(notificationRepository.findByUserAndCategory(any(), any(), any()))
          .thenReturn(new PagedResult<>(List.of(), new PageInfo(0, 8, 0)));

      BddLogger.when("getNotifications is called");
      service.getNotifications(EUserCategory.STAFF, new PageCriteria(0, 8));

      BddLogger.then("The repository is called with the logged-in user's ID");
      ArgumentCaptor<UUID> userIdCaptor = forClass(UUID.class);
      verify(notificationRepository).findByUserAndCategory(userIdCaptor.capture(), any(), any());
      assertThat(userIdCaptor.getValue()).isEqualTo(userId);
    }

    @Test
    void getNotifications_should_delegate_category_and_pagination_to_repository() {
      BddLogger.given("A logged-in user, a category filter and pagination criteria");
      User user = mock(User.class);
      when(user.getId()).thenReturn(UUID.randomUUID());
      when(loggedInUserService.getLoggedInUser()).thenReturn(user);

      EUserCategory category = EUserCategory.STUDENT;
      PageCriteria pageCriteria = new PageCriteria(1, 5);
      when(notificationRepository.findByUserAndCategory(any(), eq(category), eq(pageCriteria)))
          .thenReturn(new PagedResult<>(List.of(), new PageInfo(1, 5, 0)));

      BddLogger.when("getNotifications is called with category STUDENT and page 1/size 5");
      service.getNotifications(category, pageCriteria);

      BddLogger.then("The repository receives the exact category and pagination");
      verify(notificationRepository)
          .findByUserAndCategory(any(), eq(EUserCategory.STUDENT), eq(pageCriteria));
    }

    @Test
    void getNotifications_should_return_paged_result_from_repository() {
      BddLogger.given("A repository that returns a non-empty paged result");
      User user = mock(User.class);
      when(user.getId()).thenReturn(UUID.randomUUID());
      when(loggedInUserService.getLoggedInUser()).thenReturn(user);

      Notification notification = mock(Notification.class);
      PagedResult<Notification> expected =
          new PagedResult<>(List.of(notification), new PageInfo(0, 8, 1));
      when(notificationRepository.findByUserAndCategory(any(), any(), any())).thenReturn(expected);

      BddLogger.when("getNotifications is called");
      PagedResult<Notification> result =
          service.getNotifications(EUserCategory.STAFF, new PageCriteria(0, 8));

      BddLogger.then("The result returned by the repository is passed through unchanged");
      assertThat(result).isSameAs(expected);
      assertThat(result.content()).hasSize(1);
      assertThat(result.pageInfo().totalElements()).isEqualTo(1L);
    }
  }

  @Nested
  class HasUnseenNotification {

    @Test
    void should_set_hasUnseenNotification_true_on_student_when_userCategory_is_STUDENT() {
      BddLogger.given("A notification targeting a STUDENT whose user has notifications enabled");
      User user = UserFixture.create().withNotificationEnabled(true).toModel();
      Student student =
          StudentFixture.create().withUser(user).withHasUnseenNotification(false).toModel();
      Notification notification = buildNotification(user, EUserCategory.STUDENT);
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(notification);
      when(studentRepository.findAllById(List.of(user.getId()))).thenReturn(List.of(student));
      when(notificationRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("hasUnseenNotification is set to true on the student and saved");
      ArgumentCaptor<List<Student>> captor = forClass(List.class);
      verify(studentRepository).saveAll(captor.capture());
      assertTrue(captor.getValue().get(0).isHasUnseenNotification());
    }

    @Test
    void should_not_update_staff_when_userCategory_is_STUDENT() {
      BddLogger.given("A notification targeting a STUDENT whose user has notifications enabled");
      User user = UserFixture.create().withNotificationEnabled(true).toModel();
      Student student = StudentFixture.create().withUser(user).toModel();
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(buildNotification(user, EUserCategory.STUDENT));
      when(studentRepository.findAllById(List.of(user.getId()))).thenReturn(List.of(student));
      when(notificationRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("staffRepository is never queried");
      verify(staffRepository, never()).findAllById(any());
    }

    @Test
    void should_set_hasUnseenNotification_true_on_staff_when_userCategory_is_STAFF() {
      BddLogger.given("A notification targeting a STAFF whose user has notifications enabled");
      User user = UserFixture.create().withNotificationEnabled(true).toModel();
      Staff staff = StaffFixture.create().withUser(user).withHasUnseenNotification(false).toModel();
      Notification notification = buildNotification(user, EUserCategory.STAFF);
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(notification);
      when(staffRepository.findAllById(List.of(user.getId()))).thenReturn(List.of(staff));
      when(notificationRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("hasUnseenNotification is set to true on the staff and saved");
      ArgumentCaptor<List<Staff>> captor = forClass(List.class);
      verify(staffRepository).saveAll(captor.capture());
      assertTrue(captor.getValue().get(0).isHasUnseenNotification());
    }

    @Test
    void should_not_update_student_when_userCategory_is_STAFF() {
      BddLogger.given("A notification targeting a STAFF whose user has notifications enabled");
      User user = UserFixture.create().withNotificationEnabled(true).toModel();
      Staff staff = StaffFixture.create().withUser(user).toModel();
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(buildNotification(user, EUserCategory.STAFF));
      when(staffRepository.findAllById(List.of(user.getId()))).thenReturn(List.of(staff));
      when(notificationRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("studentRepository is never queried");
      verify(studentRepository, never()).findAllById(any());
    }

    @Test
    void should_set_hasUnseenNotification_true_on_both_when_userCategory_is_null() {
      BddLogger.given("A notification with null category whose user has notifications enabled");
      User user = UserFixture.create().withNotificationEnabled(true).toModel();
      Student student =
          StudentFixture.create().withUser(user).withHasUnseenNotification(false).toModel();
      Staff staff = StaffFixture.create().withUser(user).withHasUnseenNotification(false).toModel();
      Notification notification = buildNotification(user, null);
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(notification);
      when(studentRepository.findAllById(List.of(user.getId()))).thenReturn(List.of(student));
      when(staffRepository.findAllById(List.of(user.getId()))).thenReturn(List.of(staff));
      when(notificationRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("hasUnseenNotification is set to true on both student and staff");
      ArgumentCaptor<List<Student>> studentCaptor = forClass(List.class);
      verify(studentRepository).saveAll(studentCaptor.capture());
      assertTrue(studentCaptor.getValue().get(0).isHasUnseenNotification());

      ArgumentCaptor<List<Staff>> staffCaptor = forClass(List.class);
      verify(staffRepository).saveAll(staffCaptor.capture());
      assertTrue(staffCaptor.getValue().get(0).isHasUnseenNotification());
    }
  }

  @Nested
  class NotificationEnabledCheck {

    @Test
    void should_not_save_notification_when_user_has_notifications_disabled() {
      BddLogger.given("A notification whose user has notifications disabled");
      User user = UserFixture.create().withNotificationEnabled(false).toModel();
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(buildNotification(user, EUserCategory.STUDENT));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("the notification is not saved and no unseen flag is updated");
      verify(notificationRepository, never()).save(any());
      verify(studentRepository, never()).findById(any());
      verify(staffRepository, never()).findById(any());
    }

    @Test
    void should_not_save_notification_for_staff_when_user_has_notifications_disabled() {
      BddLogger.given("A STAFF notification whose user has notifications disabled");
      User user = UserFixture.create().withNotificationEnabled(false).toModel();
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(buildNotification(user, EUserCategory.STAFF));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("the notification is not saved");
      verify(notificationRepository, never()).save(any());
      verify(staffRepository, never()).findById(any());
    }

    @Test
    void should_not_save_notification_for_null_category_when_user_has_notifications_disabled() {
      BddLogger.given("A null-category notification whose user has notifications disabled");
      User user = UserFixture.create().withNotificationEnabled(false).toModel();
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(buildNotification(user, null));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("the notification is not saved");
      verify(notificationRepository, never()).save(any());
    }
  }
}
