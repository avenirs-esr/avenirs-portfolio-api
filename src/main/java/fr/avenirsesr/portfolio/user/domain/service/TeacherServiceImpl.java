package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotTeacherException;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.port.input.TeacherService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.TeacherRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TeacherServiceImpl implements TeacherService {
  private final TeacherRepository teacherRepository;

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
}
