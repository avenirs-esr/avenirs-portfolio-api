package fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSTranslationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AMSTranslationJpaRepository
    extends JpaRepository<AMSTranslationEntity, UUID>,
        JpaSpecificationExecutor<AMSTranslationEntity> {}
