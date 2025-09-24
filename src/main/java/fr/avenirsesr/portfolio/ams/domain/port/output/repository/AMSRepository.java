package fr.avenirsesr.portfolio.ams.domain.port.output.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface AMSRepository extends GenericRepositoryPort<AMS> {
  PagedResult<AMS> findByStudent(Student student, PageCriteria pageCriteria);

  List<AMS> findAllByStudent(Student student);
}
