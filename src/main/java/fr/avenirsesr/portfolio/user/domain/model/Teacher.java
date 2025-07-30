package fr.avenirsesr.portfolio.user.domain.model;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Teacher {

  @Setter(AccessLevel.NONE)
  private final User user;

  private String bio;

  private Teacher(User user) {
    this.user = user;
  }

  public static Teacher create(User user) {
    return new Teacher(user);
  }

  public static Teacher of(User user, String bio) {
    var teacher = new Teacher(user);
    teacher.setBio(bio);

    return teacher;
  }

  public static Teacher toDomain(User user, String bio) {
    return of(user, bio);
  }

  public UUID getId() {
    return user.getId();
  }
}
