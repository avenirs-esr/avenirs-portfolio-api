package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeclaredSkillProgressJpaRepository
    extends JpaRepository<DeclaredSkillProgressEntity, UUID>,
        JpaSpecificationExecutor<DeclaredSkillProgressEntity> {}
