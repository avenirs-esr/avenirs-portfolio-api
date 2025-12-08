package fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.model.DeclaredExperienceEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeclaredExperienceJpaRepository
    extends JpaRepository<DeclaredExperienceEntity, UUID>,
        JpaSpecificationExecutor<DeclaredExperienceEntity> {}
