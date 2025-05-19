package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillJpaRepository extends JpaRepository<SkillEntity, UUID> {}
