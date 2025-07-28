package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserUploadEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserUploadJpaRepository
    extends JpaRepository<UserUploadEntity, UUID>, JpaSpecificationExecutor<UserUploadEntity> {}
