package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.SkillLevelProgressEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SkillLevelProgressJpaRepository
    extends JpaRepository<SkillLevelProgressEntity, UUID>,
        JpaSpecificationExecutor<SkillLevelProgressEntity> {}
