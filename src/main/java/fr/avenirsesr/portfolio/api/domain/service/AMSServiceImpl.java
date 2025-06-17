package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.PagedResult;
import fr.avenirsesr.portfolio.api.domain.model.PaginationInfo;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.AMSRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AMSServiceImpl implements AMSService {
  private final AMSRepository amsRepository;

  @Override
  public PagedResult<AMS> findUserAmsWithPagination(User user, int page, int size) {
    log.debug(
        "Finding AMS for user with id [{}] with pagination (page={}, size={})",
        user.getId(),
        page,
        size);
    return amsRepository.findByUserIdViaCohorts(user.getId(), page, size);
  }

  @Override
  public PaginationInfo createPaginationInfo(
      int page, int size, long totalElements, int totalPages) {
    return new PaginationInfo(size, totalElements, totalPages, page);
  }
}
