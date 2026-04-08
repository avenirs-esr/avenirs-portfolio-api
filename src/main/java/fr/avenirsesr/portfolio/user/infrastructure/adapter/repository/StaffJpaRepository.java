package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StaffJpaRepository
    extends JpaRepository<StaffEntity, UUID>, JpaSpecificationExecutor<StaffEntity> {}
