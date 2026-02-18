package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface DeclaredActivityRepository extends GenericRepositoryPort<DeclaredActivity> {
  List<DeclaredActivity> findAllByStudent(Student student, FetchGraph fetchGraph);
}
