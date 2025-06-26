package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"user\"")
@AllArgsConstructor
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
    @AttributeOverride(name = "isActive", column = @Column(name = "student_is_active")),
    @AttributeOverride(name = "profilePicture", column = @Column(name = "student_profile_picture")),
    @AttributeOverride(name = "coverPicture", column = @Column(name = "student_cover_picture"))
  })
  private StudentEntity student;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "bio", column = @Column(name = "teacher_bio")),
    @AttributeOverride(name = "isActive", column = @Column(name = "teacher_is_active")),
    @AttributeOverride(name = "profilePicture", column = @Column(name = "teacher_profile_picture")),
    @AttributeOverride(name = "coverPicture", column = @Column(name = "teacher_cover_picture"))
  })
  private TeacherEntity teacher;
}
