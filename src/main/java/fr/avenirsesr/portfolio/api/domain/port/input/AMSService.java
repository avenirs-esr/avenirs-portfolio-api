package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.PagedResult;
import fr.avenirsesr.portfolio.api.domain.model.Student;

public interface AMSService {
  PagedResult<AMS> findUserAmsWithPagination(Student student, int page, int size);
}
