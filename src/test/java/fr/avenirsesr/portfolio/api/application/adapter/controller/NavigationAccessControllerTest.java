package fr.avenirsesr.portfolio.api.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fixtures.UserFixture;
import fr.avenirsesr.portfolio.api.application.adapter.dto.NavigationAccessDTO;
import fr.avenirsesr.portfolio.api.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class NavigationAccessControllerTest {

  @Mock private UserRepository userRepository;
  @Mock private InstitutionService institutionService;
  @Mock private ProgramProgressService programProgressService;

  @InjectMocks private NavigationAccessController controller;

  private UUID userId;
  private Principal principal;
  private User user;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = UserFixture.createStudent().withId(userId).toModel();
    principal = () -> userId.toString();
  }

  @Test
  void shouldReturnNavigationAccessForStudent() {
    // Given
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(institutionService.isNavigationEnabledFor(any(Student.class), eq(EPortfolioType.APC)))
        .thenReturn(true);
    when(institutionService.isNavigationEnabledFor(
            any(Student.class), eq(EPortfolioType.LIFE_PROJECT)))
        .thenReturn(false);
    when(programProgressService.isStudentFollowingAPCProgram(any(Student.class))).thenReturn(true);

    // When
    ResponseEntity<NavigationAccessDTO> response = controller.getStudentNavigationAccess(principal);

    // Then
    assertEquals(200, response.getStatusCode().value());

    NavigationAccessDTO body = response.getBody();
    assertNotNull(body);

    assertTrue(body.APC().enabledByInstitution());
    assertFalse(body.LIFE_PROJECT().enabledByInstitution());

    verify(userRepository).findById(userId);
    verify(institutionService).isNavigationEnabledFor(any(Student.class), eq(EPortfolioType.APC));
    verify(institutionService)
        .isNavigationEnabledFor(any(Student.class), eq(EPortfolioType.LIFE_PROJECT));
    verify(programProgressService).isStudentFollowingAPCProgram(any(Student.class));
  }

  @Test
  void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
    // Given
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Then
    assertThrows(
        UserNotFoundException.class, () -> controller.getStudentNavigationAccess(principal));

    verify(userRepository).findById(userId);
    verifyNoMoreInteractions(institutionService, programProgressService);
  }

  @Test
  void shouldThrowUserIsNotStudentExceptionWhenUserIsNotStudent() {
    // Given
    user.setStudent(false);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // Then
    assertThrows(
        UserIsNotStudentException.class, () -> controller.getStudentNavigationAccess(principal));

    verify(userRepository).findById(userId);
    verifyNoMoreInteractions(institutionService, programProgressService);
  }
}
