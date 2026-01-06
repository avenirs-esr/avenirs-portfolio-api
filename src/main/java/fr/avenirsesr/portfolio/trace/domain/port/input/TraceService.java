package fr.avenirsesr.portfolio.trace.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.trace.domain.data.TraceDetailData;
import fr.avenirsesr.portfolio.trace.domain.data.TracesSummaryData;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TraceService {
  String programNameOfTrace(Trace trace);

  List<Trace> lastTracesOf();

  List<Trace> getTracesLinkedWithDeclaredSkillProgress(
      User user, DeclaredSkillProgress declaredSkillProgress);

  PagedResult<Trace> getTracesView(
      String keyword, TraceFilter filter, DateFilter dateFilter, PageCriteria pageCriteria);

  void deleteById(UUID id);

  TracesSummaryData getTracesSummary();

  TraceDetailData getTraceDetail(UUID id);

  Trace createTrace(
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification);

  Trace createTrace(
      UUID userId,
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification);

  TraceDetailData updateTrace(
      UUID traceId,
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification);

  Optional<LocalDate> getWillBeDeletedAt(Trace trace);

  void associateTrace(
      UUID traceId,
      List<UUID> amsIds,
      List<UUID> skillLevelIds,
      List<UUID> declaredSkillProgressIds);

  void unassociateTrace(
      UUID traceId,
      List<UUID> amsIds,
      List<UUID> skillLevelIds,
      List<UUID> declaredSkillProgressIds);

  void unassociateTraces(DeclaredSkillProgress declaredSkillProgress, List<UUID> traceIds);
}
