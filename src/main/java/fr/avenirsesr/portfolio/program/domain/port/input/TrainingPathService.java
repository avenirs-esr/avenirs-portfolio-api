package fr.avenirsesr.portfolio.program.domain.port.input;

import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface TrainingPathService {
  List<TrainingPath> getTrainingPathsByStudent(Student student);
}
