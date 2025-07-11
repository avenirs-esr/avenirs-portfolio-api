package fr.avenirsesr.portfolio.trace.domain.port.input;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.TraceView;
import fr.avenirsesr.portfolio.trace.domain.model.UnassociatedTracesSummary;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.util.List;
import java.util.UUID;

public interface TraceService {
  String programNameOfTrace(Trace trace);

  List<Trace> lastTracesOf(User user);

  TraceView getUnassociatedTraces(User user, Integer page, Integer pageSize);

  void deleteById(User user, UUID id);

  UnassociatedTracesSummary getUnassociatedTracesSummary(User user);

  void createTrace(
      User user,
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification);
}
