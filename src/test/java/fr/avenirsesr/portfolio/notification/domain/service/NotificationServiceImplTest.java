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
import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
        List.of(),
        false);
  }

  @Nested
  class NotifySave {

    @Test
    void should_save_the_notification_built_from_the_base_notification() {
      BddLogger.given("A BaseNotification that builds a specific Notification");
      User user = UserFixture.create().toModel();
      Notification expectedNotification = buildNotification(user, EUserCategory.STUDENT);
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(expectedNotification);
      when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("The repository saves exactly the notification produced by build()");
      ArgumentCaptor<Notification> captor = forClass(Notification.class);
      verify(notificationRepository).save(captor.capture());
      assertThat(captor.getValue()).isSameAs(expectedNotification);
    }

    @Test
    void should_call_build_exactly_once() {
      BddLogger.given("A BaseNotification mock");
      User user = UserFixture.create().toModel();
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(buildNotification(user, EUserCategory.STUDENT));
      when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

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
      BddLogger.given("A notification targeting a STUDENT");
      User user = UserFixture.create().toModel();
      Student student =
          StudentFixture.create().withUser(user).withHasUnseenNotification(false).toModel();
      Notification notification = buildNotification(user, EUserCategory.STUDENT);
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(notification);
      when(studentRepository.findById(user.getId())).thenReturn(Optional.of(student));
      when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("hasUnseenNotification is set to true on the student and saved");
      ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
      verify(studentRepository).save(captor.capture());
      assertTrue(captor.getValue().isHasUnseenNotification());
    }

    @Test
    void should_not_update_staff_when_userCategory_is_STUDENT() {
      BddLogger.given("A notification targeting a STUDENT");
      User user = UserFixture.create().toModel();
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(buildNotification(user, EUserCategory.STUDENT));
      when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("staffRepository is never queried");
      verify(staffRepository, never()).findById(any());
    }

    @Test
    void should_set_hasUnseenNotification_true_on_staff_when_userCategory_is_STAFF() {
      BddLogger.given("A notification targeting a STAFF");
      User user = UserFixture.create().toModel();
      Staff staff =
          Staff.toDomain(
              user, user.getEmail(), "bio", false, null, null, Instant.now(), Instant.now());
      Notification notification = buildNotification(user, EUserCategory.STAFF);
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(notification);
      when(staffRepository.findById(user.getId())).thenReturn(Optional.of(staff));
      when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("hasUnseenNotification is set to true on the staff and saved");
      ArgumentCaptor<Staff> captor = ArgumentCaptor.forClass(Staff.class);
      verify(staffRepository).save(captor.capture());
      assertTrue(captor.getValue().isHasUnseenNotification());
    }

    @Test
    void should_not_update_student_when_userCategory_is_STAFF() {
      BddLogger.given("A notification targeting a STAFF");
      User user = UserFixture.create().toModel();
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(buildNotification(user, EUserCategory.STAFF));
      when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("studentRepository is never queried");
      verify(studentRepository, never()).findById(any());
    }

    @Test
    void should_set_hasUnseenNotification_true_on_both_when_userCategory_is_null() {
      BddLogger.given("A notification with no specific user category (null)");
      User user = UserFixture.create().toModel();
      Student student =
          StudentFixture.create().withUser(user).withHasUnseenNotification(false).toModel();
      Staff staff =
          Staff.toDomain(
              user, user.getEmail(), "bio", false, null, null, Instant.now(), Instant.now());
      Notification notification = buildNotification(user, null);
      BaseNotification baseNotification = mock(BaseNotification.class);
      when(baseNotification.build()).thenReturn(notification);
      when(studentRepository.findById(user.getId())).thenReturn(Optional.of(student));
      when(staffRepository.findById(user.getId())).thenReturn(Optional.of(staff));
      when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

      BddLogger.when("notify is called");
      service.notify(baseNotification);

      BddLogger.then("hasUnseenNotification is set to true on both student and staff");
      ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
      verify(studentRepository).save(studentCaptor.capture());
      assertTrue(studentCaptor.getValue().isHasUnseenNotification());

      ArgumentCaptor<Staff> staffCaptor = ArgumentCaptor.forClass(Staff.class);
      verify(staffRepository).save(staffCaptor.capture());
      assertTrue(staffCaptor.getValue().isHasUnseenNotification());
    }
  }
}
