package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.user.domain.model.UploadLink;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.UserUpload;
import fr.avenirsesr.portfolio.user.domain.model.enums.EContextType;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUploadType;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.input.RessourceService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.RessourceRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UploadLinkRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserUploadRepository;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Service
public class RessourceServiceImpl implements RessourceService {

  private final RessourceRepository ressourceRepository;
  private final UserUploadRepository userUploadRepository;
  private final UploadLinkRepository uploadLinkRepository;

  @Override
  public byte[] getPhoto(EUserCategory userCategory, String filename) throws IOException {
    if (userCategory == EUserCategory.STUDENT) {
      return ressourceRepository.getStudentPhoto(filename);
    } else {
      return ressourceRepository.getTeacherPhoto(filename);
    }
  }

  @Override
  public byte[] getCover(EUserCategory userCategory, String filename) throws IOException {
    if (userCategory == EUserCategory.STUDENT) {
      return ressourceRepository.getStudentCover(filename);
    } else {
      return ressourceRepository.getTeacherCover(filename);
    }
  }

  @Override
  public String uploadStudentProfilePicture(User user, MultipartFile photoFile) throws IOException {
    String pictureUrl = ressourceRepository.storeStudentProfilePicture(user.getId(), photoFile);
    saveUploadHistory(user, photoFile, EUploadType.PROFILE_PICTURE, pictureUrl);
    return pictureUrl;
  }

  @Override
  public String uploadStudentCoverPicture(User user, MultipartFile photoFile) throws IOException {
    String pictureUrl = ressourceRepository.storeStudentCoverPicture(user.getId(), photoFile);
    saveUploadHistory(user, photoFile, EUploadType.COVER_PICTURE, pictureUrl);
    return pictureUrl;
  }

  @Override
  public String uploadTeacherProfilePicture(User user, MultipartFile photoFile) throws IOException {
    String pictureUrl = ressourceRepository.storeTeacherProfilePicture(user.getId(), photoFile);
    saveUploadHistory(user, photoFile, EUploadType.PROFILE_PICTURE, pictureUrl);
    return pictureUrl;
  }

  @Override
  public String uploadTeacherCoverPicture(User user, MultipartFile photoFile) throws IOException {
    String pictureUrl = ressourceRepository.storeTeacherCoverPicture(user.getId(), photoFile);
    saveUploadHistory(user, photoFile, EUploadType.COVER_PICTURE, pictureUrl);
    return pictureUrl;
  }

  private void saveUploadHistory(
      User user, MultipartFile photoFile, EUploadType uploadType, String pictureUrl) {
    UserUpload userUpload =
        UserUpload.create(
            UUID.randomUUID(),
            user.getId(),
            uploadType,
            pictureUrl,
            photoFile.getSize(),
            photoFile.getContentType());
    UploadLink uploadLink =
        UploadLink.create(userUpload.getId(), EContextType.PROFILE, user.getId());
    userUploadRepository.save(userUpload);
    uploadLinkRepository.save(uploadLink);
  }
}
