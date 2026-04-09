package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.BIO_LENGTH;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
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

  @Email
  @Column(nullable = false, name = "institution_email")
  private String institutionEmail;

  private StaffEntity(UUID id, UserEntity user, String institutionEmail, String bio) {
    setId(id);
    this.user = user;
    this.bio = bio;
    this.institutionEmail = institutionEmail;
  }

  public static StaffEntity of(UserEntity user, String institutionEmail, String bio) {
    return new StaffEntity(user.getId(), user, institutionEmail, bio);
  }
}
