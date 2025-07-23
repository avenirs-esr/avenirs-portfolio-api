package fr.avenirsesr.portfolio.ams.domain.service;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AMSServiceImpl implements AMSService {

  private final AMSRepository amsRepository;

  public AMSServiceImpl(AMSRepository amsRepository) {
    this.amsRepository = amsRepository;
  }

  @Override
  public PagedResult<AMS> findUserAmsByProgramProgress(
      Student student, UUID programProgressId, PageCriteria pageCriteria) {
    log.debug(
        "Finding AMS for user with id [{}] with pagination (page={}, pageSize={})",
        student.getId(),
        pageCriteria.page(),
        pageCriteria.pageSize());

    return amsRepository.findByUserIdViaCohortsAndProgramProgressId(
        student.getId(), programProgressId, pageCriteria);
  }
}
