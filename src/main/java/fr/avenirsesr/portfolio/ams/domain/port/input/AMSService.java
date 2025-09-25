package fr.avenirsesr.portfolio.ams.domain.port.input;

import fr.avenirsesr.portfolio.ams.domain.dto.AmsView;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;

public interface AMSService {
  PagedResult<AmsView> findUserAmsByStudentProgress(
      Student student, UUID studentProgressId, PageCriteria pageCriteria);

  PagedResult<AMS> search(Student student, String keyword, PageCriteria pageCriteria);
}
