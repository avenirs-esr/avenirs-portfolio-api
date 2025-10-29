package fr.avenirsesr.portfolio.file.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.file.domain.data.UserPhotoData;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import java.io.IOException;
import java.util.UUID;

public interface UserResourceService {
  UserPhotoData getUserPhotoUrl(User user, EUserCategory userCategory, EUserPhotoType type);

  UserPhoto getUserPhotoById(UUID fileId);

  byte[] fetchContent(UserPhoto userPhoto) throws IOException;

  UserPhoto uploadPhoto(
      EUserCategory category,
      EUserPhotoType type,
      String fileName,
      String mimeType,
      long size,
      byte[] content)
      throws IOException;

  void deletePhoto(UUID fileId);
}
