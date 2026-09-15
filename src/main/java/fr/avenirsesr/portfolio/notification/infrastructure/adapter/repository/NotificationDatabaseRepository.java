package fr.avenirsesr.portfolio.notification.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.mapper.NotificationMapper;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.model.NotificationEntity;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.specification.NotificationSpecification;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDatabaseRepository
    extends GenericJpaRepositoryAdapter<Notification, NotificationEntity>
    implements NotificationRepository {
  private final NotificationJpaRepository notificationJpaRepository;

  public NotificationDatabaseRepository(NotificationJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, NotificationEntity.class, NotificationMapper.INSTANCE);
    this.notificationJpaRepository = jpaRepository;
  }

  @Override
  public PagedResult<Notification> findByUserAndCategory(
      UUID userId, EUserCategory userCategory, PageCriteria pageCriteria) {
    var specification =
        NotificationSpecification.hasUser(userId)
            .and(NotificationSpecification.hasUserCategoryNullOrEquals(userCategory));
    return findAll(
        specification,
        PageRequest.of(
            pageCriteria.page(), pageCriteria.pageSize(), Sort.by("createdAt").descending()));
  }

  @Override
  public long countUnreadByUserAndCategory(UUID userId, EUserCategory userCategory) {
    var specification =
        NotificationSpecification.hasUser(userId)
            .and(NotificationSpecification.hasUserCategoryNullOrEquals(userCategory))
            .and(NotificationSpecification.isNotSeen());
    return jpaSpecificationExecutor.count(specification);
  }

  @Override
  public void deleteByTypeAndElementId(ENotificationType type, UUID elementId) {
    notificationJpaRepository.deleteByTypeAndElementId(type, elementId);
  }
}
