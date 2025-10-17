package fr.avenirsesr.portfolio.user.infrastructure.fixture;

import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.time.Instant;
import java.util.UUID;

public class StudentFixture {
  private UUID id;
  private String bio;
  private User user;
  private Instant createdAt;
  private Instant updatedAt;

  private StudentFixture() {
    this.user = UserFixture.create().toModel();
    this.id = user.getId();
    this.bio = "this is my student bio";
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static StudentFixture create() {
    return new StudentFixture();
  }

  public StudentFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public StudentFixture withBio(String bio) {
    this.bio = bio;
    return this;
  }

  public StudentFixture withUser(User user) {
    this.user = user;
    return this;
  }

  public StudentFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public StudentFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public Student toModel() {
    return Student.toDomain(user, bio, createdAt, updatedAt);
  }
}
