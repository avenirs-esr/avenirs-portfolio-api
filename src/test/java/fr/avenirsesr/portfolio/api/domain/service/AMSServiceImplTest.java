package fr.avenirsesr.portfolio.api.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fixtures.AMSFixture;
import fixtures.UserFixture;
import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.PagedResult;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.AMSRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AMSServiceImplTest {

  @Mock private AMSRepository amsRepository;

  @InjectMocks private AMSServiceImpl amsService;

  private Student student;
  private UUID studentId;
  private int defaultPage;
  private int defaultSize;

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
    studentId = student.getId();
    defaultPage = 0;
    defaultSize = 10;
  }

  @Test
  void shouldReturnPagedAMSForUser() {
    // Given
    List<AMS> amsList = AMSFixture.create().withCount(3);
    PagedResult<AMS> expectedResult = new PagedResult<>(amsList, 3, 1);

    when(amsRepository.findByUserIdViaCohorts(eq(studentId), eq(defaultPage), eq(defaultSize)))
        .thenReturn(expectedResult);

    // When
    PagedResult<AMS> result =
        amsService.findUserAmsWithPagination(student, defaultPage, defaultSize);

    // Then
    assertNotNull(result);
    assertEquals(3, result.getTotalElements());
    assertEquals(1, result.getTotalPages());
    assertEquals(3, result.getContent().size());
    verify(amsRepository).findByUserIdViaCohorts(studentId, defaultPage, defaultSize);
  }

  @Test
  void shouldReturnEmptyPagedResultWhenUserHasNoAMS() {
    // Given
    PagedResult<AMS> expectedResult = new PagedResult<>(new ArrayList<>(), 0, 0);

    when(amsRepository.findByUserIdViaCohorts(eq(studentId), eq(defaultPage), eq(defaultSize)))
        .thenReturn(expectedResult);

    // When
    PagedResult<AMS> result =
        amsService.findUserAmsWithPagination(student, defaultPage, defaultSize);

    // Then
    assertNotNull(result);
    assertEquals(0, result.getTotalElements());
    assertEquals(0, result.getTotalPages());
    assertTrue(result.getContent().isEmpty());
    verify(amsRepository).findByUserIdViaCohorts(studentId, defaultPage, defaultSize);
  }

  @Test
  void shouldHandlePagination() {
    // Given
    int page = 1;
    int size = 5;
    List<AMS> amsList = AMSFixture.create().withCount(5);
    PagedResult<AMS> expectedResult = new PagedResult<>(amsList, 15, 3);

    when(amsRepository.findByUserIdViaCohorts(eq(studentId), eq(page), eq(size)))
        .thenReturn(expectedResult);

    // When
    PagedResult<AMS> result = amsService.findUserAmsWithPagination(student, page, size);

    // Then
    assertNotNull(result);
    assertEquals(15, result.getTotalElements());
    assertEquals(3, result.getTotalPages());
    assertEquals(5, result.getContent().size());
    verify(amsRepository).findByUserIdViaCohorts(studentId, page, size);
  }
}
