package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.UUID;

public interface DeclaredActivityService {
  DeclaredActivity subscribe(UUID activityId);

  List<DeclaredActivity> getAllDeclaredActivitiesOf(Student student);
}
