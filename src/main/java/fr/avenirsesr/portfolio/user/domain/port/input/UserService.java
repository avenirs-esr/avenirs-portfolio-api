package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.user.domain.port.output.BaseUserService;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.user.domain.data.LoggedInUserData;
import fr.avenirsesr.portfolio.user.domain.data.UserQuickLinksData;
import java.util.UUID;

@SuppressWarnings("PMD.MissingOverride")
public interface UserService extends BaseUserService {
  User getUser(UUID id);

  LoggedInUserData getMe();

  void updateProfile(EUserCategory userCategory, String email, String bio);

  void updateNotificationPreferences(boolean notificationEnabled);

  User createUser(UUID id, String firstname, String lastname, String email, String eppn);

  UserQuickLinksData getQuickLinks(EUserCategory userCategory);

  File uploadProfilePicture(
      EUserCategory userCategory, String fileName, String mimeType, long size, byte[] content);

  void deleteProfilePicture(EUserCategory userCategory);

  File uploadCoverPicture(
      EUserCategory userCategory, String fileName, String mimeType, long size, byte[] content);

  void deleteCoverPicture(EUserCategory userCategory);
}
