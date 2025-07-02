package fr.avenirsesr.portfolio.user.domain.port.output.repository;

import java.io.IOException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface RessourceRepository {

  byte[] getStudentPhoto(String filename) throws IOException;

  byte[] getStudentCover(String filename) throws IOException;

  byte[] getTeacherPhoto(String filename) throws IOException;

  byte[] getTeacherCover(String filename) throws IOException;

  String storeStudentProfilePicture(UUID userId, MultipartFile photoFile) throws IOException;

  String storeStudentCoverPicture(UUID userId, MultipartFile photoFile) throws IOException;

  String storeTeacherProfilePicture(UUID userId, MultipartFile photoFile) throws IOException;

  String storeTeacherCoverPicture(UUID userId, MultipartFile photoFile) throws IOException;
}
