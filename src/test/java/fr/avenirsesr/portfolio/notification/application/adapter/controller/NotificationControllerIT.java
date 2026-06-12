package fr.avenirsesr.portfolio.notification.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.model.NotificationEntity;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.repository.NotificationJpaRepository;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.UserJpaRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class NotificationControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/notifications";

  // lucas.tessier@university.com (student)
  private static final String STUDENT_USER_ID = "0a8700ab-90b6-4a38-8338-acbdd4fbcd3d";
  private static final String NOT_FOUND_ID = "00000000-0000-0000-0000-000000000000";

  @Autowired private WebTestClient webTestClient;
  @Autowired private NotificationJpaRepository notificationJpaRepository;
  @Autowired private UserJpaRepository userJpaRepository;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  private String staffNotificationId;
  private String studentNotificationId;
  private String universalNotificationId;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();

    UserEntity user = userJpaRepository.findById(UUID.fromString(STUDENT_USER_ID)).orElseThrow();

    staffNotificationId =
        notificationJpaRepository
            .save(
                NotificationEntity.of(
                    UUID.randomUUID(),
                    null,
                    null,
                    ENotificationType.ASK_FOR_FEEDBACK,
                    UUID.randomUUID(),
                    user,
                    EUserCategory.STAFF,
                    List.of("Prenom", "Nom", "titre de l'activité"),
                    false))
            .getId()
            .toString();

    studentNotificationId =
        notificationJpaRepository
            .save(
                NotificationEntity.of(
                    UUID.randomUUID(),
                    null,
                    null,
                    ENotificationType.ASK_FOR_FEEDBACK,
                    UUID.randomUUID(),
                    user,
                    EUserCategory.STUDENT,
                    List.of("Prenom", "Nom", "titre étudiant"),
                    false))
            .getId()
            .toString();

    universalNotificationId =
        notificationJpaRepository
            .save(
                NotificationEntity.of(
                    UUID.randomUUID(),
                    null,
                    null,
                    ENotificationType.ASK_FOR_FEEDBACK,
                    UUID.randomUUID(),
                    user,
                    null,
                    List.of("notification universelle"),
                    false))
            .getId()
            .toString();
  }

  // ── markAsSeen ──────────────────────────────────────────────────────────────

  @Test
  void markAsSeen_should_return_204_and_update_seen_flag() {
    BddLogger.given("An existing unseen STAFF notification");

    BddLogger.when("PATCH /me/notifications/{id}/seen is called");
    webTestClient
        .patch()
        .uri(BASE_PATH + "/" + staffNotificationId + "/seen")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("The notification is marked as seen in the database");
    NotificationEntity updated =
        notificationJpaRepository.findById(UUID.fromString(staffNotificationId)).orElseThrow();
    assertThat(updated.isSeen()).isTrue();
  }

  @Test
  void markAsSeen_should_return_404_when_notification_does_not_exist() {
    BddLogger.given("An ID that matches no notification");

    BddLogger.when("PATCH /me/notifications/{id}/seen is called with an unknown ID");
    webTestClient
        .patch()
        .uri(BASE_PATH + "/" + NOT_FOUND_ID + "/seen")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  // ── getNotifications ────────────────────────────────────────────────────────

  @Test
  void getNotifications_should_return_student_and_universal_notifications_for_student_category() {
    BddLogger.given("A student user with STAFF, STUDENT and null-category notifications");

    BddLogger.when("GET /me/notifications/STUDENT is called");
    webTestClient
        .get()
        .uri(BASE_PATH + "/STUDENT")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data")
        .isArray()
        .jsonPath("$.page.totalElements")
        .isEqualTo(2);
  }

  @Test
  void getNotifications_should_not_return_non_matching_category() {
    BddLogger.given("A student user whose STUDENT notification should not appear in a STAFF query");

    BddLogger.when("GET /me/notifications/STAFF is called");
    webTestClient
        .get()
        .uri(BASE_PATH + "/STAFF")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data[?(@.id == '" + studentNotificationId + "')]")
        .doesNotExist()
        .jsonPath("$.data[?(@.id == '" + staffNotificationId + "')]")
        .exists()
        .jsonPath("$.data[?(@.id == '" + universalNotificationId + "')]")
        .exists();
  }

  @Test
  void getNotifications_should_return_401_when_no_auth_headers() {
    BddLogger.given("A request with no authentication headers");

    BddLogger.when("GET /me/notifications/STAFF is called without auth");
    webTestClient
        .get()
        .uri(BASE_PATH + "/STAFF")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void getNotifications_should_return_dto_fields_without_user_category() {
    BddLogger.given("A student user with a STAFF notification");

    BddLogger.when("GET /me/notifications/STAFF is called");
    webTestClient
        .get()
        .uri(BASE_PATH + "/STAFF")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.data[0].id")
        .exists()
        .jsonPath("$.data[0].type")
        .isEqualTo("ASK_FOR_FEEDBACK")
        .jsonPath("$.data[0].seen")
        .isEqualTo(false)
        .jsonPath("$.data[0].userCategory")
        .doesNotExist();
  }
}
