package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student extends AvenirsBaseModel {
  @Setter(AccessLevel.NONE)
  private final User user;

  @Setter(AccessLevel.NONE)
  private String institutionEmail;

  private String bio;

  private Student(
      User user, String institutionEmail, String bio, Instant createdAt, Instant updatedAt) {
    super(user.getId(), createdAt, updatedAt);
    this.user = user;
    this.institutionEmail = institutionEmail;
    this.bio = bio;
  }

  public static Student create(User user, String institutionEmail, String bio) {
    return new Student(user, institutionEmail, bio, Instant.now(), Instant.now());
  }

  public static Student toDomain(
      User user, String institutionEmail, String bio, Instant createdAt, Instant updatedAt) {
    return new Student(user, institutionEmail, bio, createdAt, updatedAt);
  }
}
