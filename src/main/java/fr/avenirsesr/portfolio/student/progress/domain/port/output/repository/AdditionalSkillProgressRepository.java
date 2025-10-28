package fr.avenirsesr.portfolio.student.progress.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdditionalSkillProgressRepository
    extends GenericRepositoryPort<AdditionalSkillProgress> {
  boolean additionalSkillProgressAlreadyExists(AdditionalSkillProgress additionalSkillProgress);

  PagedResult<AdditionalSkillProgress> findAllByStudent(Student student, PageCriteria pageCriteria);

  PagedResult<AdditionalSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword);

  List<AdditionalSkillProgress> findAllByStudent(Student student);

  Optional<AdditionalSkillProgress> findByStudentAndAdditionalSkillId(
      UUID studentId, UUID additionalSkillId);
}
