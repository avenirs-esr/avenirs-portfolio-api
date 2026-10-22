package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface StaffService {
  Staff getStaffById(UUID id);

  boolean existsById(UUID id);

  /** Profile picture ids of the given users, keyed by user id. Users without one are absent. */
  Map<UUID, UUID> findProfilePictureIdsByIds(Collection<UUID> ids);

  UserProfileOverviewData getStaffProfile();

  void updateProfile(User user, String bio);

  Staff createStaff(UUID userId, String institutionEmail, String bio);

  File uploadProfilePicture(String fileName, String mimeType, long size, byte[] content);

  void deleteProfilePicture();

  File uploadCoverPicture(String fileName, String mimeType, long size, byte[] content);

  void deleteCoverPicture();
}
