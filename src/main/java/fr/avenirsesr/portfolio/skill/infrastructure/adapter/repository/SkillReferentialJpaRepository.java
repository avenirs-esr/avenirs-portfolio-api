package fr.avenirsesr.portfolio.skill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.model.SkillReferentialEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SkillReferentialJpaRepository
    extends JpaRepository<SkillReferentialEntity, UUID>,
        JpaSpecificationExecutor<SkillReferentialEntity> {
  Optional<SkillReferentialEntity> findByTypeAndInstitutionIdIsNull(ESkillReferentialType type);

  List<SkillReferentialEntity> findAllByInstitutionIdIsNull();

  List<SkillReferentialEntity> findAllByInstitutionId(UUID institutionId);
}
