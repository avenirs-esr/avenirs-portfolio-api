package fr.avenirsesr.portfolio.notification.infrastructure.adapter.specification;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.model.NotificationEntity;
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

  private UserEntity persistUser(String email) {
    UserEntity u =
        UserEntity.of(
            UUID.randomUUID(), "Firstname", "Lastname", email, false, Instant.now(), Instant.now());
    entityManager.persist(u);
    return u;
  }

  private NotificationEntity persistNotification(UserEntity owner, EUserCategory category) {
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
            false);
    entityManager.persist(entity);
    return entity;
  }
}
