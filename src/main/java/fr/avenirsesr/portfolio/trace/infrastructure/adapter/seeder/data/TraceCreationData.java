package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import java.util.List;
import java.util.UUID;

public record TraceCreationData(
    UUID traceId,
    UUID userId,
    String title,
    ETraceAuthorType traceAuthorType,
    ELanguage language,
    String aiJustification,
    String personalNote,
    String link,
    List<TraceAttachementCreationData> attachements) {
  public TraceCreationData {
    if (attachements == null) attachements = List.of();
  }
}
