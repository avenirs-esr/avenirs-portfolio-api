package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.*;
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
  public PagedResult<AMS> findUserAmsWithPagination(Student student, int page, int size) {
    log.debug(
        "Finding AMS for user with id [{}] with pagination (page={}, size={})",
        student.getId(),
        page,
        size);
    return amsRepository.findByUserIdViaCohorts(student.getId(), page, size);
  }
}
