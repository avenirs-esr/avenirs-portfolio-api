package fr.avenirsesr.portfolio.ams.domain.service;

import fr.avenirsesr.portfolio.ams.domain.dto.AmsView;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AMSServiceImpl implements AMSService {
  private final StudentRepository studentRepository;
  private final AMSRepository amsRepository;
  private final StudentProgressRepository studentProgressRepository;
  private final TraceRepository traceRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;

  public AMSServiceImpl(
      StudentRepository studentRepository,
      AMSRepository amsRepository,
      StudentProgressRepository studentProgressRepository,
      TraceRepository traceRepository,
      SkillLevelProgressRepository skillLevelProgressRepository) {
    this.studentRepository = studentRepository;
    this.amsRepository = amsRepository;
    this.studentProgressRepository = studentProgressRepository;
    this.traceRepository = traceRepository;
    this.skillLevelProgressRepository = skillLevelProgressRepository;
  }

  @Override
  public PagedResult<AmsView> findUserAmsByStudentProgress(
      UUID studentProgressId, PageCriteria pageCriteria) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    log.debug(
        "Fetching AMS for user with id [{}] with pagination (page={}, pageSize={})",
        student.getId(),
        pageCriteria.page(),
        pageCriteria.pageSize());

    var amses = amsRepository.findByStudent(student, pageCriteria);

    return new PagedResult<>(
        amses.content().stream()
            .map(
                ams ->
                    new AmsView(
                        ams,
                        skillLevelProgressRepository.linkedWith(ams).size(),
                        traceRepository.linkedWith(ams).size()))
            .toList(),
        amses.pageInfo());
  }

  @Override
  public PagedResult<AMS> search(String keyword, PageCriteria pageCriteria) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    return amsRepository.findByStudent(student, pageCriteria, keyword);
  }
}
