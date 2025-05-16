package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SkillLevelJpaRepository extends JpaRepository<SkillLevelEntity, UUID> {
}
