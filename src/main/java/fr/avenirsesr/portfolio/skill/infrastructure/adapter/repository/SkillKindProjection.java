package fr.avenirsesr.portfolio.skill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import java.util.UUID;

public interface SkillKindProjection {
  UUID getSkillId();

  ESkillKind getKind();
}
