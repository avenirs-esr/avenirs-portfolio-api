package fr.avenirsesr.portfolio.skill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    name = "skill",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_skill_referential_external_code",
          columnNames = {"referential_id", "external_code"})
    },
    indexes = {
      @Index(name = "idx_skill_referential", columnList = "referential_id"),
      @Index(name = "idx_skill_parent", columnList = "parent_id")
    })
@NoArgsConstructor
@Getter
@Setter
public class SkillEntity extends AvenirsBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "referential_id", nullable = false)
  private SkillReferentialEntity referential;

  @Column(name = "external_code")
  private String externalCode;

  @Column(nullable = false)
  private String title;

  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private SkillEntity parent;

  @Column(nullable = false)
  private boolean assessable;

  @Column(nullable = false)
  private boolean archived;

  private SkillEntity(
      UUID id,
      SkillReferentialEntity referential,
      String externalCode,
      String title,
      String description,
      SkillEntity parent,
      boolean assessable,
      boolean archived,
      Instant createdAt,
      Instant updatedAt) {
    setId(id);
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);
    this.referential = referential;
    this.externalCode = externalCode;
    this.title = title;
    this.description = description;
    this.parent = parent;
    this.assessable = assessable;
    this.archived = archived;
  }

  public static SkillEntity of(
      UUID id,
      SkillReferentialEntity referential,
      String externalCode,
      String title,
      String description,
      SkillEntity parent,
      boolean assessable,
      boolean archived,
      Instant createdAt,
      Instant updatedAt) {
    return new SkillEntity(
        id,
        referential,
        externalCode,
        title,
        description,
        parent,
        assessable,
        archived,
        createdAt,
        updatedAt);
  }
}
