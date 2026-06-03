package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.FeedbackEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeedbackJpaRepository
    extends JpaRepository<FeedbackEntity, UUID>, JpaSpecificationExecutor<FeedbackEntity> {}
