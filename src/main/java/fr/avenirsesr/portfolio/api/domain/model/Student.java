package fr.avenirsesr.portfolio.api.domain.model;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

  @Setter(AccessLevel.NONE)
  private final User user;

  private String bio;
  private String profilePicture;
  private String coverPicture;

  private Student(User user) {
    this.user = user;
  }

  public static Student create(User user) {
    return new Student(user);
  }

  public static Student of(User user, String bio, String profilePicture, String coverPicture) {
    var student = new Student(user);
    student.setBio(bio);
    student.setProfilePicture(profilePicture);
    student.setCoverPicture(coverPicture);

    return student;
  }

  public static Student toDomain(
      User user, String bio, String profilePicture, String coverPicture) {
    return of(user, bio, profilePicture, coverPicture);
  }

  public UUID getId() {
    return user.getId();
  }
}
