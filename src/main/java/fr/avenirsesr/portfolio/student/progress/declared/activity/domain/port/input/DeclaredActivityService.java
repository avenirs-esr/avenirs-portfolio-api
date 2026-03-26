package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.TraceAssociationSearchInfoData;
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

  DeclaredActivityAssociationsData getDeclaredActivityAssociations(UUID declaredActivityId);

  void deleteAssociations(UUID declaredActivityId, List<UUID> idsToDelete);

  void updateDeclaredActivityDates(UUID declaredActivityId, LocalDate startDate, LocalDate endDate);

  PagedResult<DeclaredActivity> searchDeclaredActivity(String keyword, PageCriteria pageCriteria);

  DeclaredActivityAssociationsData associateActivityWithTraces(
      UUID declaredActivityId, List<UUID> traceIds);

  PagedResult<TraceAssociationSearchInfoData> searchTracesForAssociation(
      UUID declaredActivityId, String keyword, PageCriteria pageCriteria, Boolean isAssociated);
}
