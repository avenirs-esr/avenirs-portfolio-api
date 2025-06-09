package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.User;
import java.util.List;

public interface TraceService {
  String programNameOfTrace(Trace trace);

  List<Trace> lastTracesOf(User user);
}
