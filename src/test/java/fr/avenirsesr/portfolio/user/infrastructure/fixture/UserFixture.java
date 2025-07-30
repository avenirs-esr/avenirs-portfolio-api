package fr.avenirsesr.portfolio.user.infrastructure.fixture;

import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeUser;
import java.util.UUID;

public class UserFixture {

  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private boolean isStudent;
  private String studentBio;
  private boolean isTeacher;
  private String teacherBio;

  private UserFixture() {
    var base = FakeUser.create().toEntity();
    this.id = base.getId();
    this.firstName = base.getFirstName();
    this.lastName = base.getLastName();
    this.email = base.getEmail();
    this.isStudent = false;
    this.studentBio = null;
    this.isTeacher = false;
    this.teacherBio = null;
  }

  public static UserFixture create() {
    return new UserFixture();
  }

  public static UserFixture createStudent() {
    return new UserFixture().asStudent("Student Bio");
  }

  public static UserFixture createTeacher() {
    return new UserFixture().asTeacher("Teacher Bio");
  }

  public UserFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public UserFixture withFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public UserFixture withLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public UserFixture withEmail(String email) {
    this.email = email;
    return this;
  }

  public UserFixture asStudent(String studentBio) {
    this.isStudent = true;
    this.studentBio = studentBio;
    return this;
  }

  public UserFixture asTeacher(String teacherBio) {
    this.isTeacher = true;
    this.teacherBio = teacherBio;
    return this;
  }

  public User toModel() {
    return User.toDomain(
        id, firstName, lastName, email, isStudent, studentBio, isTeacher, teacherBio);
  }
}
