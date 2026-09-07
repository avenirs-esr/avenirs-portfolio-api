package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityViewRepository;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityViewEntity;
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
    if (activityViewJpaRepository.existsByActivityIdAndStudentId(activityId, studentId)) {
      return;
    }
    try {
      activityViewJpaRepository.saveAndFlush(ActivityViewEntity.of(activityId, studentId));
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
}
