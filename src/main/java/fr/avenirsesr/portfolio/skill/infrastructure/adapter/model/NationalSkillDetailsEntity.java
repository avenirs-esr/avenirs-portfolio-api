package fr.avenirsesr.portfolio.skill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
    name = "national_skill_details",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_national_skill_details_skill",
          columnNames = {"skill_id"})
    })
@NoArgsConstructor
@Getter
@Setter
public class NationalSkillDetailsEntity extends AvenirsBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "skill_id", nullable = false)
  private SkillEntity skill;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ESkillKind kind;

  private NationalSkillDetailsEntity(
      UUID id, SkillEntity skill, ESkillKind kind, Instant createdAt, Instant updatedAt) {
    setId(id);
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);
    this.skill = skill;
    this.kind = kind;
  }

  public static NationalSkillDetailsEntity of(
      UUID id, SkillEntity skill, ESkillKind kind, Instant createdAt, Instant updatedAt) {
    return new NationalSkillDetailsEntity(id, skill, kind, createdAt, updatedAt);
  }
}
