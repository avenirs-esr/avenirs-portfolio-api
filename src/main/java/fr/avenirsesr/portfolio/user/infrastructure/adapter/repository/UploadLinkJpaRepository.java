package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.FileUploadLinkEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.FileUploadLinkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UploadLinkJpaRepository
    extends JpaRepository<FileUploadLinkEntity, FileUploadLinkId>,
        JpaSpecificationExecutor<FileUploadLinkEntity> {}
