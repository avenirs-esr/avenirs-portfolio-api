package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SkillJpaRepository
    extends JpaRepository<SkillEntity, UUID>, JpaSpecificationExecutor<SkillEntity> {}
