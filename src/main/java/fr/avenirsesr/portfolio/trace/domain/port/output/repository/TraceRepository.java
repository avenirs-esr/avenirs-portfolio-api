package fr.avenirsesr.portfolio.trace.domain.port.output.repository;

import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TraceRepository extends GenericRepositoryPort<Trace> {
  List<Trace> findLastsOf(User user, int limit);

  List<Trace> findAllPage(User user, int page, int pageSize);

  Page<TraceEntity> findAllUnassociatedPage(User user, int page, int pageSize);

  List<Trace> findAllUnassociated(User user);
}
