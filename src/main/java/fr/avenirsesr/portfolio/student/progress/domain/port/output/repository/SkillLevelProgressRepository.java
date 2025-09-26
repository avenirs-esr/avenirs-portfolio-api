package fr.avenirsesr.portfolio.student.progress.domain.port.output.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.UUID;

public interface SkillLevelProgressRepository extends GenericRepositoryPort<SkillLevelProgress> {
  List<SkillLevelProgress> linkedWith(AMS ams);

  List<SkillLevelProgress> findAllByStudent(Student student);

  List<SkillLevelProgress> findAllByStudentAndSkillId(Student student, UUID skillId);

  PagedResult<SkillLevelProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword);
}
