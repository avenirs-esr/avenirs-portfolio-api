package fr.avenirsesr.portfolio.trace.domain.port.output.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericDeletableRepositoryPort;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.util.List;

public interface TraceRepository extends GenericDeletableRepositoryPort<Trace> {
  List<Trace> findLastsOf(User user, int limit);

  PagedResult<Trace> findAll(User user, PageCriteria pageCriteria, ETraceStatus status);

  List<Trace> findAll(User user, ETraceStatus status);

  List<Trace> linkedWith(AMS ams);
}
