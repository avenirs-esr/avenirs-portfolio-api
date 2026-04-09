package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.UUID;

public interface StudentService {
  Student getStudentById(UUID studentId);

  String getBio(User user);

  void updateProfile(User user, String bio);

  Student createStudent(UUID userId, String institutionEmail, String bio);

  void addSelfKnowledgeCategories(Student student, List<SelfKnowledgeCategory> categories);

  void removeSelfKnowledgeCategory(Student student, SelfKnowledgeCategory selfKnowledgeCategory);
}
