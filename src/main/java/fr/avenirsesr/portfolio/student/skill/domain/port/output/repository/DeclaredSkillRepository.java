package fr.avenirsesr.portfolio.student.skill.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkill;

public interface DeclaredSkillRepository extends GenericRepositoryPort<DeclaredSkill> {
  DeclaredSkill saveOrGet(DeclaredSkill declaredSkill);
}
