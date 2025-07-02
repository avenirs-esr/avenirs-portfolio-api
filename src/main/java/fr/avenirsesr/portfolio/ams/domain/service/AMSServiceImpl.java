package fr.avenirsesr.portfolio.ams.domain.service;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AMSServiceImpl implements AMSService {

  private final AMSRepository amsRepository;

  @Value("${pagination.default-page}")
  private int defaultPage;

  @Value("${pagination.default-page-size}")
  private int defaultPageSize;

  @Value("${pagination.max-page-size}")
  private int maxPageSize;

  public AMSServiceImpl(AMSRepository amsRepository) {
    this.amsRepository = amsRepository;
  }

  @Override
  public PagedResult<AMS> findUserAmsByProgramProgressWithPagination(
      Student student, UUID programProgressId, Integer page, Integer pageSize) {
    log.debug(
        "Finding AMS for user with id [{}] with pagination (page={}, pageSize={})",
        student.getId(),
        page,
        pageSize);
    int pageValue = (page != null && page >= 0) ? page : defaultPage;
    int pageSizeValue =
        (pageSize != null && pageSize > 0 && pageSize <= maxPageSize) ? pageSize : defaultPageSize;

    return amsRepository.findByUserIdViaCohortsAndProgramProgressId(
        student.getId(), programProgressId, pageValue, pageSizeValue);
  }
}
