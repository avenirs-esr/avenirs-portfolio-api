package fr.avenirsesr.portfolio.student.progress.domain.port.output.repository;

import fr.avenirsesr.portfolio.student.progress.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import java.util.List;

public interface SkillRepository extends GenericRepositoryPort<Skill> {
  List<SkillEntity> findAll();
}
