package fr.avenirsesr.portfolio.skill.infrastructure.adapter.repository;

import java.util.UUID;

public interface SkillParentProjection {
  UUID getId();

  UUID getParentId();
}
