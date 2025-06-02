package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import java.util.List;

public interface SkillRepository extends GenericRepositoryPort<Skill> {
  List<SkillEntity> findAll();
}
