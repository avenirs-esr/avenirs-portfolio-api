package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Teacher extends AvenirsBaseModel {
  @Setter(AccessLevel.NONE)
  private final User user;

  private String bio;

  private Teacher(User user, String bio, Instant createdAt, Instant updatedAt) {
    super(user.getId(), createdAt, updatedAt);
    this.user = user;
    this.bio = bio;
  }

  public static Teacher create(User user, String bio) {
    return new Teacher(user, bio, Instant.now(), Instant.now());
  }

  public static Teacher toDomain(User user, String bio, Instant createdAt, Instant updatedAt) {
    return new Teacher(user, bio, createdAt, updatedAt);
  }
}
