package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import java.time.Instant;
import java.util.UUID;
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

  private UserEntity(
      UUID id,
      String firstName,
      String lastName,
      String email,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static UserEntity of(
      UUID id,
      String firstName,
      String lastName,
      String email,
      Instant createdAt,
      Instant updatedAt) {
    return new UserEntity(id, firstName, lastName, email, createdAt, updatedAt);
  }
}
