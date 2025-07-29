package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.user.domain.model.FileUploadLink;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.UserFileUpload;
import fr.avenirsesr.portfolio.user.domain.model.enums.EContextType;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUploadType;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.RessourceRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UploadLinkRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserFileUploadRepository;
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

  @Mock private UserFileUploadRepository userFileUploadRepository;

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
  void shouldUploadProfilePicture() throws IOException {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);
    when(mockFile.getContentType()).thenReturn("image/jpeg");

    when(ressourceRepository.storeStudentProfilePicture(student.getId(), mockFile))
        .thenReturn(
            "https://baseUrl.com/photo/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg");

    ressourceService.uploadProfilePicture(student, mockFile);
    // Then
    ArgumentCaptor<UserFileUpload> userUploadCaptor = ArgumentCaptor.forClass(UserFileUpload.class);
    verify(userFileUploadRepository).save(userUploadCaptor.capture());

    ArgumentCaptor<FileUploadLink> uploadLinkCaptor = ArgumentCaptor.forClass(FileUploadLink.class);
    verify(uploadLinkRepository).save(uploadLinkCaptor.capture());

    UserFileUpload savedUserFileUpload = userUploadCaptor.getValue();
    FileUploadLink savedFileUploadLink = uploadLinkCaptor.getValue();

    assertEquals(student.getId(), savedUserFileUpload.getUser().getId());
    assertEquals(EUploadType.PROFILE_PICTURE, savedUserFileUpload.getType());
    assertEquals(
        "https://baseUrl.com/photo/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg",
        savedUserFileUpload.getUrl());
    assertEquals(UserServiceImpl.MAX_SIZE, savedUserFileUpload.getSize());
    assertEquals("image/jpeg", savedUserFileUpload.getMediaType());

    assertEquals(savedUserFileUpload.getId(), savedFileUploadLink.getUploadId());
    assertEquals(EContextType.PROFILE, savedFileUploadLink.getContextType());
    assertEquals(student.getId(), savedFileUploadLink.getContextId());
  }

  @Test
  void shouldUploadCoverPicture() throws IOException {
    // Given
    MultipartFile mockFile = mock(MultipartFile.class);

    // When
    when(mockFile.getSize()).thenReturn(UserServiceImpl.MAX_SIZE);
    when(mockFile.getContentType()).thenReturn("image/jpeg");

    when(ressourceRepository.storeStudentCoverPicture(student.getId(), mockFile))
        .thenReturn(
            "https://baseUrl.com/photo/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg");

    ressourceService.uploadCoverPicture(student, mockFile);
    // Then
    ArgumentCaptor<UserFileUpload> userUploadCaptor = ArgumentCaptor.forClass(UserFileUpload.class);
    verify(userFileUploadRepository).save(userUploadCaptor.capture());

    ArgumentCaptor<FileUploadLink> uploadLinkCaptor = ArgumentCaptor.forClass(FileUploadLink.class);
    verify(uploadLinkRepository).save(uploadLinkCaptor.capture());

    UserFileUpload savedUserFileUpload = userUploadCaptor.getValue();
    FileUploadLink savedFileUploadLink = uploadLinkCaptor.getValue();

    assertEquals(student.getId(), savedUserFileUpload.getUser().getId());
    assertEquals(EUploadType.COVER_PICTURE, savedUserFileUpload.getType());
    assertEquals(
        "https://baseUrl.com/photo/student/062f14a0-0575-481b-9457-47005945609d_1748423323502_spring-images-min.jpg",
        savedUserFileUpload.getUrl());
    assertEquals(UserServiceImpl.MAX_SIZE, savedUserFileUpload.getSize());
    assertEquals("image/jpeg", savedUserFileUpload.getMediaType());

    assertEquals(savedUserFileUpload.getId(), savedFileUploadLink.getUploadId());
    assertEquals(EContextType.PROFILE, savedFileUploadLink.getContextType());
    assertEquals(student.getId(), savedFileUploadLink.getContextId());
  }
}
