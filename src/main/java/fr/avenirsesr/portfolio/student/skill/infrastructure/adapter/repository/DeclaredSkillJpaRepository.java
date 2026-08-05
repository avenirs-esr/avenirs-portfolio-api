package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model.DeclaredSkillEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeclaredSkillJpaRepository
    extends JpaRepository<DeclaredSkillEntity, UUID>,
        JpaSpecificationExecutor<DeclaredSkillEntity> {}
