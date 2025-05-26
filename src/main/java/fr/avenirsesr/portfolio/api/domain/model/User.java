package fr.avenirsesr.portfolio.api.domain.model;

import java.util.UUID;
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
  private String studentBio;
  private String studentProfilePicture;
  private String studentCoverPicture;

  // -- Teacher --
  private String teacherBio;
  private String teacherProfilePicture;
  private String teacherCoverPicture;

  private User(UUID id) {
    this.id = id;
  }

  public static User create(String firstName, String lastName) {
    var user = new User(UUID.randomUUID());
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setStudent(false);
    user.setTeacher(false);
    return user;
  }

  public static User toDomain(
      UUID id,
      String firstName,
      String lastName,
      String email,
      boolean isStudent,
      String studentBio,
      String studentProfilePicture,
      String studentCoverPicture,
      boolean isTeacher,
      String teacherBio,
      String teacherProfilePicture,
      String teacherCoverPicture) {
    var user = new User(id);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);
    user.setStudent(isStudent);
    user.setStudentBio(studentBio);
    user.setStudentProfilePicture(studentProfilePicture);
    user.setStudentCoverPicture(studentCoverPicture);
    user.setTeacher(isTeacher);
    user.setTeacherBio(teacherBio);
    user.setTeacherProfilePicture(teacherProfilePicture);
    user.setTeacherCoverPicture(teacherCoverPicture);

    return user;
  }

  public Student toStudent() {
    return Student.of(this, studentBio, studentProfilePicture, studentCoverPicture);
  }

  public Teacher toTeacher() {
    return Teacher.of(this, teacherBio, teacherProfilePicture, teacherCoverPicture);
  }
}
