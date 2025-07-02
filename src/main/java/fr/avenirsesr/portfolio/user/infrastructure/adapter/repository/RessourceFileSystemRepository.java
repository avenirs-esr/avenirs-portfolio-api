package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.domain.port.output.repository.RessourceRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class RessourceFileSystemRepository implements RessourceRepository {

  @Value("${server.url}")
  private String baseUrl;

  @Value("${photo.storage.student.path}")
  private String photoStudentPath;

  @Value("${photo.storage.teacher.path}")
  private String photoTeacherPath;

  @Value("${cover.storage.student.path}")
  private String coverStudentPath;

  @Value("${cover.storage.teacher.path}")
  private String coverTeacherPath;

  @Override
  public byte[] getStudentPhoto(String filename) throws IOException {
    Path filePath = Paths.get(photoStudentPath).resolve(filename);
    return Files.readAllBytes(filePath);
  }

  @Override
  public byte[] getStudentCover(String filename) throws IOException {
    Path filePath = Paths.get(coverStudentPath).resolve(filename);
    return Files.readAllBytes(filePath);
  }

  @Override
  public byte[] getTeacherPhoto(String filename) throws IOException {
    Path filePath = Paths.get(photoTeacherPath).resolve(filename);
    return Files.readAllBytes(filePath);
  }

  @Override
  public byte[] getTeacherCover(String filename) throws IOException {
    Path filePath = Paths.get(coverTeacherPath).resolve(filename);
    return Files.readAllBytes(filePath);
  }

  @Override
  public String storeStudentProfilePicture(UUID userId, MultipartFile profilePictureFile)
      throws IOException {
    return storePicture(userId, profilePictureFile, photoStudentPath);
  }

  @Override
  public String storeStudentCoverPicture(UUID userId, MultipartFile coverPictureFile)
      throws IOException {
    return storePicture(userId, coverPictureFile, coverStudentPath);
  }

  @Override
  public String storeTeacherProfilePicture(UUID userId, MultipartFile profilePictureFile)
      throws IOException {
    return storePicture(userId, profilePictureFile, photoTeacherPath);
  }

  @Override
  public String storeTeacherCoverPicture(UUID userId, MultipartFile coverPictureFile)
      throws IOException {
    return storePicture(userId, coverPictureFile, coverTeacherPath);
  }

  private String storePicture(UUID userId, MultipartFile pictureFile, String storagePath)
      throws IOException {
    Instant instantNow = Instant.now();
    String filename =
        userId.toString()
            + "_"
            + instantNow.toEpochMilli()
            + "_"
            + pictureFile.getOriginalFilename();
    Path filePath = Paths.get(storagePath).resolve(filename);
    Files.createDirectories(filePath.getParent());
    Files.copy(pictureFile.getInputStream(), filePath);
    return baseUrl + storagePath.substring(1) + "/" + filename;
  }
}
