package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeElementEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SelfKnowledgeElementJpaRepository
    extends JpaRepository<SelfKnowledgeElementEntity, UUID>,
        JpaSpecificationExecutor<SelfKnowledgeElementEntity> {}
