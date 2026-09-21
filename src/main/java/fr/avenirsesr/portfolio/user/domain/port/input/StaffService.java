package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.util.List;
import java.util.UUID;

public interface StaffService {
  Staff getStaffById(UUID id);

  boolean existsById(UUID id);

  UserProfileOverviewData getStaffProfile();

  void updateProfile(User user, String bio);

  void updateAffiliations(UUID staffId, List<UUID> institutionIds, List<UUID> groupIds);

  Staff createStaff(
      UUID userId,
      String institutionEmail,
      List<UUID> institutionIds,
      List<UUID> groupIds,
      String bio);

  File uploadProfilePicture(String fileName, String mimeType, long size, byte[] content);

  void deleteProfilePicture();

  File uploadCoverPicture(String fileName, String mimeType, long size, byte[] content);

  void deleteCoverPicture();
}
