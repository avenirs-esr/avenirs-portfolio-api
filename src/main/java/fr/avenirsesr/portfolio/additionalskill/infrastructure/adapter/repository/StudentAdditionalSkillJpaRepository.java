package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.StudentAdditionalSkillEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentAdditionalSkillJpaRepository
    extends JpaRepository<StudentAdditionalSkillEntity, UUID>,
        JpaSpecificationExecutor<StudentAdditionalSkillEntity> {}
