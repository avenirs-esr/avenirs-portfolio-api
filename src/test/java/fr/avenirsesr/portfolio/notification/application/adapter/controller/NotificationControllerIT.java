package fr.avenirsesr.portfolio.notification.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.test.web.reactive.server.WebTestClient;

public class NotificationControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/notifications";

  @Autowired private WebTestClient webTestClient;
  @Autowired private NotificationJpaRepository notificationJpaRepository;
  @Autowired private UserJpaRepository userJpaRepository;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  private final String authorId = "0a8700ab-90b6-4a38-8338-acbdd4fbcd3d";

  private final String notFoundId = "00000000-0000-0000-0000-000000000000";
  private String notificationId;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();

    UserEntity user = userJpaRepository.findById(UUID.fromString(authorId)).orElseThrow();
    NotificationEntity entity =
        NotificationEntity.of(
            UUID.randomUUID(),
            null,
            null,
            ENotificationType.ASK_FOR_FEEDBACK,
            UUID.randomUUID(),
            user,
            ENotificationType.ASK_FOR_FEEDBACK.getRestrictedTo(),
            List.of("Prenom", "Nom", "titre de l'activité"),
            false);
    notificationId = notificationJpaRepository.save(entity).getId().toString();
  }

  @Test
  void markAsSeen_should_return_204_and_update_seen_flag() {
    BddLogger.given("An existing unseen notification");

    BddLogger.when("PATCH /me/notifications/{id}/seen is called");
    webTestClient
        .patch()
        .uri(BASE_PATH + "/" + notificationId + "/seen")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNoContent();

    BddLogger.then("The notification is marked as seen in the database");
    NotificationEntity updated =
        notificationJpaRepository.findById(UUID.fromString(notificationId)).orElseThrow();
    assertThat(updated.isSeen()).isTrue();
  }

  @Test
  void markAsSeen_should_return_404_when_notification_does_not_exist() {
    BddLogger.given("An ID that matches no notification");

    BddLogger.when("PATCH /me/notifications/{id}/seen is called with an unknown ID");
    webTestClient
        .patch()
        .uri(BASE_PATH + "/" + notFoundId + "/seen")
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Kid", secretKey)
        .header("X-Context-Signature", studentSignature)
        .exchange()
        .expectStatus()
        .isNotFound();
  }
}
