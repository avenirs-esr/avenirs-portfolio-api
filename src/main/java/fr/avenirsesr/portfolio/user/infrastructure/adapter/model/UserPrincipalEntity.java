package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "user_principal",
    indexes = {@Index(name = "idx_user_principal_eppn", columnList = "eppn")})
@NoArgsConstructor
@Getter
@Setter
public class UserPrincipalEntity extends AvenirsBaseEntity {

  @OneToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private UserEntity user;

  @Column(nullable = false, unique = true)
  private String eppn;

  private UserPrincipalEntity(
      UUID id, UserEntity user, String eppn, Instant createdAt, Instant updatedAt) {

    this.setId(id);
    this.user = user;
    this.eppn = eppn;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static UserPrincipalEntity of(
      UUID id, UserEntity user, String eppn, Instant createdAt, Instant updatedAt) {

    return new UserPrincipalEntity(id, user, eppn, createdAt, updatedAt);
  }
}
