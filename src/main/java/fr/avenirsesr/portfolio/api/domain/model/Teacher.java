package fr.avenirsesr.portfolio.api.domain.model;

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
  private byte[] profilePicture;
  private byte[] coverPicture;

  private Teacher(User user) {
    this.user = user;
  }

  public static Teacher create(User user) {
    return new Teacher(user);
  }

  public static Teacher toDomain(
      User user, String bio, byte[] profilePicture, byte[] coverPicture) {
    var teacher = new Teacher(user);
    teacher.setBio(bio);
    teacher.setProfilePicture(profilePicture);
    teacher.setCoverPicture(coverPicture);
    return teacher;
  }

  public UUID getId() {
    return user.getId();
  }
}
