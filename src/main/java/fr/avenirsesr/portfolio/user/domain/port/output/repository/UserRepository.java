package fr.avenirsesr.portfolio.user.domain.port.output.repository;

import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.util.List;

public interface UserRepository extends GenericRepositoryPort<User> {
  void save(Student student);

  void saveAllStudents(List<Student> students);

  void save(Teacher teacher);

  void saveAllTeachers(List<Teacher> teachers);

  long countAll();
}
