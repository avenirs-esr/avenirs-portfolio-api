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
@Table(name = "teacher")
@NoArgsConstructor
@Getter
@Setter
public class TeacherEntity extends AvenirsBaseEntity {
  @OneToOne private UserEntity user;
  @Column private String bio;

  private TeacherEntity(UUID id, UserEntity user, String bio) {
    setId(id);
    this.user = user;
    this.bio = bio;
  }

  public static TeacherEntity of(UserEntity user, String bio) {
    return new TeacherEntity(user.getId(), user, bio);
  }
}
