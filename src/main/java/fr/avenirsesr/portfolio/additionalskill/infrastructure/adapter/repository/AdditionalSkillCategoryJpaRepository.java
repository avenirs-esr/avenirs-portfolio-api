package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillCategoryEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdditionalSkillCategoryJpaRepository
    extends JpaRepository<AdditionalSkillCategoryEntity, UUID>,
        JpaSpecificationExecutor<AdditionalSkillCategoryEntity> {}
