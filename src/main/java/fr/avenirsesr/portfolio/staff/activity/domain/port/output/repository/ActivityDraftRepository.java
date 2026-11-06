package fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ActivityDraftRepository extends GenericRepositoryPort<ActivityDraft> {
  boolean existsById(UUID id);

  boolean existsSubActivityById(UUID id);

  List<ActivityDraft> findAllSubActivitiesOf(UUID mainActivityId);

  Map<UUID, UUID> findMainActivityIdsOfSubActivities(Collection<UUID> ids);
}
