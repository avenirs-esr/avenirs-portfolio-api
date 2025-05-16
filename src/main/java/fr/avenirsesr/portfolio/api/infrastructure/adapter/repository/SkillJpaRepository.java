package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SkillJpaRepository extends JpaRepository<SkillEntity, UUID> {
}
