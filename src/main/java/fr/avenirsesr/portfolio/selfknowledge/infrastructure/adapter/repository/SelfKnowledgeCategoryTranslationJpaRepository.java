package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryTranslationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SelfKnowledgeCategoryTranslationJpaRepository
    extends JpaRepository<SelfKnowledgeCategoryTranslationEntity, UUID>,
        JpaSpecificationExecutor<SelfKnowledgeCategoryTranslationEntity> {}
