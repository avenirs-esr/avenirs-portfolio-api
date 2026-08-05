package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityJpaRepository
    extends JpaRepository<ActivityEntity, UUID>, JpaSpecificationExecutor<ActivityEntity> {}
