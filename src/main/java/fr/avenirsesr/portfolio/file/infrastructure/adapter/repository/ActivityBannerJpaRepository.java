package fr.avenirsesr.portfolio.file.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.ActivityBannerEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityBannerJpaRepository
    extends JpaRepository<ActivityBannerEntity, UUID>,
        JpaSpecificationExecutor<ActivityBannerEntity> {}
