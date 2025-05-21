package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class UserEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Email @Column private String email;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "bio", column = @Column(name = "student_bio")),
    @AttributeOverride(name = "profilePicture", column = @Column(name = "student_profile_picture")),
    @AttributeOverride(name = "coverPicture", column = @Column(name = "student_cover_picture"))
  })
  private StudentEntity student;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "bio", column = @Column(name = "teacher_bio")),
    @AttributeOverride(name = "profilePicture", column = @Column(name = "teacher_profile_picture")),
    @AttributeOverride(name = "coverPicture", column = @Column(name = "teacher_cover_picture"))
  })
  private TeacherEntity teacher;

  public static UserEntity fromDomain(User user) {
    return new UserEntity(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        StudentEntity.fromDomain(user.toStudent()),
        TeacherEntity.fromDomain(user.toTeacher()));
  }

  public static User toDomain(UserEntity userEntity) {
    return User.toDomain(
        userEntity.getId(),
        userEntity.getFirstName(),
        userEntity.getLastName(),
        userEntity.getEmail(),
        userEntity.getStudent().getBio(),
        userEntity.getStudent().getProfilePicture(),
        userEntity.getStudent().getCoverPicture(),
        userEntity.getTeacher().getBio(),
        userEntity.getTeacher().getProfilePicture(),
        userEntity.getTeacher().getCoverPicture());
  }
}
