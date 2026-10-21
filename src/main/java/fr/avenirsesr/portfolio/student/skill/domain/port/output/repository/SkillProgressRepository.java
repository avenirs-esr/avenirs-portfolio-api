package fr.avenirsesr.portfolio.student.skill.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface SkillProgressRepository extends GenericRepositoryPort<SkillProgress> {
  boolean skillProgressAlreadyExists(SkillProgress skillProgress);

  PagedResult<SkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, Boolean isValorized, SortCriteria sortCriteria);

  PagedResult<SkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword, SortCriteria sortCriteria);

  List<SkillProgress> findAllByStudent(Student student);
}
