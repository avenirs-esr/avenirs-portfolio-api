package fr.avenirsesr.portfolio.configuration.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.configuration.infrastructure.adapter.model.ConfigurationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfigurationJpaRepository
    extends JpaRepository<ConfigurationEntity, UUID>,
        JpaSpecificationExecutor<ConfigurationEntity> {}
