package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.TraceView;
import fr.avenirsesr.portfolio.api.domain.model.UnassociatedTracesSummary;
import fr.avenirsesr.portfolio.api.domain.model.User;
import java.util.List;
import java.util.UUID;

public interface TraceService {
  String programNameOfTrace(Trace trace);

  List<Trace> lastTracesOf(User user);

  TraceView getUnassociatedTraces(User user, Integer page, Integer pageSize);

  void deleteById(User user, UUID id);

  UnassociatedTracesSummary getUnassociatedTracesSummary(User user);
}
