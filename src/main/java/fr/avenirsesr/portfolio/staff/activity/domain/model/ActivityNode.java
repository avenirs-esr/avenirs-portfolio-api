package fr.avenirsesr.portfolio.staff.activity.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityNode {
  UUID getId();

  Optional<UUID> getMainActivityId();

  Optional<UUID> getPreviousActivityId();

  default boolean isSubActivity() {
    return getMainActivityId().isPresent();
  }

  static <T extends ActivityNode> List<T> orderedFrom(
      UUID mainActivityId, Collection<T> subActivities) {
    var byPreviousId = new HashMap<UUID, T>();
    subActivities.forEach(
        subActivity ->
            subActivity
                .getPreviousActivityId()
                .ifPresent(previousId -> byPreviousId.put(previousId, subActivity)));

    var ordered = new LinkedHashSet<T>();
    var next = byPreviousId.get(mainActivityId);
    while (next != null && ordered.add(next)) {
      next = byPreviousId.get(next.getId());
    }

    var result = new ArrayList<>(ordered);
    subActivities.stream()
        .filter(subActivity -> !ordered.contains(subActivity))
        .sorted(Comparator.comparing(subActivity -> subActivity.getId().toString()))
        .forEach(result::add);
    return result;
  }
}
