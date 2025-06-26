package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.PagedResult;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import java.util.UUID;

public interface AMSService {
  PagedResult<AMS> findUserAmsByProgramProgressWithPagination(
      Student student, UUID programProgressId, Integer page, Integer size);
}
