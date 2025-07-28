package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.UploadLink;
import fr.avenirsesr.portfolio.user.domain.model.UserUpload;
import fr.avenirsesr.portfolio.user.domain.model.enums.EContextType;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUploadType;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.RessourceRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UploadLinkRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserUploadRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class RessourceServiceImplTest {

  @Mock private RessourceRepository ressourceRepository;

  @Mock private UserUploadRepository userUploadRepository;

  @Mock private UploadLinkRepository uploadLinkRepository;

  @InjectMocks private RessourceServiceImpl ressourceService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @Test
  void shouldGetPhoto() throws IOException {
    // Given
    String studentText = "Student";
    byte[] studentBytes = studentText.getBytes();

    String teacherText = "Teacher";
    byte[] teacherBytes = teacherText.getBytes();

    // When
    when(ressourceRepository.getStudentPhoto(anyString())).thenReturn(studentBytes);
    when(ressourceRepository.getTeacherPhoto(anyString())).thenReturn(teacherBytes);
    byte[] studentResult = ressourceService.getPhoto(EUserCategory.STUDENT, "student.png");
    byte[] teacherResult = ressourceService.getPhoto(EUserCategory.TEACHER, "teacher.png");

    // Then
    assertEquals(studentBytes, studentResult);
    assertEquals(teacherBytes, teacherResult);
  }

  @Test
  void shouldGetCover() throws IOException {
    // Given
    String studentText = "Student";
    byte[] studentBytes = studentText.getBytes();

    String teacherText = "Teacher";
    byte[] teacherBytes = teacherText.getBytes();

    // When
    when(ressourceRepository.getStudentCover(anyString())).thenReturn(studentBytes);
    when(ressourceRepository.getTeacherCover(anyString())).thenReturn(teacherBytes);
    byte[] studentResult = ressourceService.getCover(EUserCategory.STUDENT, "student.png");
    byte[] teacherResult = ressourceService.getCover(EUserCategory.TEACHER, "teacher.png");

    // Then
    assertEquals(studentBytes, studentResult);
    assertEquals(teacherBytes, teacherResult);
  }

  @Test
  void shouldUploadStudentProfilePicture() throws IOException {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);
    when(mockFile.getContentType()).thenReturn("image/jpeg");

    when(ressourceRepository.storeStudentProfilePicture(student.getId(), mockFile))
        .thenReturn(
            "https://baseUrl.com/photo/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg");

    ressourceService.uploadStudentProfilePicture(student.getUser(), mockFile);
    // Then
    ArgumentCaptor<UserUpload> userUploadCaptor = ArgumentCaptor.forClass(UserUpload.class);
    verify(userUploadRepository).save(userUploadCaptor.capture());

    ArgumentCaptor<UploadLink> uploadLinkCaptor = ArgumentCaptor.forClass(UploadLink.class);
    verify(uploadLinkRepository).save(uploadLinkCaptor.capture());

    UserUpload savedUserUpload = userUploadCaptor.getValue();
    UploadLink savedUploadLink = uploadLinkCaptor.getValue();

    assertEquals(student.getId(), savedUserUpload.getUserId());
    assertEquals(EUploadType.PROFILE_PICTURE, savedUserUpload.getType());
    assertEquals(
        "https://baseUrl.com/photo/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg",
        savedUserUpload.getUrl());
    assertEquals(UserServiceImpl.MAX_SIZE, savedUserUpload.getSize());
    assertEquals("image/jpeg", savedUserUpload.getMediaType());

    assertEquals(savedUserUpload.getId(), savedUploadLink.getUploadId());
    assertEquals(EContextType.PROFILE, savedUploadLink.getContextType());
    assertEquals(student.getId(), savedUploadLink.getContextId());
  }

  @Test
  void shouldUploadStudentCoverPicture() throws IOException {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);
    when(mockFile.getContentType()).thenReturn("image/jpeg");

    when(ressourceRepository.storeStudentCoverPicture(student.getId(), mockFile))
        .thenReturn(
            "https://baseUrl.com/photo/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg");

    ressourceService.uploadStudentCoverPicture(student.getUser(), mockFile);
    // Then
    ArgumentCaptor<UserUpload> userUploadCaptor = ArgumentCaptor.forClass(UserUpload.class);
    verify(userUploadRepository).save(userUploadCaptor.capture());

    ArgumentCaptor<UploadLink> uploadLinkCaptor = ArgumentCaptor.forClass(UploadLink.class);
    verify(uploadLinkRepository).save(uploadLinkCaptor.capture());

    UserUpload savedUserUpload = userUploadCaptor.getValue();
    UploadLink savedUploadLink = uploadLinkCaptor.getValue();

    assertEquals(student.getId(), savedUserUpload.getUserId());
    assertEquals(EUploadType.COVER_PICTURE, savedUserUpload.getType());
    assertEquals(
        "https://baseUrl.com/photo/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg",
        savedUserUpload.getUrl());
    assertEquals(UserServiceImpl.MAX_SIZE, savedUserUpload.getSize());
    assertEquals("image/jpeg", savedUserUpload.getMediaType());

    assertEquals(savedUserUpload.getId(), savedUploadLink.getUploadId());
    assertEquals(EContextType.PROFILE, savedUploadLink.getContextType());
    assertEquals(student.getId(), savedUploadLink.getContextId());
  }
}
