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
  private String profilePicture;
  private String coverPicture;

  private Teacher(User user) {
    this.user = user;
  }

  public static Teacher create(User user) {
    return new Teacher(user);
  }

  public static Teacher of(User user, String bio, String profilePicture, String coverPicture) {
    var teacher = new Teacher(user);
    teacher.setBio(bio);
    teacher.setProfilePicture(profilePicture);
    teacher.setCoverPicture(coverPicture);

    return teacher;
  }

  public static Teacher toDomain(
      User user, String bio, String profilePicture, String coverPicture) {
    return of(user, bio, profilePicture, coverPicture);
  }

  public UUID getId() {
    return user.getId();
  }
}
