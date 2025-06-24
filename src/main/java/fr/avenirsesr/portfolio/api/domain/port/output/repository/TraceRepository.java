package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TraceEntity;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TraceRepository extends GenericRepositoryPort<Trace> {
  List<Trace> findLastsOf(User user, int limit);

  List<Trace> findAllPage(User user, int page, int pageSize);

  Page<TraceEntity> findAllUnassociatedPage(User user, int page, int pageSize);

  List<Trace> findAllUnassociated(User user);
}
