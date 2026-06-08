package fr.avenirsesr.portfolio.notification.infrastructure.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.model.NotificationEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationMapperTest {

  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;
  private ENotificationType type;
  private UUID elementId;
  private User user;
  private UserEntity userEntity;
  private List<String> parameters;
  private boolean seen;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    createdAt = Instant.parse("2026-01-01T10:00:00Z");
    updatedAt = Instant.parse("2026-01-02T10:00:00Z");
    type = ENotificationType.ASK_FOR_FEEDBACK;
    elementId = UUID.randomUUID();
    parameters = List.of("Alice", "Martin", "Activité de test");
    seen = false;
    user = UserFixture.create().toModel();
    userEntity = UserMapper.INSTANCE.fromDomain(user);
  }

  @Test
  void fromDomain_should_map_all_fields_to_entity() {
    BddLogger.given("A Notification domain object with all fields set");
    Notification domain =
        Notification.toDomain(id, createdAt, updatedAt, type, elementId, user, parameters, seen);

    BddLogger.when("fromDomain is called");
    NotificationEntity entity = NotificationMapper.INSTANCE.fromDomain(domain);

    BddLogger.then("All fields are correctly mapped to the entity");
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
    assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(entity.getType()).isEqualTo(type);
    assertThat(entity.getElementId()).isEqualTo(elementId);
    assertThat(entity.getUser().getId()).isEqualTo(user.getId());
    assertThat(entity.getParameters()).isEqualTo(parameters);
    assertThat(entity.isSeen()).isEqualTo(seen);
  }

  @Test
  void toDomain_should_map_all_fields_to_domain() {
    BddLogger.given("A NotificationEntity with all fields set");
    NotificationEntity entity =
        NotificationEntity.of(
            id, createdAt, updatedAt, type, elementId, userEntity, parameters, seen);

    BddLogger.when("toDomain is called");
    Notification domain = NotificationMapper.INSTANCE.toDomain(entity);

    BddLogger.then("All fields are correctly mapped to the domain model");
    assertThat(domain.getId()).isEqualTo(id);
    assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
    assertThat(domain.getUpdatedAt()).isEqualTo(updatedAt);
    assertThat(domain.getType()).isEqualTo(type);
    assertThat(domain.getElementId()).isEqualTo(elementId);
    assertThat(domain.getUser().getId()).isEqualTo(userEntity.getId());
    assertThat(domain.getParameters()).isEqualTo(parameters);
    assertThat(domain.isSeen()).isEqualTo(seen);
  }

  @Test
  void fromDomain_then_toDomain_should_produce_equivalent_domain_object() {
    BddLogger.given("A Notification domain object");
    Notification original =
        Notification.toDomain(id, createdAt, updatedAt, type, elementId, user, parameters, seen);

    BddLogger.when("mapped to entity and back to domain");
    Notification roundTripped =
        NotificationMapper.INSTANCE.toDomain(NotificationMapper.INSTANCE.fromDomain(original));

    BddLogger.then("The round-tripped domain object is equivalent to the original");
    assertThat(roundTripped.getId()).isEqualTo(original.getId());
    assertThat(roundTripped.getType()).isEqualTo(original.getType());
    assertThat(roundTripped.getElementId()).isEqualTo(original.getElementId());
    assertThat(roundTripped.getUser().getId()).isEqualTo(original.getUser().getId());
    assertThat(roundTripped.getParameters()).isEqualTo(original.getParameters());
    assertThat(roundTripped.isSeen()).isEqualTo(original.isSeen());
    assertThat(roundTripped.getCreatedAt()).isEqualTo(original.getCreatedAt());
    assertThat(roundTripped.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
  }

  @Test
  void fromDomain_should_correctly_map_seen_true() {
    BddLogger.given("A Notification with seen=true");
    Notification domain =
        Notification.toDomain(id, createdAt, updatedAt, type, elementId, user, parameters, true);

    BddLogger.when("fromDomain is called");
    NotificationEntity entity = NotificationMapper.INSTANCE.fromDomain(domain);

    BddLogger.then("The entity has seen=true");
    assertThat(entity.isSeen()).isTrue();
  }
}
