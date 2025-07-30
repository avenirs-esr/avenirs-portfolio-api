package fr.avenirsesr.portfolio.file.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserPhotoJpaRepository
    extends JpaRepository<UserPhotoEntity, UUID>, JpaSpecificationExecutor<UserPhotoEntity> {}
