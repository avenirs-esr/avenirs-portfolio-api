package fr.avenirsesr.portfolio.api.domain.model;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
  private final UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private boolean isStudent;
  private boolean isTeacher;

  // -- Student --
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.PRIVATE)
  private String studentBio;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.PRIVATE)
  private byte[] studentProfilePicture;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.PRIVATE)
  private byte[] studentCoverPicture;

  // -- Teacher --
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.PRIVATE)
  private String teacherBio;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.PRIVATE)
  private byte[] teacherProfilePicture;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.PRIVATE)
  private byte[] teacherCoverPicture;

  private User(UUID id) {
    this.id = id;
  }

  public static User create(String firstName, String lastName) {
    var user = new User(UUID.randomUUID());
    user.setFirstName(firstName);
    user.setLastName(lastName);

    return user;
  }

  public Student toStudent() {
    return Student.of(this, studentBio, studentProfilePicture, studentCoverPicture);
  }

  public Teacher toTeacher() {
    return Teacher.of(this, teacherBio, teacherProfilePicture, teacherCoverPicture);
  }

  public static User toDomain(
      UUID id,
      String firstName,
      String lastName,
      String email,
      String studentBio, byte[] studentProfilePicture, byte[] studentCoverPicture,
      String teacherBio, byte[]teacherProfilePicture, byte[] teacherCoverPicture
  ) {
    var user = new User(id);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);
    user.setStudentBio(studentBio);
    user.setStudentProfilePicture(studentProfilePicture);
    user.setStudentCoverPicture(studentCoverPicture);
    user.setTeacherBio(teacherBio);
    user.setTeacherProfilePicture(teacherProfilePicture);
    user.setTeacherCoverPicture(teacherCoverPicture);

    return user;
  }
}
