package fr.avenirsesr.portfolio.api.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.test.util.ReflectionTestUtils;

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

    ReflectionTestUtils.setField(amsService, "defaultPage", 0);
    ReflectionTestUtils.setField(amsService, "defaultPageSize", 8);
    ReflectionTestUtils.setField(amsService, "maxPageSize", 12);
  }

  @Test
  void shouldReturnPagedAMSForUser() {
    // Given
    List<AMS> amsList = AMSFixture.create().withCount(3);
    PagedResult<AMS> expectedResult = new PagedResult<>(amsList, 3, 1, defaultPage, defaultSize);

    when(amsRepository.findByUserIdViaCohorts(any(UUID.class), eq(defaultPage), eq(defaultSize)))
        .thenReturn(expectedResult);

    // When
    PagedResult<AMS> result =
        amsService.findUserAmsWithPagination(student, defaultPage, defaultSize);

    // Then
    assertNotNull(result);
    assertEquals(3, result.totalElements());
    assertEquals(1, result.totalPages());
    assertEquals(3, result.content().size());
    assertEquals(defaultPage, result.page());
    assertEquals(defaultSize, result.pageSize());
    verify(amsRepository).findByUserIdViaCohorts(any(UUID.class), eq(defaultPage), eq(defaultSize));
  }

  @Test
  void shouldReturnEmptyPagedResultWhenUserHasNoAMS() {
    // Given
    PagedResult<AMS> expectedResult =
        new PagedResult<>(new ArrayList<>(), 0, 0, defaultPage, defaultSize);

    when(amsRepository.findByUserIdViaCohorts(any(UUID.class), eq(defaultPage), eq(defaultSize)))
        .thenReturn(expectedResult);

    // When
    PagedResult<AMS> result =
        amsService.findUserAmsWithPagination(student, defaultPage, defaultSize);

    // Then
    assertNotNull(result);
    assertEquals(0, result.totalElements());
    assertEquals(0, result.totalPages());
    assertTrue(result.content().isEmpty());
    assertEquals(defaultPage, result.page());
    assertEquals(defaultSize, result.pageSize());
    verify(amsRepository).findByUserIdViaCohorts(any(UUID.class), eq(defaultPage), eq(defaultSize));
  }

  @Test
  void shouldHandlePagination() {
    // Given
    int page = 1;
    int size = 5;
    List<AMS> amsList = AMSFixture.create().withCount(5);
    PagedResult<AMS> expectedResult = new PagedResult<>(amsList, 15, 3, page, size);

    when(amsRepository.findByUserIdViaCohorts(any(UUID.class), eq(page), eq(size)))
        .thenReturn(expectedResult);

    // When
    PagedResult<AMS> result = amsService.findUserAmsWithPagination(student, page, size);

    // Then
    assertNotNull(result);
    assertEquals(15, result.totalElements());
    assertEquals(3, result.totalPages());
    assertEquals(5, result.content().size());
    assertEquals(page, result.page());
    assertEquals(size, result.pageSize());
    verify(amsRepository).findByUserIdViaCohorts(any(UUID.class), eq(page), eq(size));
  }
}
