package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface DeclaredSkillProgressRepository
    extends GenericRepositoryPort<DeclaredSkillProgress> {
  boolean declaredSkillProgressAlreadyExists(DeclaredSkillProgress declaredSkillProgress);

  PagedResult<DeclaredSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, Boolean isValorized, SortCriteria sortCriteria);

  PagedResult<DeclaredSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword, SortCriteria sortCriteria);

  List<DeclaredSkillProgress> findAllByStudent(Student student);
}
