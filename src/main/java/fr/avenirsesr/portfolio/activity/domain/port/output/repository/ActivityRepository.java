package fr.avenirsesr.portfolio.activity.domain.port.output.repository;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;

public interface ActivityRepository extends GenericRepositoryPort<Activity> {
  PagedResult<Activity> findAll(EActivityThematic thematic, PageCriteria pageCriteria);
}
