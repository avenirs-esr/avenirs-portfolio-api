package fr.avenirsesr.portfolio.user.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.RessourceRepository;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RessourceServiceImplTest {

  @Mock private RessourceRepository ressourceRepository;

  @InjectMocks private RessourceServiceImpl ressourceService;

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
}
