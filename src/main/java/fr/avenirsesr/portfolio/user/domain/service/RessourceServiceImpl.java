package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.user.domain.model.FileUploadLink;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.UserFileUpload;
import fr.avenirsesr.portfolio.user.domain.model.enums.EContextType;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUploadType;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.input.RessourceService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.RessourceRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UploadLinkRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserFileUploadRepository;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Service
public class RessourceServiceImpl implements RessourceService {

  private final RessourceRepository ressourceRepository;
  private final UserFileUploadRepository userFileUploadRepository;
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
  public String uploadProfilePicture(Student student, MultipartFile photoFile) throws IOException {
    String pictureUrl = ressourceRepository.storeStudentProfilePicture(student.getId(), photoFile);
    saveUploadHistory(student.getUser(), photoFile, EUploadType.PROFILE_PICTURE, pictureUrl);
    return pictureUrl;
  }

  @Override
  public String uploadProfilePicture(Teacher teacher, MultipartFile photoFile) throws IOException {
    String pictureUrl = ressourceRepository.storeTeacherProfilePicture(teacher.getId(), photoFile);
    saveUploadHistory(teacher.getUser(), photoFile, EUploadType.PROFILE_PICTURE, pictureUrl);
    return pictureUrl;
  }

  @Override
  public String uploadCoverPicture(Student student, MultipartFile photoFile) throws IOException {
    String pictureUrl = ressourceRepository.storeStudentCoverPicture(student.getId(), photoFile);
    saveUploadHistory(student.getUser(), photoFile, EUploadType.COVER_PICTURE, pictureUrl);
    return pictureUrl;
  }

  @Override
  public String uploadCoverPicture(Teacher teacher, MultipartFile photoFile) throws IOException {
    String pictureUrl = ressourceRepository.storeTeacherCoverPicture(teacher.getId(), photoFile);
    saveUploadHistory(teacher.getUser(), photoFile, EUploadType.COVER_PICTURE, pictureUrl);
    return pictureUrl;
  }

  private void saveUploadHistory(
      User user, MultipartFile photoFile, EUploadType uploadType, String pictureUrl) {
    UserFileUpload userFileUpload =
        UserFileUpload.create(
            UUID.randomUUID(),
            user,
            uploadType,
            pictureUrl,
            photoFile.getSize(),
            photoFile.getContentType());
    FileUploadLink fileUploadLink =
        FileUploadLink.create(userFileUpload.getId(), EContextType.PROFILE, user.getId());
    userFileUploadRepository.save(userFileUpload);
    uploadLinkRepository.save(fileUploadLink);
  }
}
