package fr.avenirsesr.portfolio.student.trace.domain.filter;

import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import java.util.*;

public record TraceFilter(
    Boolean isAssociated,
    List<EFileType> fileTypes,
    List<UUID> skillIds,
    Boolean isValorized,
    Boolean isLink) {

  public TraceFilter(
      Boolean isAssociated, List<EFileType> fileTypes, List<UUID> skillIds, Boolean isValorized) {
    this(isAssociated, fileTypes, skillIds, isValorized, null);
  }

  public record TraceTypeFilter(List<EFileType> fileTypes, Boolean isLink) {
    public boolean hasNoFileTypes() {
      return fileTypes == null || fileTypes.isEmpty();
    }

    public boolean isEmpty() {
      return hasNoFileTypes() && isLink == null;
    }
  }

  public Map<ETraceFilterKey, Object> toMap() {
    Map<ETraceFilterKey, Object> map = new EnumMap<>(ETraceFilterKey.class);

    map.put(ETraceFilterKey.IS_ASSOCIATED, isAssociated);
    map.put(ETraceFilterKey.IS_VALORIZED, isValorized);

    var type = new TraceTypeFilter(fileTypes, isLink);

    if (!type.isEmpty()) {
      map.put(ETraceFilterKey.TYPE, type);
    }

    if (skillIds != null && !skillIds.isEmpty()) {
      map.put(ETraceFilterKey.SKILL, skillIds);
    }

    return map;
  }
}
