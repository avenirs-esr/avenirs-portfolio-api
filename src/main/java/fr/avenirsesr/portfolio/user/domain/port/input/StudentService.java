package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.UUID;

public interface StudentService {
  Student getStudentById(UUID studentId);

  UserProfileOverviewData getStudentProfile();

  void updateProfile(User user, String bio);

  Student createStudent(
      UUID userId, String institutionEmail, UUID institutionId, UUID groupId, String bio);

  void addSelfKnowledgeCategories(Student student, List<ESelfKnowledgeCategory> categories);

  void removeSelfKnowledgeCategory(Student student, ESelfKnowledgeCategory selfKnowledgeCategory);

  File uploadProfilePicture(String fileName, String mimeType, long size, byte[] content);

  void deleteProfilePicture();

  File uploadCoverPicture(String fileName, String mimeType, long size, byte[] content);

  void deleteCoverPicture();
}
