package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student")
@NoArgsConstructor
@Getter
@Setter
public class StudentEntity extends AvenirsBaseEntity {
  @OneToOne private UserEntity user;
  @Column private String bio;

  private StudentEntity(UUID id, UserEntity user, String bio) {
    setId(id);
    this.user = user;
    this.bio = bio;
  }

  public static StudentEntity of(UserEntity user, String bio) {
    return new StudentEntity(user.getId(), user, bio);
  }
}
