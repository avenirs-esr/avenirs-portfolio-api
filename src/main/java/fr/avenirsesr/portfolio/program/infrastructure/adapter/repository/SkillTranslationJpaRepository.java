package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillTranslationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SkillTranslationJpaRepository
    extends JpaRepository<SkillTranslationEntity, UUID>,
        JpaSpecificationExecutor<SkillTranslationEntity> {}
