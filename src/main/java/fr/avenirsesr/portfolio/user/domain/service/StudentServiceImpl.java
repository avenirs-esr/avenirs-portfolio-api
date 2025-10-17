package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {
  private StudentRepository studentRepository;

  @Override
  public Student getStudentById(UUID studentId) {
    return studentRepository
        .findById(studentId)
        .orElseThrow(
            () -> {
              log.error("Student {} not found", studentId);
              return new UserIsNotStudentException();
            });
  }

  @Override
  public String getBio(User user) {
    var student =
        studentRepository.findById(user.getId()).orElseThrow(UserIsNotStudentException::new);
    return student.getBio();
  }

  @Override
  public void updateProfile(User user, String bio) {
    var student =
        studentRepository.findById(user.getId()).orElseThrow(UserIsNotStudentException::new);

    if (student != null) student.setBio(bio);

    studentRepository.save(student);
  }
}
