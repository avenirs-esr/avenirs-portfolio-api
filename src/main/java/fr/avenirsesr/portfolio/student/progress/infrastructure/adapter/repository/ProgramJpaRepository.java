package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.ProgramEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProgramJpaRepository
    extends JpaRepository<ProgramEntity, UUID>, JpaSpecificationExecutor<ProgramEntity> {}
