package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociations;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeclaredActivityService {
  PagedResult<DeclaredActivity> getDeclaredActivities(PageCriteria pageCriteria);

  List<DeclaredActivity> getAllDeclaredActivitiesOf(Student student);

  Optional<DeclaredActivity> getByActivity(Activity activity);

  DeclaredActivity subscribe(UUID activityId, LocalDate startDate, LocalDate endDate);

  DeclaredActivity subscribe(
      UUID declaredActivityId, UUID activityId, LocalDate startDate, LocalDate endDate);

  void unsubscribeMultiple(List<UUID> activityIds);

  DeclaredActivity finish(UUID declaredActivityId);

  void updateReflection(UUID declaredActivityId, String reflection);

  DeclaredActivity getDeclaredActivityDetails(UUID declaredActivityId);

  DeclaredActivityAssociations getDeclaredActivityAssociations(UUID declaredActivityId);

  void deleteAssociations(UUID declaredActivityId, List<UUID> idsToDelete);

  void updateDeclaredActivityDates(UUID declaredActivityId, LocalDate startDate, LocalDate endDate);

  void associateActivityWithTraces(UUID declaredActivityId, List<UUID> traceIds);
}
