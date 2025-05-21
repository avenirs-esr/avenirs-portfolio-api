package fr.avenirsesr.portfolio.api.domain.model;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {
  private final User user;

  private String bio;
  private byte[] profilePicture;
  private byte[] coverPicture;

  private Student(User user) {
    this.user = user;
  }

  public static Student create(User user) {
    return new Student(user);
  }

  public static Student of(User user, String bio, byte[] profilePicture, byte[] coverPicture) {
    var student = new Student(user);
    student.setBio(bio);
    student.setProfilePicture(profilePicture);
    student.setCoverPicture(coverPicture);

    return student;
  }

  public static Student toDomain(
      User user, String bio, byte[] profilePicture, byte[] coverPicture) {
    return of(user,bio,profilePicture,coverPicture);
  }

  public UUID getId() {
    return user.getId();
  }
}
