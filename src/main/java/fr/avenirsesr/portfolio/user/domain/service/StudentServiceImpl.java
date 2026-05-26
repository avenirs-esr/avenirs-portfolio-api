package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.file.domain.mapper.FileDataMapper;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {
  private StudentRepository studentRepository;
  private UserRepository userRepository;
  private SelfKnowledgeService selfKnowledgeService;
  private final LoggedInUserService loggedInUserService;

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
  public UserProfileOverviewData getStudentProfile() {
    var student = loggedInUserService.getLoggedInStudent();

    return new UserProfileOverviewData(
        student.getUser().getFirstName(),
        student.getUser().getLastName(),
        student.getUser().getEmail(),
        student.getBio(),
        FileDataMapper.mapFileData(
            student.getCoverPicture().orElse(null), FileStorageConstants.DEFAULT_COVER_FILE_URL),
        FileDataMapper.mapFileData(
            student.getProfilePicture().orElse(null),
            FileStorageConstants.DEFAULT_PROFILE_FILE_URL));
  }

  @Override
  public void updateProfile(User user, String bio) {
    var student =
        studentRepository.findById(user.getId()).orElseThrow(UserIsNotStudentException::new);

    if (bio != null) student.setBio(bio);

    studentRepository.save(student);
  }

  @Override
  public Student createStudent(UUID userId, String institutionEmail, String bio) {
    var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var student = Student.create(user, institutionEmail, bio);
    if (user.getEmail() == null) {
      user.setEmail(institutionEmail);
      userRepository.save(user);
    }
    studentRepository.save(student);
    selfKnowledgeService.initSelfKnowledgeCategoriesMandatory(student);
    return student;
  }

  @Override
  public void addSelfKnowledgeCategories(Student student, List<SelfKnowledgeCategory> categories) {
    studentRepository.addSelfKnowledgeCategories(student, categories);
  }

  @Override
  public void removeSelfKnowledgeCategory(
      Student student, SelfKnowledgeCategory selfKnowledgeCategory) {
    studentRepository.removeSelfKnowledgeCategory(student, selfKnowledgeCategory);
  }
}
