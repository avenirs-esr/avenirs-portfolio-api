package fr.avenirsesr.portfolio.file.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileJpaRepository
    extends JpaRepository<FileEntity, UUID>, JpaSpecificationExecutor<FileEntity> {}
