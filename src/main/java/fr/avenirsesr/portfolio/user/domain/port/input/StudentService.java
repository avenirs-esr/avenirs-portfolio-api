package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.UUID;

public interface StudentService {
  Student getStudentById(UUID studentId);

  boolean existsById(UUID studentId);

  UserProfileOverviewData getStudentProfile();

  void updateProfile(User user, String bio);

  Student createStudent(
      UUID userId,
      String institutionEmail,
      List<UUID> institutionIds,
      List<UUID> groupIds,
      String bio);

  void updateAffiliations(UUID studentId, List<UUID> institutionIds, List<UUID> groupIds);

  void addSelfKnowledgeCategories(Student student, List<ESelfKnowledgeCategory> categories);

  void removeSelfKnowledgeCategory(Student student, ESelfKnowledgeCategory selfKnowledgeCategory);

  File uploadProfilePicture(String fileName, String mimeType, long size, byte[] content);

  void deleteProfilePicture();

  File uploadCoverPicture(String fileName, String mimeType, long size, byte[] content);

  void deleteCoverPicture();
}
