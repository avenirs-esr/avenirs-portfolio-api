package fr.avenirsesr.portfolio.ams.domain.port.input;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;

public interface AMSService {
  PagedResult<AMS> findUserAmsByStudentProgress(
      Student student, UUID studentProgressId, PageCriteria pageCriteria);
}
