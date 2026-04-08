package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.BIO_LENGTH;

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
@Table(name = "staff")
@NoArgsConstructor
@Getter
@Setter
public class StaffEntity extends AvenirsBaseEntity {
  @OneToOne private UserEntity user;

  @Column(length = BIO_LENGTH)
  private String bio;

  private StaffEntity(UUID id, UserEntity user, String bio) {
    setId(id);
    this.user = user;
    this.bio = bio;
  }

  public static StaffEntity of(UserEntity user, String bio) {
    return new StaffEntity(user.getId(), user, bio);
  }
}
