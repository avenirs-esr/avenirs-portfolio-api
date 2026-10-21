package fr.avenirsesr.portfolio.skill.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NationalSkillDetails extends AvenirsBaseModel {
  private Skill skill;
  private ESkillKind kind;

  private NationalSkillDetails(
      UUID id, Skill skill, ESkillKind kind, Instant createdAt, Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.skill = skill;
    this.kind = kind;
  }

  public static NationalSkillDetails create(Skill skill, ESkillKind kind) {
    Instant now = Instant.now();
    return new NationalSkillDetails(UUID.randomUUID(), skill, kind, now, now);
  }

  public static NationalSkillDetails toDomain(
      UUID id, Skill skill, ESkillKind kind, Instant createdAt, Instant updatedAt) {
    return new NationalSkillDetails(id, skill, kind, createdAt, updatedAt);
  }
}
