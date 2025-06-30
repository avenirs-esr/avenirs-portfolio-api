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
  private String studentProfilePicture;
  private String studentCoverPicture;
  private boolean isTeacher;
  private String teacherBio;
  private String teacherProfilePicture;
  private String teacherCoverPicture;

  private UserFixture() {
    var base = FakeUser.create().toEntity();
    this.id = base.getId();
    this.firstName = base.getFirstName();
    this.lastName = base.getLastName();
    this.email = base.getEmail();
    this.isStudent = false;
    this.studentBio = null;
    this.studentProfilePicture = null;
    this.studentCoverPicture = null;
    this.isTeacher = false;
    this.teacherBio = null;
    this.teacherProfilePicture = null;
    this.teacherCoverPicture = null;
  }

  public static UserFixture create() {
    return new UserFixture();
  }

  public static UserFixture createStudent() {
    return new UserFixture().asStudent("Student Bio", "studentProfile.jpg", "studentCover.jpg");
  }

  public static UserFixture createTeacher() {
    return new UserFixture().asTeacher("Teacher Bio", "teacherProfile.jpg", "teacherCover.jpg");
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

  public UserFixture asStudent(String studentBio, String profilePicture, String coverPicture) {
    this.isStudent = true;
    this.studentBio = studentBio;
    this.studentProfilePicture = profilePicture;
    this.studentCoverPicture = coverPicture;
    return this;
  }

  public UserFixture asTeacher(String teacherBio, String profilePicture, String coverPicture) {
    this.isTeacher = true;
    this.teacherBio = teacherBio;
    this.teacherProfilePicture = profilePicture;
    this.teacherCoverPicture = coverPicture;
    return this;
  }

  public User toModel() {
    return User.toDomain(
        id,
        firstName,
        lastName,
        email,
        isStudent,
        studentBio,
        studentProfilePicture,
        studentCoverPicture,
        isTeacher,
        teacherBio,
        teacherProfilePicture,
        teacherCoverPicture);
  }
}
