package fr.avenirsesr.portfolio.file.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.file.domain.model.ActivityBanner;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.ActivityBannerRepository;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.ActivityBannerMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.ActivityBannerEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.specification.ActivityBannerSpecification;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ActivityBannerDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<ActivityBanner, ActivityBannerEntity>
    implements ActivityBannerRepository {

  protected ActivityBannerDatabaseRepository(ActivityBannerJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, ActivityBannerEntity.class, ActivityBannerMapper.INSTANCE);
  }

  @Override
  public List<ActivityBanner> findAllByActivity(Activity activity) {
    return jpaSpecificationExecutor
        .findAll(ActivityBannerSpecification.ofActivity(activity))
        .stream()
        .map(ActivityBannerMapper.INSTANCE::toDomain)
        .toList();
  }
}
