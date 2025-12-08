package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.model.DeclaredProgramEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeclaredProgramJpaRepository
    extends JpaRepository<DeclaredProgramEntity, UUID>,
        JpaSpecificationExecutor<DeclaredProgramEntity> {}
