package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TeacherJpaRepository
    extends JpaRepository<TeacherEntity, UUID>, JpaSpecificationExecutor<TeacherEntity> {}
