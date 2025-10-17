package fr.avenirsesr.portfolio.user.infrastructure.fixture;

import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeUser;
import java.time.Instant;
import java.util.UUID;

public class UserFixture {

  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private Instant createdAt;
  private Instant updatedAt;

  private UserFixture() {
    var base = FakeUser.create().toEntity();
    this.id = base.getId();
    this.firstName = base.getFirstName();
    this.lastName = base.getLastName();
    this.email = base.getEmail();
    this.createdAt = base.getCreatedAt();
    this.updatedAt = base.getUpdatedAt();
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

  public User toModel() {
    return User.toDomain(id, firstName, lastName, email, createdAt, updatedAt);
  }
}
