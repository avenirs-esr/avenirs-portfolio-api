package fr.avenirsesr.portfolio.ams.domain.service;

import fr.avenirsesr.portfolio.ams.domain.dto.AmsView;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AMSServiceImpl implements AMSService {
  private final AMSRepository amsRepository;
  private final TraceRepository traceRepository;
  private final SkillLevelProgressRepository skillLevelProgressRepository;
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
                        skillLevelProgressRepository.linkedWith(ams).size(),
                        traceRepository.linkedWith(ams).size()))
            .toList(),
        amses.pageInfo());
  }

  @Override
  public PagedResult<AMS> search(String keyword, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    return amsRepository.findByStudent(student, pageCriteria, keyword);
  }
}
