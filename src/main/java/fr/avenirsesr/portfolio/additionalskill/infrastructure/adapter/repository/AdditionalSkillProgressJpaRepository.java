package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdditionalSkillProgressJpaRepository
    extends JpaRepository<AdditionalSkillProgressEntity, UUID>,
        JpaSpecificationExecutor<AdditionalSkillProgressEntity> {}
