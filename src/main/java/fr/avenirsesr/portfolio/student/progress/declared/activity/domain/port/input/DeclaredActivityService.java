package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.UUID;

public interface DeclaredActivityService {
  PagedResult<DeclaredActivity> getDeclaredActivities(PageCriteria pageCriteria);

  List<DeclaredActivity> getAllDeclaredActivitiesOf(Student student);

  DeclaredActivity subscribe(UUID activityId);

  void unsubscribeMultiple(List<UUID> activityIds);

  DeclaredActivity finish(UUID declaredActivityId);
}
