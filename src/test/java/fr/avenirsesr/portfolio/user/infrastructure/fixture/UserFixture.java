package fr.avenirsesr.portfolio.user.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import java.time.Instant;
import java.util.UUID;

public class UserFixture {

  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private boolean notificationEnabled;
  private Instant createdAt;
  private Instant updatedAt;

  private UserFixture() {
    this.id = UUID.randomUUID();
    this.firstName = "firstName";
    this.lastName = "lastName";
    this.email = "user@email.com";
    this.notificationEnabled = false;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static UserFixture create() {
    return new UserFixture();
  }

  public UserFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public UserFixture withFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public UserFixture withLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public UserFixture withEmail(String email) {
    this.email = email;
    return this;
  }

  public UserFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public UserFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public UserFixture withNotificationEnabled(boolean notificationEnabled) {
    this.notificationEnabled = notificationEnabled;
    return this;
  }

  public User toModel() {
    return User.toDomain(id, firstName, lastName, email, notificationEnabled, createdAt, updatedAt);
  }
}
