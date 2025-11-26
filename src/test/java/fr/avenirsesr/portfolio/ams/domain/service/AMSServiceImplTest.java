package fr.avenirsesr.portfolio.ams.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.ams.domain.dto.AmsView;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.AMSFixture;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AMSServiceImplTest {

  @Mock private AMSRepository amsRepository;
  @Mock private StudentProgressRepository studentProgressRepository;
  @Mock private SkillLevelProgressRepository skillLevelProgressRepository;
  @Mock private TraceRepository traceRepository;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private AMSServiceImpl amsService;

  private Student student;
  private UUID studentProgressId;
  private static final Integer DEFAULT_PAGE = 1;
  private static final Integer DEFAULT_SIZE = 10;
  private static final PageCriteria DEFAULT_PAGE_CRITERIA =
      new PageCriteria(DEFAULT_PAGE, DEFAULT_SIZE);
  private StudentProgress studentProgress;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    studentProgressId = UUID.randomUUID();
    studentProgress = StudentProgressFixture.create().toModel();
  }

  @Test
  void shouldReturnPagedAMSForUser() {
    BddLogger.given("the findUserAmsByStudentProgress method from AMSServiceImpl");
    List<AMS> amsList = AMSFixture.create().withCount(3);
    PagedResult<AMS> expectedResult =
        new PagedResult<>(amsList, new PageInfo(DEFAULT_PAGE, DEFAULT_SIZE, 3));

    when(amsRepository.findByStudent(any(Student.class), eq(DEFAULT_PAGE_CRITERIA)))
        .thenReturn(expectedResult);
    when(studentProgressRepository.findById(eq(studentProgressId)))
        .thenReturn(Optional.of(studentProgress));
    when(skillLevelProgressRepository.linkedWith(any())).thenReturn(Collections.emptyList());
    when(traceRepository.linkedWith(any(AMS.class))).thenReturn(Collections.emptyList());
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    try (MockedStatic<RequestContext> mockedRequestContext = mockStatic(RequestContext.class)) {
      mockedRequestContext
          .when(RequestContext::get)
          .thenReturn(new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));

      BddLogger.when("calling the method with an user with AMS");
      PagedResult<AmsView> result =
          amsService.findUserAmsByStudentProgress(studentProgressId, DEFAULT_PAGE_CRITERIA);

      BddLogger.then("it should return paged AMS");
      assertNotNull(result);
      assertEquals(3, result.pageInfo().totalElements());
      assertEquals(3, result.content().size());
      assertEquals(DEFAULT_PAGE, result.pageInfo().page());
      assertEquals(DEFAULT_SIZE, result.pageInfo().pageSize());
      verify(amsRepository).findByStudent(any(Student.class), eq(DEFAULT_PAGE_CRITERIA));
    }
  }

  @Test
  void shouldReturnEmptyPagedResultWhenUserHasNoAMS() {
    BddLogger.given("the findUserAmsByStudentProgress method from AMSServiceImpl");
    PagedResult<AMS> expectedResult =
        new PagedResult<>(new ArrayList<>(), new PageInfo(DEFAULT_PAGE, DEFAULT_SIZE, 0));

    when(amsRepository.findByStudent(any(Student.class), eq(DEFAULT_PAGE_CRITERIA)))
        .thenReturn(expectedResult);
    when(studentProgressRepository.findById(eq(studentProgressId)))
        .thenReturn(Optional.of(studentProgress));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    try (MockedStatic<RequestContext> mockedRequestContext = mockStatic(RequestContext.class)) {
      mockedRequestContext
          .when(RequestContext::get)
          .thenReturn(new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));

      BddLogger.when("calling the method with an user without AMS");
      PagedResult<AmsView> result =
          amsService.findUserAmsByStudentProgress(studentProgressId, DEFAULT_PAGE_CRITERIA);

      BddLogger.then("it should return an empty paged result");
      assertNotNull(result);
      assertEquals(0, result.pageInfo().totalElements());
      assertTrue(result.content().isEmpty());
      assertEquals(DEFAULT_PAGE, result.pageInfo().page());
      assertEquals(DEFAULT_SIZE, result.pageInfo().pageSize());
      verify(amsRepository).findByStudent(any(Student.class), eq(DEFAULT_PAGE_CRITERIA));
    }
  }

  @Test
  void shouldHandlePagination() {
    BddLogger.given("the findUserAmsByStudentProgress method from AMSServiceImpl");
    Integer page = 1;
    Integer size = 5;
    List<AMS> amsList = AMSFixture.create().withCount(5);
    PagedResult<AMS> expectedResult = new PagedResult<>(amsList, new PageInfo(page, size, 15));

    when(amsRepository.findByStudent(any(Student.class), eq(new PageCriteria(page, size))))
        .thenReturn(expectedResult);
    when(studentProgressRepository.findById(eq(studentProgressId)))
        .thenReturn(Optional.of(studentProgress));
    when(skillLevelProgressRepository.linkedWith(any())).thenReturn(Collections.emptyList());
    when(traceRepository.linkedWith(any(AMS.class))).thenReturn(Collections.emptyList());
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    try (MockedStatic<RequestContext> mockedRequestContext = mockStatic(RequestContext.class)) {
      mockedRequestContext
          .when(RequestContext::get)
          .thenReturn(new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));

      BddLogger.when("calling the method with an user with a large amount of AMS");
      PagedResult<AmsView> result =
          amsService.findUserAmsByStudentProgress(studentProgressId, new PageCriteria(page, size));

      BddLogger.then("it should return paged AMS");
      assertNotNull(result);
      assertEquals(15, result.pageInfo().totalElements());
      assertEquals(5, result.content().size());
      assertEquals(page, result.pageInfo().page());
      assertEquals(size, result.pageInfo().pageSize());
      verify(amsRepository).findByStudent(any(Student.class), eq(new PageCriteria(page, size)));
    }
  }

  @Test
  void shouldUseDefaultValuesWhenPaginationParametersAreNull() {
    BddLogger.given("the findUserAmsByStudentProgress method from AMSServiceImpl");
    List<AMS> amsList = AMSFixture.create().withCount(3);
    PagedResult<AMS> expectedResult = new PagedResult<>(amsList, new PageInfo(0, 8, 3));

    when(amsRepository.findByStudent(any(Student.class), eq(new PageCriteria(0, 8))))
        .thenReturn(expectedResult);
    when(studentProgressRepository.findById(eq(studentProgressId)))
        .thenReturn(Optional.of(studentProgress));
    when(skillLevelProgressRepository.linkedWith(any())).thenReturn(Collections.emptyList());
    when(traceRepository.linkedWith(any(AMS.class))).thenReturn(Collections.emptyList());
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    try (MockedStatic<RequestContext> mockedRequestContext = mockStatic(RequestContext.class)) {
      mockedRequestContext
          .when(RequestContext::get)
          .thenReturn(new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));

      BddLogger.when("calling the method with null pagination parameters");
      PagedResult<AmsView> result =
          amsService.findUserAmsByStudentProgress(studentProgressId, new PageCriteria(null, null));

      BddLogger.then("it should return paged AMS with default pagination values");
      assertNotNull(result);
      assertEquals(3, result.pageInfo().totalElements());
      assertEquals(3, result.content().size());
      assertEquals(0, result.pageInfo().page());
      assertEquals(8, result.pageInfo().pageSize());
      verify(amsRepository).findByStudent(any(Student.class), eq(new PageCriteria(0, 8)));
    }
  }
}
