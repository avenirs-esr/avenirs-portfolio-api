package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"user\"")
@NoArgsConstructor
@Getter
@Setter
public class UserEntity extends AvenirsBaseEntity {
  @Column(nullable = false, name = "first_name")
  private String firstName;

  @Column(nullable = false, name = "last_name")
  private String lastName;

  @Email @Column private String email;

  @Getter(AccessLevel.NONE)
  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "bio", column = @Column(name = "student_bio")),
    @AttributeOverride(name = "isActive", column = @Column(name = "student_is_active")),
    @AttributeOverride(name = "profilePicture", column = @Column(name = "student_profile_picture")),
    @AttributeOverride(name = "coverPicture", column = @Column(name = "student_cover_picture"))
  })
  private StudentEntity student;

  @Getter(AccessLevel.NONE)
  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "bio", column = @Column(name = "teacher_bio")),
    @AttributeOverride(name = "isActive", column = @Column(name = "teacher_is_active")),
    @AttributeOverride(name = "profilePicture", column = @Column(name = "teacher_profile_picture")),
    @AttributeOverride(name = "coverPicture", column = @Column(name = "teacher_cover_picture"))
  })
  private TeacherEntity teacher;

  private UserEntity(
      UUID id,
      String firstName,
      String lastName,
      String email,
      StudentEntity student,
      TeacherEntity teacher) {
    this.setId(id);
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.student = student;
    this.teacher = teacher;
  }

  public static UserEntity of(
      UUID id,
      String firstName,
      String lastName,
      String email,
      StudentEntity student,
      TeacherEntity teacher) {
    return new UserEntity(id, firstName, lastName, email, student, teacher);
  }

  public Optional<StudentEntity> getStudent() {
    return Optional.ofNullable(student);
  }

  public Optional<TeacherEntity> getTeacher() {
    return Optional.ofNullable(teacher);
  }
}
