package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.PagedResult;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.AMSRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AMSServiceImpl implements AMSService {
  private final AMSRepository amsRepository;

  @Value("${pagination.default-page:0}")
  private int defaultPage;

  @Value("${pagination.default-page-pageSize:8}")
  private int defaultPageSize;

  @Value("${pagination.max-page-pageSize:12}")
  private int maxPageSize;

  public AMSServiceImpl(AMSRepository amsRepository) {
    this.amsRepository = amsRepository;
  }

  @Override
  public PagedResult<AMS> findUserAmsWithPagination(Student student, int page, int pageSize) {
    log.debug(
        "Finding AMS for user with id [{}] with pagination (page={}, pageSize={})",
        student.getId(),
        page,
        pageSize);

    page = page < 0 ? defaultPage : page;
    pageSize = (pageSize > 0 && pageSize <= maxPageSize) ? pageSize : defaultPageSize;

    return amsRepository.findByUserIdViaCohorts(student.getId(), page, pageSize);
  }
}
