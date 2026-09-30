package fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import java.util.UUID;

public interface ActivityDraftRepository extends GenericRepositoryPort<ActivityDraft> {
  boolean existsById(UUID id);
}
