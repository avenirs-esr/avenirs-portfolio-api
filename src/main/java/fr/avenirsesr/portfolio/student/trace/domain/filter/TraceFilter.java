package fr.avenirsesr.portfolio.student.trace.domain.filter;

import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import java.util.*;

public record TraceFilter(
    Boolean isAssociated, List<EFileType> fileTypes, List<UUID> skillIds, Boolean isValorized) {
  public Map<ETraceFilterKey, Object> toMap() {
    Map<ETraceFilterKey, Object> map = new EnumMap<>(ETraceFilterKey.class);

    map.put(ETraceFilterKey.IS_ASSOCIATED, isAssociated);
    map.put(ETraceFilterKey.IS_VALORIZED, isValorized);

    if (fileTypes != null && !fileTypes.isEmpty()) {
      map.put(ETraceFilterKey.FILE_TYPE, fileTypes);
    }

    if (skillIds != null && !skillIds.isEmpty()) {
      map.put(ETraceFilterKey.SKILL, skillIds);
    }

    return map;
  }
}
