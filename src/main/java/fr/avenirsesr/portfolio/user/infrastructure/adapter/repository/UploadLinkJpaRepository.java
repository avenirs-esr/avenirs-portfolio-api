package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UploadLinkEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UploadLinkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UploadLinkJpaRepository
    extends JpaRepository<UploadLinkEntity, UploadLinkId>,
        JpaSpecificationExecutor<UploadLinkEntity> {}
