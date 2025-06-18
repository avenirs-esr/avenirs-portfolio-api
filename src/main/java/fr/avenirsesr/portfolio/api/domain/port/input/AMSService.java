package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.*;

public interface AMSService {
  PagedResult<AMS> findUserAmsWithPagination(Student student, int page, int size);
}
