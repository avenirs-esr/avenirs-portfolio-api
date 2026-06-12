package fr.avenirsesr.portfolio.notification.application.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.application.adapter.dto.NotificationDTO;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationResponseMapperTest {

  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;
  private ENotificationType type;
  private UUID elementId;
  private EUserCategory userCategory;
  private List<String> parameters;
  private User user;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    createdAt = Instant.parse("2026-01-01T10:00:00Z");
    updatedAt = Instant.parse("2026-01-02T10:00:00Z");
    type = ENotificationType.ASK_FOR_FEEDBACK;
    elementId = UUID.randomUUID();
    userCategory = EUserCategory.STAFF;
    parameters = List.of("Alice", "Martin", "Activité de test");
    user = UserFixture.create().toModel();
  }

  @Test
  void toDTO_should_map_all_exposed_fields_correctly() {
    BddLogger.given("A Notification domain object with all fields set");
    Notification notification =
        Notification.toDomain(
            id, createdAt, updatedAt, type, elementId, user, userCategory, parameters, false);

    BddLogger.when("toDTO is called");
    NotificationDTO dto = NotificationResponseMapper.INSTANCE.toDTO(notification);

    BddLogger.then("All DTO fields match the source notification");
    assertThat(dto.id()).isEqualTo(id);
    assertThat(dto.createdAt()).isEqualTo(createdAt);
    assertThat(dto.type()).isEqualTo(type);
    assertThat(dto.elementId()).isEqualTo(elementId);
    assertThat(dto.parameters()).isEqualTo(parameters);
    assertThat(dto.seen()).isFalse();
  }

  @Test
  void toDTO_should_map_seen_true() {
    BddLogger.given("A Notification with seen=true");
    Notification notification =
        Notification.toDomain(
            id, createdAt, updatedAt, type, elementId, user, userCategory, parameters, true);

    BddLogger.when("toDTO is called");
    NotificationDTO dto = NotificationResponseMapper.INSTANCE.toDTO(notification);

    BddLogger.then("The DTO has seen=true");
    assertThat(dto.seen()).isTrue();
  }

  @Test
  void toDTO_should_map_empty_parameters_list() {
    BddLogger.given("A Notification with an empty parameters list");
    Notification notification =
        Notification.toDomain(
            id, createdAt, updatedAt, type, elementId, user, userCategory, List.of(), false);

    BddLogger.when("toDTO is called");
    NotificationDTO dto = NotificationResponseMapper.INSTANCE.toDTO(notification);

    BddLogger.then("The DTO has an empty parameters list");
    assertThat(dto.parameters()).isEmpty();
  }

  @Test
  void toDTO_should_preserve_id_and_element_id_independently() {
    BddLogger.given("A Notification where id and elementId are different UUIDs");
    UUID distinctElementId = UUID.randomUUID();
    Notification notification =
        Notification.toDomain(
            id,
            createdAt,
            updatedAt,
            type,
            distinctElementId,
            user,
            userCategory,
            parameters,
            false);

    BddLogger.when("toDTO is called");
    NotificationDTO dto = NotificationResponseMapper.INSTANCE.toDTO(notification);

    BddLogger.then("The id and elementId in the DTO remain distinct");
    assertThat(dto.id()).isEqualTo(id);
    assertThat(dto.elementId()).isEqualTo(distinctElementId);
    assertThat(dto.id()).isNotEqualTo(dto.elementId());
  }
}
