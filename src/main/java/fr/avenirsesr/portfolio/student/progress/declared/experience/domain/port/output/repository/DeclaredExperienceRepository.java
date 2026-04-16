package fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.user.domain.model.Student;

public interface DeclaredExperienceRepository extends GenericRepositoryPort<DeclaredExperience> {
  PagedResult<DeclaredExperience> findAllByStudent(Student student, PageCriteria pageCriteria);

  PagedResult<DeclaredExperience> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword);
}
