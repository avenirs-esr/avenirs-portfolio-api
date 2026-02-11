package fr.avenirsesr.portfolio.file.domain.port.output.repository;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.file.domain.model.ActivityBanner;
import java.util.List;

public interface ActivityBannerRepository extends GenericRepositoryPort<ActivityBanner> {
  List<ActivityBanner> findAllByActivity(Activity activity);
}
