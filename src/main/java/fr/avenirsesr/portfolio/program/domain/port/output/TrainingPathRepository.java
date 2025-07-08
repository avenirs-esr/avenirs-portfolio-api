package fr.avenirsesr.portfolio.program.domain.port.output;

import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface TrainingPathRepository extends GenericRepositoryPort<TrainingPath> {
  List<TrainingPath> findAllTrainingPathsByStudents(Student student);
}
