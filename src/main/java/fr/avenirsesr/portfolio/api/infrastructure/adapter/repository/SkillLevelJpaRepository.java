package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillLevelEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SkillLevelJpaRepository
    extends JpaRepository<SkillLevelEntity, UUID>, JpaSpecificationExecutor<SkillLevelEntity> {}
