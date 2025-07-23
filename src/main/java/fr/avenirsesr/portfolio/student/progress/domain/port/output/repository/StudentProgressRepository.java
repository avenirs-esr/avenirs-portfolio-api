package fr.avenirsesr.portfolio.student.progress.domain.port.output.repository;

import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface StudentProgressRepository extends GenericRepositoryPort<StudentProgress> {
  List<StudentProgress> findAllByStudent(Student student);

  List<StudentProgress> findAllAPCByStudent(Student student);
}
