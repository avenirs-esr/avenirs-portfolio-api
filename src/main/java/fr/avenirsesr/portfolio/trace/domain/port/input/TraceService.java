package fr.avenirsesr.portfolio.trace.domain.port.input;

import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.FileDownload;
import fr.avenirsesr.portfolio.trace.domain.data.*;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import java.util.List;
import java.util.UUID;

public interface TraceService {
  Trace getTraceById(UUID id);

  List<Trace> findAllTracesById(List<UUID> ids);

  String programNameOfTrace(Trace trace);

  List<Trace> lastTracesOf();

  PagedResult<TraceViewData> getTracesView(
      String keyword, TraceFilter filter, DateFilter dateFilter, PageCriteria pageCriteria);

  void deleteAllByIds(List<UUID> tracesIds);

  TracesSummaryData getTracesSummary();

  TraceDetailData getTraceDetail(UUID id);

  Trace createTrace(
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String personalNote,
      String aiJustification,
      String link);

  Trace createTrace(
      UUID traceId,
      UUID userId,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String personalNote,
      String aiJustification,
      String link,
      boolean valorized);

  TraceDetailData updateTrace(
      UUID traceId,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String personalNote,
      String aiJustification,
      String link,
      boolean valorized);

  TraceDetailData updateTrace(
      UUID traceId,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String personalNote,
      String aiJustification);

  TraceAssociationsData associateTraceWithActivities(UUID traceId, List<UUID> activityIds);

  TraceAssociationsData associateTraceWithDeclaredSkill(UUID traceId, List<UUID> skillIds);

  TraceAssociationsData associateTraceWithDeclaredExperience(
      UUID traceId, List<UUID> experienceIds);

  TraceAssociationsData getTraceAssociations(UUID traceId, boolean onlyNotCompleted);

  void unassociate(UUID traceId, List<UUID> associationIds);

  PagedResult<AssociationSearchResultData> searchDeclaredActivityForAssociation(
      UUID traceId, String keyword, PageCriteria pageCriteria);

  PagedResult<AssociationSearchResultData> searchDeclaredSkillForAssociation(
      UUID traceId, String keyword, PageCriteria pageCriteria);

  PagedResult<AssociationSearchResultData> searchDeclaredExperienceForAssociation(
      UUID traceId, String keyword, PageCriteria pageCriteria);

  List<TraceLockedDeclaredActivitiesData> getLockedDeclaredActivities(List<UUID> traceIds);

  File uploadAttachment(UUID traceId, String fileName, String mimeType, long size, byte[] content);

  FileDownload downloadAttachment(UUID traceId);
}
