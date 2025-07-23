package fr.avenirsesr.portfolio.ams.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.ams.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.ams.application.adapter.response.AmsViewResponse;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.AMSFixture;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AMSControllerTest {

  @Mock private UserUtil userUtil;
  @Mock private AMSService amsService;

  @InjectMocks private AMSController controller;

  private UUID userId;
  private UUID programProgressId;
  private Student student;
  private Principal principal;
  private static final Integer DEFAULT_PAGE = 1;
  private static final Integer DEFAULT_SIZE = 10;
  private static final PageCriteria DEFAULT_PAGE_CRITERIA =
      new PageCriteria(DEFAULT_PAGE, DEFAULT_SIZE);

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    programProgressId = UUID.randomUUID();
    User user = UserFixture.createStudent().withId(userId).toModel();
    student = user.toStudent();
    principal = () -> userId.toString();
  }

  @Test
  void shouldReturnAmsViewForUser() {
    // Given
    List<AMS> amsList = AMSFixture.create().withCount(3);
    PagedResult<AMS> pagedResult =
        new PagedResult<>(amsList, new PageInfo(DEFAULT_PAGE, DEFAULT_SIZE, 3));

    when(userUtil.getStudent(principal)).thenReturn(student);
    when(amsService.findUserAmsByProgramProgress(student, programProgressId, DEFAULT_PAGE_CRITERIA))
        .thenReturn(pagedResult);

    // When
    ResponseEntity<AmsViewResponse> response =
        controller.getAmsView(principal, programProgressId, DEFAULT_PAGE, DEFAULT_SIZE);

    // Then
    assertEquals(200, response.getStatusCode().value());

    AmsViewResponse body = response.getBody();
    assertNotNull(body);
    assertEquals(3, body.data().size());
    assertEquals(3, body.page().totalElements());
    assertEquals(1, body.page().totalPages());
    assertEquals(DEFAULT_PAGE, body.page().page());
    assertEquals(DEFAULT_SIZE, body.page().pageSize());

    // Verify DTO conversion
    AmsViewDTO firstDto = body.data().getFirst();
    assertEquals(amsList.getFirst().getId(), firstDto.id());
    assertEquals(amsList.getFirst().getTitle(), firstDto.title());
    assertEquals(amsList.getFirst().getSkillLevels().size(), firstDto.countSkills());
    assertEquals(amsList.getFirst().getTraces().size(), firstDto.countTraces());
    assertEquals(amsList.getFirst().getStatus(), firstDto.status());

    verify(userUtil).getStudent(principal);
    verify(amsService)
        .findUserAmsByProgramProgress(student, programProgressId, DEFAULT_PAGE_CRITERIA);
  }

  @Test
  void shouldReturnEmptyListWhenUserHasNoAMS() {
    // Given
    PagedResult<AMS> emptyPagedResult =
        new PagedResult<>(new ArrayList<>(), new PageInfo(DEFAULT_PAGE, DEFAULT_SIZE, 0));

    when(userUtil.getStudent(principal)).thenReturn(student);
    when(amsService.findUserAmsByProgramProgress(student, programProgressId, DEFAULT_PAGE_CRITERIA))
        .thenReturn(emptyPagedResult);

    // When
    ResponseEntity<AmsViewResponse> response =
        controller.getAmsView(principal, programProgressId, DEFAULT_PAGE, DEFAULT_SIZE);

    // Then
    assertEquals(200, response.getStatusCode().value());

    AmsViewResponse body = response.getBody();
    assertNotNull(body);
    assertEquals(0, body.data().size());
    assertEquals(0, body.page().totalElements());
    assertEquals(0, body.page().totalPages());
    assertEquals(DEFAULT_PAGE, body.page().page());
    assertEquals(DEFAULT_SIZE, body.page().pageSize());

    verify(userUtil).getStudent(principal);
    verify(amsService)
        .findUserAmsByProgramProgress(student, programProgressId, DEFAULT_PAGE_CRITERIA);
  }

  @Test
  void shouldHandlePagination() {
    // Given
    Integer page = 1;
    Integer size = 5;
    List<AMS> amsList = AMSFixture.create().withCount(5);
    PagedResult<AMS> pagedResult = new PagedResult<>(amsList, new PageInfo(page, size, 15));

    when(userUtil.getStudent(principal)).thenReturn(student);
    when(amsService.findUserAmsByProgramProgress(
            student, programProgressId, new PageCriteria(page, size)))
        .thenReturn(pagedResult);

    // When
    ResponseEntity<AmsViewResponse> response =
        controller.getAmsView(principal, programProgressId, page, size);

    // Then
    assertEquals(200, response.getStatusCode().value());

    AmsViewResponse body = response.getBody();
    assertNotNull(body);
    assertEquals(5, body.data().size());
    assertEquals(15, body.page().totalElements());
    assertEquals(3, body.page().totalPages());
    assertEquals(page, body.page().page());
    assertEquals(size, body.page().pageSize());

    verify(userUtil).getStudent(principal);
    verify(amsService)
        .findUserAmsByProgramProgress(student, programProgressId, new PageCriteria(page, size));
  }

  @Test
  void shouldPropagateUserNotFoundException() {
    // Given
    when(userUtil.getStudent(principal)).thenThrow(new UserNotFoundException());

    // When & Then
    assertThrows(
        UserNotFoundException.class,
        () -> controller.getAmsView(principal, programProgressId, DEFAULT_PAGE, DEFAULT_SIZE));

    verify(userUtil).getStudent(principal);
    verifyNoMoreInteractions(amsService);
  }

  @Test
  void shouldPropagateUserIsNotStudentException() {
    // Given
    when(userUtil.getStudent(principal)).thenThrow(new UserIsNotStudentException());

    // When & Then
    assertThrows(
        UserIsNotStudentException.class,
        () -> controller.getAmsView(principal, programProgressId, DEFAULT_PAGE, DEFAULT_SIZE));

    verify(userUtil).getStudent(principal);
    verifyNoMoreInteractions(amsService);
  }
}
