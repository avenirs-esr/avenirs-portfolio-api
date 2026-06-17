package fr.avenirsesr.portfolio.notification.infrastructure.adapter.specification;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.model.NotificationEntity;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.repository.NotificationDatabaseRepository;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.repository.NotificationJpaRepository;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@Transactional
@TestPropertySource(properties = {"seeder.enabled=false"})
class NotificationSpecificationIT extends ContainerConfigurationTest {

  @Autowired private NotificationJpaRepository notificationJpaRepository;
  @Autowired private NotificationDatabaseRepository notificationDatabaseRepository;
  @Autowired private EntityManager entityManager;

  private UserEntity user;
  private UserEntity otherUser;

  @BeforeEach
  void setup() {
    user = persistUser("spec-user@example.com");
    otherUser = persistUser("other-user@example.com");
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void hasUser_should_return_only_notifications_for_the_given_user() {
    BddLogger.given("Two notifications for user and one for otherUser");
    NotificationEntity n1 = persistNotification(user, EUserCategory.STAFF);
    NotificationEntity n2 = persistNotification(user, EUserCategory.STUDENT);
    NotificationEntity other = persistNotification(otherUser, EUserCategory.STAFF);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("hasUser spec is applied for user");
    List<NotificationEntity> results =
        notificationJpaRepository.findAll(NotificationSpecification.hasUser(user.getId()));

    BddLogger.then("Only the two notifications belonging to user are returned");
    assertThat(results)
        .extracting(NotificationEntity::getId)
        .containsExactlyInAnyOrder(n1.getId(), n2.getId());
    assertThat(results).extracting(NotificationEntity::getId).doesNotContain(other.getId());
  }

  @Test
  void hasUserCategoryNullOrEquals_should_include_notifications_with_null_category() {
    BddLogger.given("A notification with null category and one with STAFF category");
    NotificationEntity withNull = persistNotification(user, null);
    NotificationEntity withStaff = persistNotification(user, EUserCategory.STAFF);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("hasUserCategoryNullOrEquals(STAFF) spec is applied");
    List<NotificationEntity> results =
        notificationJpaRepository.findAll(
            NotificationSpecification.hasUser(user.getId())
                .and(NotificationSpecification.hasUserCategoryNullOrEquals(EUserCategory.STAFF)));

    BddLogger.then("Both the null-category and the STAFF-category notifications are returned");
    assertThat(results)
        .extracting(NotificationEntity::getId)
        .containsExactlyInAnyOrder(withNull.getId(), withStaff.getId());
  }

  @Test
  void hasUserCategoryNullOrEquals_should_exclude_notifications_with_non_matching_category() {
    BddLogger.given("Notifications with STAFF, STUDENT and null categories");
    NotificationEntity staffNotif = persistNotification(user, EUserCategory.STAFF);
    NotificationEntity studentNotif = persistNotification(user, EUserCategory.STUDENT);
    NotificationEntity nullNotif = persistNotification(user, null);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("hasUserCategoryNullOrEquals(STAFF) spec is applied");
    List<NotificationEntity> results =
        notificationJpaRepository.findAll(
            NotificationSpecification.hasUser(user.getId())
                .and(NotificationSpecification.hasUserCategoryNullOrEquals(EUserCategory.STAFF)));

    BddLogger.then("STAFF and null are included; STUDENT is excluded");
    assertThat(results)
        .extracting(NotificationEntity::getId)
        .containsExactlyInAnyOrder(staffNotif.getId(), nullNotif.getId());
    assertThat(results).extracting(NotificationEntity::getId).doesNotContain(studentNotif.getId());
  }

  @Test
  void hasUserCategoryNullOrEquals_should_work_symmetrically_for_student_category() {
    BddLogger.given("Notifications with STAFF, STUDENT and null categories");
    NotificationEntity staffNotif = persistNotification(user, EUserCategory.STAFF);
    NotificationEntity studentNotif = persistNotification(user, EUserCategory.STUDENT);
    NotificationEntity nullNotif = persistNotification(user, null);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("hasUserCategoryNullOrEquals(STUDENT) spec is applied");
    List<NotificationEntity> results =
        notificationJpaRepository.findAll(
            NotificationSpecification.hasUser(user.getId())
                .and(NotificationSpecification.hasUserCategoryNullOrEquals(EUserCategory.STUDENT)));

    BddLogger.then("STUDENT and null are included; STAFF is excluded");
    assertThat(results)
        .extracting(NotificationEntity::getId)
        .containsExactlyInAnyOrder(studentNotif.getId(), nullNotif.getId());
    assertThat(results).extracting(NotificationEntity::getId).doesNotContain(staffNotif.getId());
  }

  @Test
  void combined_specification_should_scope_by_user_and_filter_by_category() {
    BddLogger.given(
        "STAFF notifications for user and otherUser, plus a null-category notification for user");
    NotificationEntity userStaff = persistNotification(user, EUserCategory.STAFF);
    NotificationEntity userNull = persistNotification(user, null);
    NotificationEntity otherStaff = persistNotification(otherUser, EUserCategory.STAFF);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("Combined hasUser + hasUserCategoryNullOrEquals(STAFF) is applied for user");
    List<NotificationEntity> results =
        notificationJpaRepository.findAll(
            NotificationSpecification.hasUser(user.getId())
                .and(NotificationSpecification.hasUserCategoryNullOrEquals(EUserCategory.STAFF)));

    BddLogger.then("Only user's STAFF and null-category notifications are returned");
    assertThat(results)
        .extracting(NotificationEntity::getId)
        .containsExactlyInAnyOrder(userStaff.getId(), userNull.getId());
    assertThat(results).extracting(NotificationEntity::getId).doesNotContain(otherStaff.getId());
  }

  @Test
  void isNotSeen_should_return_only_unseen_notifications() {
    BddLogger.given("One seen and one unseen notification for user");
    NotificationEntity seen = persistNotificationWithSeen(user, EUserCategory.STUDENT, true);
    NotificationEntity unseen = persistNotificationWithSeen(user, EUserCategory.STUDENT, false);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("isNotSeen spec is applied");
    List<NotificationEntity> results =
        notificationJpaRepository.findAll(
            NotificationSpecification.hasUser(user.getId())
                .and(NotificationSpecification.isNotSeen()));

    BddLogger.then("Only the unseen notification is returned");
    assertThat(results).extracting(NotificationEntity::getId).containsExactly(unseen.getId());
    assertThat(results).extracting(NotificationEntity::getId).doesNotContain(seen.getId());
  }

  @Test
  void isNotSeen_combined_with_all_specs_should_exclude_seen_notifications() {
    BddLogger.given("Mixed seen/unseen notifications for STAFF and null category");
    NotificationEntity seenStaff = persistNotificationWithSeen(user, EUserCategory.STAFF, true);
    NotificationEntity unseenStaff = persistNotificationWithSeen(user, EUserCategory.STAFF, false);
    NotificationEntity unseenNull = persistNotificationWithSeen(user, null, false);
    NotificationEntity unseenOtherUser =
        persistNotificationWithSeen(otherUser, EUserCategory.STAFF, false);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("hasUser + hasUserCategoryNullOrEquals(STAFF) + isNotSeen is applied for user");
    List<NotificationEntity> results =
        notificationJpaRepository.findAll(
            NotificationSpecification.hasUser(user.getId())
                .and(NotificationSpecification.hasUserCategoryNullOrEquals(EUserCategory.STAFF))
                .and(NotificationSpecification.isNotSeen()));

    BddLogger.then("Only unseen STAFF and null-category notifications for user are returned");
    assertThat(results)
        .extracting(NotificationEntity::getId)
        .containsExactlyInAnyOrder(unseenStaff.getId(), unseenNull.getId());
    assertThat(results)
        .extracting(NotificationEntity::getId)
        .doesNotContain(seenStaff.getId(), unseenOtherUser.getId());
  }

  @Test
  void countUnreadByUserAndCategory_should_return_correct_count_for_student() {
    BddLogger.given(
        "3 unseen STUDENT notifications, 1 seen STUDENT notification, and 1 unseen STAFF"
            + " notification for user");
    persistNotificationWithSeen(user, EUserCategory.STUDENT, false);
    persistNotificationWithSeen(user, EUserCategory.STUDENT, false);
    persistNotificationWithSeen(user, EUserCategory.STUDENT, false);
    persistNotificationWithSeen(user, EUserCategory.STUDENT, true);
    persistNotificationWithSeen(user, EUserCategory.STAFF, false);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("countUnreadByUserAndCategory is called for STUDENT");
    long count =
        notificationDatabaseRepository.countUnreadByUserAndCategory(
            user.getId(), EUserCategory.STUDENT);

    BddLogger.then("It should return 3");
    assertThat(count).isEqualTo(3);
  }

  @Test
  void countUnreadByUserAndCategory_should_include_null_category_notifications() {
    BddLogger.given("1 unseen STAFF and 1 unseen null-category notification for user");
    persistNotificationWithSeen(user, EUserCategory.STAFF, false);
    persistNotificationWithSeen(user, null, false);
    persistNotificationWithSeen(user, EUserCategory.STUDENT, false);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("countUnreadByUserAndCategory is called for STAFF");
    long count =
        notificationDatabaseRepository.countUnreadByUserAndCategory(
            user.getId(), EUserCategory.STAFF);

    BddLogger.then("It should return 2 (STAFF + null-category)");
    assertThat(count).isEqualTo(2);
  }

  @Test
  void countUnreadByUserAndCategory_should_not_count_other_users_notifications() {
    BddLogger.given("1 unseen STUDENT notification for user and 1 for otherUser");
    persistNotificationWithSeen(user, EUserCategory.STUDENT, false);
    persistNotificationWithSeen(otherUser, EUserCategory.STUDENT, false);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("countUnreadByUserAndCategory is called for user");
    long count =
        notificationDatabaseRepository.countUnreadByUserAndCategory(
            user.getId(), EUserCategory.STUDENT);

    BddLogger.then("It should return 1, excluding the other user's notification");
    assertThat(count).isEqualTo(1);
  }

  @Test
  void countUnreadByUserAndCategory_should_return_zero_when_all_notifications_are_seen() {
    BddLogger.given("Only seen STUDENT notifications for user");
    persistNotificationWithSeen(user, EUserCategory.STUDENT, true);
    persistNotificationWithSeen(user, EUserCategory.STUDENT, true);
    entityManager.flush();
    entityManager.clear();

    BddLogger.when("countUnreadByUserAndCategory is called for STUDENT");
    long count =
        notificationDatabaseRepository.countUnreadByUserAndCategory(
            user.getId(), EUserCategory.STUDENT);

    BddLogger.then("It should return 0");
    assertThat(count).isZero();
  }

  private UserEntity persistUser(String email) {
    UserEntity u =
        UserEntity.of(
            UUID.randomUUID(), "Firstname", "Lastname", email, false, Instant.now(), Instant.now());
    entityManager.persist(u);
    return u;
  }

  private NotificationEntity persistNotification(UserEntity owner, EUserCategory category) {
    return persistNotificationWithSeen(owner, category, false);
  }

  private NotificationEntity persistNotificationWithSeen(
      UserEntity owner, EUserCategory category, boolean seen) {
    NotificationEntity entity =
        NotificationEntity.of(
            UUID.randomUUID(),
            Instant.now(),
            Instant.now(),
            ENotificationType.ASK_FOR_FEEDBACK,
            UUID.randomUUID(),
            owner,
            category,
            List.of("param"),
            seen);
    entityManager.persist(entity);
    return entity;
  }
}
