package fr.avenirsesr.portfolio.file.domain.port.input;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import java.io.IOException;
import java.util.UUID;

public interface UserResourceService {
  String getUserPhotoUrl(User user, EUserCategory userCategory, EUserPhotoType type);

  UserPhoto getUserPhotoById(UUID fileId);

  byte[] fetchContent(UserPhoto userPhoto) throws IOException;

  UserPhoto uploadPhoto(
      User user,
      EUserCategory category,
      EUserPhotoType type,
      String fileName,
      String mimeType,
      long size,
      byte[] content)
      throws IOException;
}
