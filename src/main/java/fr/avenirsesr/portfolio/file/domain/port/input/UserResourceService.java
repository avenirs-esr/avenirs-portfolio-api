package fr.avenirsesr.portfolio.file.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import java.util.UUID;

public interface UserResourceService {
  FileData getUserPhotoUrl(User user, EUserCategory userCategory, EUserPhotoType type);

  UserPhoto getUserPhotoById(UUID fileId);

  byte[] fetchContent(UserPhoto userPhoto);

  UserPhoto uploadPhoto(
      EUserCategory category,
      EUserPhotoType type,
      String fileName,
      String mimeType,
      long size,
      byte[] content);

  void deletePhoto(UUID fileId);
}
