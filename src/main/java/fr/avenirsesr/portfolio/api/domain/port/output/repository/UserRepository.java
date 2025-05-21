package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.Teacher;
import fr.avenirsesr.portfolio.api.domain.model.User;
import java.util.List;

public interface UserRepository extends GenericRepositoryPort<User> {
  void save(Student student);

  void saveAllStudents(List<Student> students);

  void save(Teacher teacher);

  void saveAllTeachers(List<Teacher> teachers);
}
