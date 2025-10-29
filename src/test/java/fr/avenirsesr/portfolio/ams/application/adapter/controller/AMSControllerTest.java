package fr.avenirsesr.portfolio.ams.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.ams.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.ams.domain.dto.AmsView;
import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.AMSFixture;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AMSControllerTest {

  @Mock private AMSService amsService;

  @InjectMocks private AMSController controller;

  private UUID userId;
  private UUID studentProgressId;
  private Student student;
  private Principal principal;
  private static final Integer DEFAULT_PAGE = 1;
  private static final Integer DEFAULT_SIZE = 10;
  private static final PageCriteria DEFAULT_PAGE_CRITERIA =
      new PageCriteria(DEFAULT_PAGE, DEFAULT_SIZE);

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    studentProgressId = UUID.randomUUID();
    student = StudentFixture.create().withId(userId).toModel();
    principal = () -> userId.toString();
  }

  @Test
  void shouldReturnAmsViewForUser() {
    BddLogger.given("an user with AMS");
    List<AmsView> amsList =
        AMSFixture.create().withCount(3).stream().map(ams -> new AmsView(ams, 1, 2)).toList();
    PagedResult<AmsView> pagedResult =
        new PagedResult<>(amsList, new PageInfo(DEFAULT_PAGE, DEFAULT_SIZE, 3));

    when(amsService.findUserAmsByStudentProgress(studentProgressId, DEFAULT_PAGE_CRITERIA))
        .thenReturn(pagedResult);

    BddLogger.when("getting the AMS view");
    ResponseEntity<PagedResponse<AmsViewDTO>> response =
        controller.getAmsView(principal, studentProgressId, DEFAULT_PAGE, DEFAULT_SIZE);

    BddLogger.then("it should return the AMS view");
    assertEquals(200, response.getStatusCode().value());

    PagedResponse<AmsViewDTO> body = response.getBody();
    assertNotNull(body);
    assertEquals(3, body.data().size());
    assertEquals(3, body.page().totalElements());
    assertEquals(1, body.page().totalPages());
    assertEquals(DEFAULT_PAGE, body.page().page());
    assertEquals(DEFAULT_SIZE, body.page().pageSize());

    // Verify DTO conversion
    AmsViewDTO firstDto = body.data().getFirst();
    assertEquals(amsList.getFirst().ams().getId(), firstDto.id());
    assertEquals(amsList.getFirst().ams().getTitle(), firstDto.title());
    assertEquals(amsList.getFirst().skillLevelCount(), firstDto.countSkills());
    assertEquals(amsList.getFirst().traceCount(), firstDto.countTraces());
    assertEquals(amsList.getFirst().ams().getStatus(), firstDto.status());

    verify(amsService).findUserAmsByStudentProgress(studentProgressId, DEFAULT_PAGE_CRITERIA);
  }

  @Test
  void shouldReturnEmptyListWhenUserHasNoAMS() {
    BddLogger.given("an user without AMS");
    PagedResult<AmsView> emptyPagedResult =
        new PagedResult<>(new ArrayList<>(), new PageInfo(DEFAULT_PAGE, DEFAULT_SIZE, 0));

    when(amsService.findUserAmsByStudentProgress(studentProgressId, DEFAULT_PAGE_CRITERIA))
        .thenReturn(emptyPagedResult);

    BddLogger.when("getting the AMS view");
    ResponseEntity<PagedResponse<AmsViewDTO>> response =
        controller.getAmsView(principal, studentProgressId, DEFAULT_PAGE, DEFAULT_SIZE);

    BddLogger.then("it should return and empty AMS view");
    assertEquals(200, response.getStatusCode().value());

    PagedResponse<AmsViewDTO> body = response.getBody();
    assertNotNull(body);
    assertEquals(0, body.data().size());
    assertEquals(0, body.page().totalElements());
    assertEquals(0, body.page().totalPages());
    assertEquals(DEFAULT_PAGE, body.page().page());
    assertEquals(DEFAULT_SIZE, body.page().pageSize());

    verify(amsService).findUserAmsByStudentProgress(studentProgressId, DEFAULT_PAGE_CRITERIA);
  }

  @Test
  void shouldHandlePagination() {
    BddLogger.given("an user with a large amount of AMS");
    Integer page = 1;
    Integer size = 5;
    List<AmsView> amsList =
        AMSFixture.create().withCount(5).stream().map(ams -> new AmsView(ams, 1, 2)).toList();
    PagedResult<AmsView> pagedResult = new PagedResult<>(amsList, new PageInfo(page, size, 15));

    when(amsService.findUserAmsByStudentProgress(studentProgressId, new PageCriteria(page, size)))
        .thenReturn(pagedResult);

    BddLogger.when("getting the AMS view");
    ResponseEntity<PagedResponse<AmsViewDTO>> response =
        controller.getAmsView(principal, studentProgressId, page, size);

    BddLogger.then("it should return a paged AMS view");
    assertEquals(200, response.getStatusCode().value());

    PagedResponse<AmsViewDTO> body = response.getBody();
    assertNotNull(body);
    assertEquals(5, body.data().size());
    assertEquals(15, body.page().totalElements());
    assertEquals(3, body.page().totalPages());
    assertEquals(page, body.page().page());
    assertEquals(size, body.page().pageSize());

    verify(amsService)
        .findUserAmsByStudentProgress(studentProgressId, new PageCriteria(page, size));
  }

  @Test
  void shouldPropagateUserNotFoundException() {
    BddLogger.given("a not found user");
    when(amsService.findUserAmsByStudentProgress(any(), any()))
        .thenThrow(new UserNotFoundException());

    BddLogger.when("getting the AMS view");
    BddLogger.then("it should throw an UserNotFoundException");
    assertThrows(
        UserNotFoundException.class,
        () -> controller.getAmsView(principal, studentProgressId, DEFAULT_PAGE, DEFAULT_SIZE));

    verify(amsService).findUserAmsByStudentProgress(any(), any());
  }

  @Test
  void shouldPropagateUserIsNotStudentException() {
    BddLogger.given("a non student user");
    when(amsService.findUserAmsByStudentProgress(any(), any()))
        .thenThrow(new UserIsNotStudentException());

    BddLogger.when("getting the AMS view");
    BddLogger.then("it should throw an UserIsNotStudentException");
    assertThrows(
        UserIsNotStudentException.class,
        () -> controller.getAmsView(principal, studentProgressId, DEFAULT_PAGE, DEFAULT_SIZE));

    verify(amsService).findUserAmsByStudentProgress(any(), any());
  }
}
