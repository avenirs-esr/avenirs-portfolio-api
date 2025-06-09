package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.User;
import java.util.List;

public interface TraceRepository extends GenericRepositoryPort<Trace> {
  List<Trace> findLastsOf(User user, int limit);
}
