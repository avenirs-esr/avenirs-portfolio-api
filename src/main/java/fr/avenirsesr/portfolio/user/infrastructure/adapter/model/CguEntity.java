package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "cgu",
    indexes = {@Index(name = "idx_cgu_user_id", columnList = "user_id")},
    uniqueConstraints = {
      @UniqueConstraint(
          name = "cgu_user_version_uk",
          columnNames = {"user_id", "version_id"})
    })
@NoArgsConstructor
@Getter
@Setter
public class CguEntity extends AvenirsBaseEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(name = "version_id", nullable = false)
  private UUID versionId;

  @Column(name = "accepted_at", nullable = false)
  private Instant acceptedAt;

  private CguEntity(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UserEntity user,
      UUID versionId,
      Instant acceptedAt) {
    this.setId(id);
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
    this.user = user;
    this.versionId = versionId;
    this.acceptedAt = acceptedAt;
  }

  public static CguEntity of(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      UserEntity user,
      UUID versionId,
      Instant acceptedAt) {
    return new CguEntity(id, createdAt, updatedAt, user, versionId, acceptedAt);
  }
}
