package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends AvenirsBaseModel {
  private String firstName;
  private String lastName;
  private String email;
  private boolean isStudent;
  private boolean isTeacher;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.PRIVATE)
  private String studentBio;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.PRIVATE)
  private String teacherBio;

  private User(UUID id) {
    super(id);
  }

  public static User create(UUID id, String firstName, String lastName) {
    var user = new User(id);
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
      boolean isTeacher,
      String teacherBio) {
    var user = new User(id);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);
    user.setStudent(isStudent);
    user.setStudentBio(studentBio);
    user.setTeacher(isTeacher);
    user.setTeacherBio(teacherBio);

    return user;
  }

  public Student toStudent() {
    return Student.of(this, studentBio);
  }

  public Teacher toTeacher() {
    return Teacher.of(this, teacherBio);
  }
}
