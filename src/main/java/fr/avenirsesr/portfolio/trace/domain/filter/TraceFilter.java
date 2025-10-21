package fr.avenirsesr.portfolio.trace.domain.filter;

import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TraceFilter(
    Boolean isAssociated,
    List<EFileType> fileTypes,
    List<UUID> skillIds,
    List<ETraceStatus> statuses) {
  public Map<ETraceFilterKey, Object> toMap() {
    var map = new HashMap<ETraceFilterKey, Object>();
    map.put(ETraceFilterKey.IS_ASSOCIATED, isAssociated);
    if (statuses != null && !statuses.isEmpty()) map.put(ETraceFilterKey.STATUS, statuses);
    if (fileTypes != null && !fileTypes.isEmpty()) map.put(ETraceFilterKey.FILE_TYPE, fileTypes);
    if (skillIds != null && !skillIds.isEmpty()) {
      map.put(ETraceFilterKey.SKILL, skillIds);
    }
    return map;
  }
}
