package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserFileUploadEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserFileUploadJpaRepository
    extends JpaRepository<UserFileUploadEntity, UUID>,
        JpaSpecificationExecutor<UserFileUploadEntity> {}
