package fr.avenirsesr.portfolio.user.domain.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {
  private final User user;
  private String bio;

  private Student(User user) {
    this.user = user;
  }

  public static Student create(User user) {
    return new Student(user);
  }

  public static Student of(User user, String bio) {
    var student = new Student(user);
    student.setBio(bio);

    return student;
  }

  public static Student toDomain(User user, String bio) {
    return of(user, bio);
  }

  public UUID getId() {
    return user.getId();
  }
}
