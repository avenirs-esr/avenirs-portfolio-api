package fr.avenirsesr.portfolio.ams.domain.service;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.domain.exception.StudentProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AMSServiceImpl implements AMSService {

  private final AMSRepository amsRepository;
  private final StudentProgressRepository studentProgressRepository;

  public AMSServiceImpl(
      AMSRepository amsRepository, StudentProgressRepository studentProgressRepository) {
    this.amsRepository = amsRepository;
    this.studentProgressRepository = studentProgressRepository;
  }

  @Override
  public PagedResult<AMS> findUserAmsByStudentProgress(
      Student student, UUID studentProgressId, PageCriteria pageCriteria) {
    log.debug(
        "Finding AMS for user with id [{}] with pagination (page={}, pageSize={})",
        student.getId(),
        pageCriteria.page(),
        pageCriteria.pageSize());

    StudentProgress studentProgress =
        studentProgressRepository
            .findById(studentProgressId)
            .orElseThrow(StudentProgressNotFoundException::new);

    return amsRepository.findByUserIdViaCohortsAndSkillLevelProgresses(
        student.getId(), studentProgress.getAllSkillLevels(), pageCriteria);
  }
}
