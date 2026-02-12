package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeclaredActivityJpaRepository
    extends JpaRepository<DeclaredActivityEntity, UUID>,
        JpaSpecificationExecutor<DeclaredActivityEntity> {}
