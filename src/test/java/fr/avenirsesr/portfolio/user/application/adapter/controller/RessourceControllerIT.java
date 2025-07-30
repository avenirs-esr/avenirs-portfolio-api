package fr.avenirsesr.portfolio.user.application.adapter.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import jakarta.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RessourceControllerIT {

  private final String studentPhotoFile = "photo-student.png";
  private final String studentCoverFile = "cover-student.jpg";
  private final String teacherPhotoFile = "photo-teacher.png";
  private final String teacherCoverFile = "cover-teacher.jpg";
  @Resource private MockMvc mockMvc;

  @Value("${hmac.secret-key}")
  private String secretKey;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.teacher.payload}")
  private String teacherPayload;

  @Value("${user.teacher.signature}")
  private String teacherSignature;

  @Value("${photo.storage.student.path}")
  private String studentPhotoPath;

  @Value("${cover.storage.student.path}")
  private String studentCoverPath;

  @Value("${cover.storage.teacher.path}")
  private String teacherCoverPath;

  @Value("${photo.storage.teacher.path}")
  private String teacherPhotoPath;

  @BeforeEach
  void setup() throws Exception {
    Path studentPhoto = Path.of(studentPhotoPath, studentPhotoFile);
    Path teacherCover = Path.of(teacherCoverPath, teacherCoverFile);
    Path studentCover = Path.of(studentCoverPath, studentCoverFile);
    Path teacherPhoto = Path.of(teacherPhotoPath, teacherPhotoFile);

    Files.createDirectories(studentPhoto.getParent());
    Files.write(studentPhoto, new byte[] {(byte) 137, 80, 78, 71, 13, 10, 26, 10});
    Files.createDirectories(teacherCover.getParent());
    Files.write(teacherCover, new byte[] {(byte) 255, (byte) 216, (byte) 255});
    Files.createDirectories(studentCover.getParent());
    Files.write(studentCover, new byte[] {(byte) 255, (byte) 216, (byte) 255});
    Files.createDirectories(teacherPhoto.getParent());
    Files.write(teacherPhoto, new byte[] {(byte) 137, 80, 78, 71, 13, 10, 26, 10});
  }

  @Test
  void shouldReturnStudentPhotoWithCorrectContentType() throws Exception {
    mockMvc
        .perform(get("/photo/STUDENT/" + studentPhotoFile))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_PNG));
  }

  @Test
  void shouldReturnTeacherCoverWithCorrectContentType() throws Exception {
    mockMvc
        .perform(get("/cover/TEACHER/" + teacherCoverFile))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG));
  }

  @Test
  void shouldReturnTeacherPhotoWithCorrectContentType() throws Exception {
    mockMvc
        .perform(get("/photo/TEACHER/" + teacherPhotoFile))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_PNG));
  }

  @Test
  void shouldReturnStudentCoverWithCorrectContentType() throws Exception {
    mockMvc
        .perform(get("/cover/STUDENT/" + studentCoverFile))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG));
  }

  @Test
  void shouldReturnAllErrorCodes() throws Exception {
    mockMvc
        .perform(
            get("/errors")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[?(@ =~ /.*USER_NOT_FOUND.*/)]").exists());
  }

  @Test
  void shouldReturnAllUserCategories() throws Exception {
    mockMvc
        .perform(
            get("/user-categories")
                .header("X-Signed-Context", studentPayload)
                .header("X-Context-Kid", secretKey)
                .header("X-Context-Signature", studentSignature))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0]").value(EUserCategory.TEACHER.name()));
  }
}
