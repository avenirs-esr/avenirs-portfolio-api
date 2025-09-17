package fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.user.domain.model.Student;

public interface AdditionalSkillProgressRepository
    extends GenericRepositoryPort<AdditionalSkillProgress> {
  boolean additionalSkillProgressAlreadyExists(AdditionalSkillProgress additionalSkillProgress);

  PagedResult<AdditionalSkillProgress> findAllByStudent(Student student, PageCriteria pageCriteria);
}
