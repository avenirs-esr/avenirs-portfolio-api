package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdditionalSkillJpaRepository
    extends JpaRepository<AdditionalSkillEntity, UUID>,
        JpaSpecificationExecutor<AdditionalSkillEntity> {}
