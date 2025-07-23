package fr.avenirsesr.portfolio.ams.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.AMSFixture;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
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
  private UUID programProgressId;
  private static final Integer DEFAULT_PAGE = 1;
  private static final Integer DEFAULT_SIZE = 10;
  private static final PageCriteria DEFAULT_PAGE_CRITERIA =
      new PageCriteria(DEFAULT_PAGE, DEFAULT_SIZE);

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
    studentId = student.getId();
    programProgressId = UUID.randomUUID();
  }

  @Test
  void shouldReturnPagedAMSForUser() {
    // Given
    List<AMS> amsList = AMSFixture.create().withCount(3);
    PagedResult<AMS> expectedResult =
        new PagedResult<>(amsList, new PageInfo(DEFAULT_PAGE, DEFAULT_SIZE, 3));

    when(amsRepository.findByUserIdViaCohortsAndProgramProgressId(
            any(UUID.class), eq(programProgressId), eq(DEFAULT_PAGE_CRITERIA)))
        .thenReturn(expectedResult);

    // When
    PagedResult<AMS> result =
        amsService.findUserAmsByProgramProgress(student, programProgressId, DEFAULT_PAGE_CRITERIA);

    // Then
    assertNotNull(result);
    assertEquals(3, result.pageInfo().totalElements());
    assertEquals(3, result.content().size());
    assertEquals(DEFAULT_PAGE, result.pageInfo().page());
    assertEquals(DEFAULT_SIZE, result.pageInfo().pageSize());
    verify(amsRepository)
        .findByUserIdViaCohortsAndProgramProgressId(
            any(UUID.class), eq(programProgressId), eq(DEFAULT_PAGE_CRITERIA));
  }

  @Test
  void shouldReturnEmptyPagedResultWhenUserHasNoAMS() {
    // Given
    PagedResult<AMS> expectedResult =
        new PagedResult<>(new ArrayList<>(), new PageInfo(DEFAULT_PAGE, DEFAULT_SIZE, 0));

    when(amsRepository.findByUserIdViaCohortsAndProgramProgressId(
            any(UUID.class), eq(programProgressId), eq(DEFAULT_PAGE_CRITERIA)))
        .thenReturn(expectedResult);

    // When
    PagedResult<AMS> result =
        amsService.findUserAmsByProgramProgress(student, programProgressId, DEFAULT_PAGE_CRITERIA);

    // Then
    assertNotNull(result);
    assertEquals(0, result.pageInfo().totalElements());
    assertTrue(result.content().isEmpty());
    assertEquals(DEFAULT_PAGE, result.pageInfo().page());
    assertEquals(DEFAULT_SIZE, result.pageInfo().pageSize());
    verify(amsRepository)
        .findByUserIdViaCohortsAndProgramProgressId(
            any(UUID.class), eq(programProgressId), eq(DEFAULT_PAGE_CRITERIA));
  }

  @Test
  void shouldHandlePagination() {
    // Given
    Integer page = 1;
    Integer size = 5;
    List<AMS> amsList = AMSFixture.create().withCount(5);
    PagedResult<AMS> expectedResult = new PagedResult<>(amsList, new PageInfo(page, size, 15));

    when(amsRepository.findByUserIdViaCohortsAndProgramProgressId(
            any(UUID.class), eq(programProgressId), eq(new PageCriteria(page, size))))
        .thenReturn(expectedResult);

    // When
    PagedResult<AMS> result =
        amsService.findUserAmsByProgramProgress(
            student, programProgressId, new PageCriteria(page, size));

    // Then
    assertNotNull(result);
    assertEquals(15, result.pageInfo().totalElements());
    assertEquals(5, result.content().size());
    assertEquals(page, result.pageInfo().page());
    assertEquals(size, result.pageInfo().pageSize());
    verify(amsRepository)
        .findByUserIdViaCohortsAndProgramProgressId(
            any(UUID.class), eq(programProgressId), eq(new PageCriteria(page, size)));
  }

  @Test
  void shouldUseDefaultValuesWhenPaginationParametersAreNull() {
    // Given
    List<AMS> amsList = AMSFixture.create().withCount(3);
    PagedResult<AMS> expectedResult = new PagedResult<>(amsList, new PageInfo(1, 8, 3));

    when(amsRepository.findByUserIdViaCohortsAndProgramProgressId(
            any(UUID.class), eq(programProgressId), eq(new PageCriteria(1, 8))))
        .thenReturn(expectedResult);

    // When
    PagedResult<AMS> result =
        amsService.findUserAmsByProgramProgress(
            student, programProgressId, new PageCriteria(null, null));

    // Then
    assertNotNull(result);
    assertEquals(3, result.pageInfo().totalElements());
    assertEquals(3, result.content().size());
    assertEquals(1, result.pageInfo().page());
    assertEquals(8, result.pageInfo().pageSize());
    verify(amsRepository)
        .findByUserIdViaCohortsAndProgramProgressId(
            any(UUID.class), eq(programProgressId), eq(new PageCriteria(1, 8)));
  }
}
