package fr.avenirsesr.portfolio.trace.domain.port.output.repository;

import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.util.List;

public interface TraceRepository extends GenericRepositoryPort<Trace> {
  List<Trace> findLastsOf(User user, int limit);

  PagedResult<Trace> findAllUnassociated(User user, PageCriteria pageCriteria);

  List<Trace> findAllUnassociated(User user);
}
