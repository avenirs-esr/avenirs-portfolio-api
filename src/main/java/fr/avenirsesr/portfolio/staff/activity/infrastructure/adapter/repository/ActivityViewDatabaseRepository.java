package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityViewRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityViewEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ActivityViewDatabaseRepository implements ActivityViewRepository {
  private final ActivityViewJpaRepository activityViewJpaRepository;

  @Override
  public void recordView(UUID activityId, UUID studentId) {
    var viewedAt = Instant.now();
    if (activityViewJpaRepository.updateLastViewedAt(activityId, studentId, viewedAt) > 0) {
      return;
    }
    try {
      activityViewJpaRepository.saveAndFlush(
          ActivityViewEntity.of(activityId, studentId, viewedAt));
    } catch (DataIntegrityViolationException e) {
      log.debug(
          "View of activity [{}] by student [{}] already recorded concurrently",
          activityId,
          studentId);
    }
  }

  @Override
  public int countUniqueViews(UUID activityId) {
    return activityViewJpaRepository.countByActivityId(activityId);
  }

  @Override
  public int countViewersSince(UUID activityId, Collection<UUID> studentIds, Instant since) {
    if (studentIds.isEmpty()) {
      return 0;
    }
    return activityViewJpaRepository.countByActivityIdAndStudentIdInAndLastViewedAtGreaterThanEqual(
        activityId, studentIds, since);
  }

  @Override
  public void deleteAllByActivityId(UUID activityId) {
    activityViewJpaRepository.deleteByActivityId(activityId);
  }
}
