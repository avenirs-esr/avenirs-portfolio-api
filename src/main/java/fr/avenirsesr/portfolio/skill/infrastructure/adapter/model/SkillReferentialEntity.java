package fr.avenirsesr.portfolio.skill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "skill_referential",
    indexes = {
      @Index(name = "idx_skill_referential_institution", columnList = "institution_id"),
      @Index(name = "idx_skill_referential_type", columnList = "type")
    })
@NoArgsConstructor
@Getter
@Setter
public class SkillReferentialEntity extends AvenirsBaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ESkillReferentialType type;

  @Column(name = "external_id", nullable = false)
  private String externalId;

  @Column(nullable = false)
  private String title;

  @Column(name = "institution_id")
  private UUID institutionId;

  private SkillReferentialEntity(
      UUID id,
      ESkillReferentialType type,
      String externalId,
      String title,
      UUID institutionId,
      Instant createdAt,
      Instant updatedAt) {
    setId(id);
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);
    this.type = type;
    this.externalId = externalId;
    this.title = title;
    this.institutionId = institutionId;
  }

  public static SkillReferentialEntity of(
      UUID id,
      ESkillReferentialType type,
      String externalId,
      String title,
      UUID institutionId,
      Instant createdAt,
      Instant updatedAt) {
    return new SkillReferentialEntity(
        id, type, externalId, title, institutionId, createdAt, updatedAt);
  }
}
