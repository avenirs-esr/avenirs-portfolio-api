package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface RessourceService {

  byte[] getPhoto(EUserCategory userCategory, String filename) throws IOException;

  byte[] getCover(EUserCategory userCategory, String filename) throws IOException;

  String uploadStudentProfilePicture(User user, MultipartFile photoFile) throws IOException;

  String uploadStudentCoverPicture(User user, MultipartFile photoFile) throws IOException;

  String uploadTeacherProfilePicture(User user, MultipartFile photoFile) throws IOException;

  String uploadTeacherCoverPicture(User user, MultipartFile photoFile) throws IOException;
}
