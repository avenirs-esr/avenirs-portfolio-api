package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface RessourceService {

  byte[] getPhoto(EUserCategory userCategory, String filename) throws IOException;

  byte[] getCover(EUserCategory userCategory, String filename) throws IOException;

  String uploadProfilePicture(Student student, MultipartFile photoFile) throws IOException;

  String uploadProfilePicture(Teacher teacher, MultipartFile photoFile) throws IOException;

  String uploadCoverPicture(Student student, MultipartFile photoFile) throws IOException;

  String uploadCoverPicture(Teacher teacher, MultipartFile photoFile) throws IOException;
}
