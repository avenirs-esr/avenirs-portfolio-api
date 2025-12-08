package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.StudentProgressEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentProgressJpaRepository
    extends JpaRepository<StudentProgressEntity, UUID>,
        JpaSpecificationExecutor<StudentProgressEntity> {}
