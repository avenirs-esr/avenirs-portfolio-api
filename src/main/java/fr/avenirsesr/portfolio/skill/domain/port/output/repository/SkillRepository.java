package fr.avenirsesr.portfolio.skill.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.model.SkillReferential;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillRepository extends GenericRepositoryPort<Skill> {
  Optional<Skill> findWithAncestorsById(UUID id);

  List<Skill> findAllWithAncestorsById(List<UUID> ids);

  List<Skill> findAllByReferential(SkillReferential referential);

  long countByReferential(SkillReferential referential);

  void archiveAll(List<UUID> skillIds);
}
