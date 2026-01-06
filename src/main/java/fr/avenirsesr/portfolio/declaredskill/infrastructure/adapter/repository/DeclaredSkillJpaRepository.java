package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.model.DeclaredSkillEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeclaredSkillJpaRepository
    extends JpaRepository<DeclaredSkillEntity, UUID>,
        JpaSpecificationExecutor<DeclaredSkillEntity> {
  Optional<DeclaredSkillEntity> findByExternalSkillId(UUID externalSkillId);
}
