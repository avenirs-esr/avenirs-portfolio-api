package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotTeacherException;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.port.input.TeacherService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.TeacherRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TeacherServiceImpl implements TeacherService {
  private final TeacherRepository teacherRepository;
  private final UserRepository userRepository;

  @Override
  public String getBio(User user) {
    var teacher =
        teacherRepository.findById(user.getId()).orElseThrow(UserIsNotTeacherException::new);
    return teacher.getBio();
  }

  @Override
  public void updateProfile(User user, String bio) {
    var teacher =
        teacherRepository.findById(user.getId()).orElseThrow(UserIsNotTeacherException::new);

    if (bio != null) teacher.setBio(bio);

    teacherRepository.save(teacher);
  }

  @Override
  public Teacher createTeacher(UUID userId, String bio) {
    var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var teacher = Teacher.create(user, bio);
    teacherRepository.save(teacher);
    return teacher;
  }
}
