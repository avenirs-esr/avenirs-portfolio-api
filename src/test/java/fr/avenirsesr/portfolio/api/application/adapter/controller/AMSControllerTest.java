package fr.avenirsesr.portfolio.api.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import fixtures.AMSFixture;
import fixtures.UserFixture;
import fr.avenirsesr.portfolio.api.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.api.application.adapter.response.AmsViewResponse;
import fr.avenirsesr.portfolio.api.application.adapter.util.UserUtil;
import fr.avenirsesr.portfolio.api.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.PagedResult;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.input.AMSService;
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
  private Student student;
  private Principal principal;
  private int defaultPage;
  private int defaultSize;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    User user = UserFixture.createStudent().withId(userId).toModel();
    student = user.toStudent();
    principal = () -> userId.toString();
    defaultPage = 0;
    defaultSize = 10;
  }

  @Test
  void shouldReturnAmsViewForUser() {
    // Given
    List<AMS> amsList = AMSFixture.create().withCount(3);
    PagedResult<AMS> pagedResult = new PagedResult<>(amsList, 3, 1);

    when(userUtil.getStudent(principal)).thenReturn(student);
    when(amsService.findUserAmsWithPagination(student, defaultPage, defaultSize))
        .thenReturn(pagedResult);

    // When
    ResponseEntity<AmsViewResponse> response =
        controller.getAmsView(principal, defaultPage, defaultSize);

    // Then
    assertEquals(200, response.getStatusCode().value());

    AmsViewResponse body = response.getBody();
    assertNotNull(body);
    assertEquals(3, body.content().size());
    assertEquals(3, body.page().totalElements());
    assertEquals(1, body.page().totalPages());
    assertEquals(defaultPage, body.page().number());
    assertEquals(defaultSize, body.page().size());

    // Verify DTO conversion
    AmsViewDTO firstDto = body.content().getFirst();
    assertEquals(amsList.getFirst().getId(), firstDto.id());
    assertEquals(amsList.getFirst().getTitle(), firstDto.title());
    assertEquals(amsList.getFirst().getSkillLevels().size(), firstDto.countSkills());
    assertEquals(amsList.getFirst().getTraces().size(), firstDto.countTraces());
    assertEquals(amsList.getFirst().getStatus(), firstDto.status());

    verify(userUtil).getStudent(principal);
    verify(amsService).findUserAmsWithPagination(student, defaultPage, defaultSize);
  }

  @Test
  void shouldReturnEmptyListWhenUserHasNoAMS() {
    // Given
    PagedResult<AMS> emptyPagedResult = new PagedResult<>(new ArrayList<>(), 0, 0);

    when(userUtil.getStudent(principal)).thenReturn(student);
    when(amsService.findUserAmsWithPagination(student, defaultPage, defaultSize))
        .thenReturn(emptyPagedResult);

    // When
    ResponseEntity<AmsViewResponse> response =
        controller.getAmsView(principal, defaultPage, defaultSize);

    // Then
    assertEquals(200, response.getStatusCode().value());

    AmsViewResponse body = response.getBody();
    assertNotNull(body);
    assertEquals(0, body.content().size());
    assertEquals(0, body.page().totalElements());
    assertEquals(0, body.page().totalPages());
    assertEquals(defaultPage, body.page().number());
    assertEquals(defaultSize, body.page().size());

    verify(userUtil).getStudent(principal);
    verify(amsService).findUserAmsWithPagination(student, defaultPage, defaultSize);
  }

  @Test
  void shouldHandlePagination() {
    // Given
    int page = 1;
    int size = 5;
    List<AMS> amsList = AMSFixture.create().withCount(5);
    PagedResult<AMS> pagedResult = new PagedResult<>(amsList, 15, 3);

    when(userUtil.getStudent(principal)).thenReturn(student);
    when(amsService.findUserAmsWithPagination(student, page, size)).thenReturn(pagedResult);

    // When
    ResponseEntity<AmsViewResponse> response = controller.getAmsView(principal, page, size);

    // Then
    assertEquals(200, response.getStatusCode().value());

    AmsViewResponse body = response.getBody();
    assertNotNull(body);
    assertEquals(5, body.content().size());
    assertEquals(15, body.page().totalElements());
    assertEquals(3, body.page().totalPages());
    assertEquals(page, body.page().number());
    assertEquals(size, body.page().size());

    verify(userUtil).getStudent(principal);
    verify(amsService).findUserAmsWithPagination(student, page, size);
  }

  @Test
  void shouldPropagateUserNotFoundException() {
    // Given
    when(userUtil.getStudent(principal)).thenThrow(new UserNotFoundException());

    // Then
    assertThrows(
        UserNotFoundException.class,
        () -> controller.getAmsView(principal, defaultPage, defaultSize));

    verify(userUtil).getStudent(principal);
    verifyNoMoreInteractions(amsService);
  }

  @Test
  void shouldPropagateUserIsNotStudentException() {
    // Given
    when(userUtil.getStudent(principal)).thenThrow(new UserIsNotStudentException());

    // Then
    assertThrows(
        UserIsNotStudentException.class,
        () -> controller.getAmsView(principal, defaultPage, defaultSize));

    verify(userUtil).getStudent(principal);
    verifyNoMoreInteractions(amsService);
  }
}
