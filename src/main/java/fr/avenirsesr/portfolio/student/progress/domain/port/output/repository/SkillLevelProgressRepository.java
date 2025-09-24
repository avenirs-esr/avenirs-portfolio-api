package fr.avenirsesr.portfolio.student.progress.domain.port.output.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface SkillLevelProgressRepository extends GenericRepositoryPort<SkillLevelProgress> {
  List<SkillLevelProgress> linkedWith(AMS ams);

  List<SkillLevelProgress> findAllByStudent(Student student);
}
