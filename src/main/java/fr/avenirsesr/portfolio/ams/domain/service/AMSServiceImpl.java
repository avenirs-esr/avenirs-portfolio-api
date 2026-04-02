package fr.avenirsesr.portfolio.ams.domain.service;

import fr.avenirsesr.portfolio.ams.domain.dto.AmsView;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.SkillLevelProgressService;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AMSServiceImpl implements AMSService {
  private final AMSRepository amsRepository;
  private final TraceService traceService;
  private final SkillLevelProgressService skillLevelProgressService;
  private final LoggedInUserService loggedInUserService;

  @Override
  public PagedResult<AmsView> findUserAmsByStudentProgress(
      UUID studentProgressId, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
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
                        skillLevelProgressService.getSkillLevelProgressesLinkedWithAMS(ams).size(),
                        traceService.getTracesLinkedWithAMS(ams).size()))
            .toList(),
        amses.pageInfo());
  }

  @Override
  public PagedResult<AMS> search(String keyword, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return amsRepository.findByStudent(student, pageCriteria, keyword);
  }
}
