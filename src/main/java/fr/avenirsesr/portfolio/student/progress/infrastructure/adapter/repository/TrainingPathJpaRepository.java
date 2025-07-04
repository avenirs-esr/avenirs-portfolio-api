package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.TrainingPathEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrainingPathJpaRepository
    extends JpaRepository<TrainingPathEntity, UUID>, JpaSpecificationExecutor<TrainingPathEntity> {}
