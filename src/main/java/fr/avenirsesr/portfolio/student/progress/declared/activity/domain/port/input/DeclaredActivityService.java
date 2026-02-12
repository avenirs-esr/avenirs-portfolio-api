package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import java.util.UUID;

public interface DeclaredActivityService {
  DeclaredActivity subscribe(UUID activityId);
}
