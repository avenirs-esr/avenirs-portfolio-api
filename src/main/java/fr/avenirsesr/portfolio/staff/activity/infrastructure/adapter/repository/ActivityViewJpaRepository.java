package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityViewEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityViewJpaRepository extends JpaRepository<ActivityViewEntity, UUID> {
  boolean existsByActivityIdAndStudentId(UUID activityId, UUID studentId);

  int countByActivityId(UUID activityId);
}
