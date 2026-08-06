package fr.avenirsesr.portfolio.student.trace.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.Map;

public interface TraceRepository extends GenericRepositoryPort<Trace> {
  List<Trace> findLastsOf(Student student, int limit);

  PagedResult<Trace> findAll(
      Student student,
      String keyword,
      TraceFilter filter,
      DateFilter dateFilter,
      PageCriteria pageCriteria);

  List<Trace> findAll(Student student, boolean isAssociated);

  Map<Trace, Boolean> isAssociated(List<Trace> traces);
}
