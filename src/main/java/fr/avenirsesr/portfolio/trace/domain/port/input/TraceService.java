package fr.avenirsesr.portfolio.trace.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.trace.domain.model.AssociationsTrace;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.TraceDetail;
import fr.avenirsesr.portfolio.trace.domain.model.TracesSummary;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TraceService {
  String programNameOfTrace(Trace trace);

  List<Trace> lastTracesOf(User user);

  PagedResult<Trace> getTracesView(
      User user, PageCriteria pageCriteria, ETraceStatus status, String keyword);

  void deleteById(User user, UUID id);

  TracesSummary getTracesSummary(User user);

  TraceDetail getTraceDetail(User user, UUID id);

  AssociationsTrace getAssociationsTrace(User user, UUID id);

  Trace createTrace(
      User user,
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification);

  Optional<LocalDate> getWillBeDeletedAt(Trace trace);

  void associateTrace(
      User user,
      UUID traceId,
      List<UUID> amsIds,
      List<UUID> skillLevelIds,
      List<UUID> additionalSkillProgressIds);
}
