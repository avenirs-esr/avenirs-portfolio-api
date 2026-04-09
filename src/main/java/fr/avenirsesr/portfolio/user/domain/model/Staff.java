package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Staff extends AvenirsBaseModel {
  @Setter(AccessLevel.NONE)
  private final User user;

  private String bio;
  private String institutionEmail;

  private Staff(
      User user, String institutionEmail, String bio, Instant createdAt, Instant updatedAt) {
    super(user.getId(), createdAt, updatedAt);
    this.user = user;
    this.bio = bio;
    this.institutionEmail = institutionEmail;
  }

  public static Staff create(User user, String institutionEmail, String bio) {
    return new Staff(user, institutionEmail, bio, Instant.now(), Instant.now());
  }

  public static Staff toDomain(
      User user, String institutionEmail, String bio, Instant createdAt, Instant updatedAt) {
    return new Staff(user, institutionEmail, bio, createdAt, updatedAt);
  }
}
