package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.Teacher;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class TeacherEntity {
  @Column private String bio;
  @Column @Lob private byte[] profilePicture;
  @Column @Lob private byte[] coverPicture;

  public static TeacherEntity fromDomain(Teacher teacher) {
    return new TeacherEntity(
        teacher.getBio(), teacher.getProfilePicture(), teacher.getCoverPicture());
  }

  public static Teacher toDomain(TeacherEntity teacherEntity, UserEntity userEntity) {
    return Teacher.toDomain(
            UserEntity.toDomain(userEntity),
            teacherEntity.getBio(),
            teacherEntity.getProfilePicture(),
            teacherEntity.getCoverPicture()
    );
  }
}
