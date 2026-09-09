package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.kit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.kit.domain.model.KitMediaEntry;
import fr.avenirsesr.portfolio.student.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class TraceKitDownloadAdapterTest {

  private static final TraceFilter VALORIZED_FILTER = new TraceFilter(null, null, null, true);
  private static final String REQUIRED_AUTHORITY = "trace:list:own";

  @Mock private LoggedInUserService loggedInUserService;
  @Mock private TraceRepository traceRepository;

  private TraceKitDownloadAdapter adapter;
  private MockedStatic<SecurityContextHolder> securityContextHolderMock;
  private SecurityContext securityContext;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    adapter = new TraceKitDownloadAdapter(loggedInUserService, traceRepository);

    securityContext = mock(SecurityContext.class);
    securityContextHolderMock = mockStatic(SecurityContextHolder.class);
    securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
  }

  @AfterEach
  void tearDown() {
    securityContextHolderMock.close();
  }

  @Nested
  class GivenATraceKitDownloadAdapter {

    Student student;

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a TraceKitDownloadAdapter");
      student = mock(Student.class);
    }

    @Nested
    class WhenGettingMedia {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting the media to include in the kit");
      }

      @Nested
      class AndTheUserHasTheRequiredAuthority {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the logged-in user has the trace:list:own authority");
          grantAuthority(REQUIRED_AUTHORITY);
          when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
        }

        @Test
        void thenItShouldQueryValorizedTracesOfTheLoggedInStudent() {
          BddLogger.then("it should query the repository with the exact valorized-only filter");

          when(traceRepository.findAll(student, VALORIZED_FILTER)).thenReturn(List.of());

          adapter.getMedia();

          verify(traceRepository).findAll(student, VALORIZED_FILTER);
        }

        @Test
        void thenItShouldMapATraceWithAnAttachmentToAKitMediaEntryUnderTraces() {
          BddLogger.then("the trace should be mapped to a KitMediaEntry under traces/<title>");

          File attachment = mock(File.class);
          Trace trace = mock(Trace.class);
          when(trace.getTitle()).thenReturn("Voyage à Berlin");
          when(trace.getAttachment()).thenReturn(Optional.of(attachment));
          when(traceRepository.findAll(student, VALORIZED_FILTER)).thenReturn(List.of(trace));

          List<KitMediaEntry> result = adapter.getMedia();

          assertEquals(1, result.size());
          assertEquals(attachment, result.getFirst().file());
          assertEquals(List.of("traces", "Voyage à Berlin"), result.getFirst().folderPath());
        }

        @Test
        void thenItShouldSkipTracesWithoutAnAttachment() {
          BddLogger.then("traces without an attachment should not produce any entry");

          Trace trace = mock(Trace.class);
          when(trace.getAttachment()).thenReturn(Optional.empty());
          when(traceRepository.findAll(student, VALORIZED_FILTER)).thenReturn(List.of(trace));

          List<KitMediaEntry> result = adapter.getMedia();

          assertTrue(result.isEmpty());
        }

        @Test
        void thenItShouldReturnEmptyListWhenNoValorizedTracesExist() {
          BddLogger.then("it should return an empty list");

          when(traceRepository.findAll(student, VALORIZED_FILTER)).thenReturn(List.of());

          List<KitMediaEntry> result = adapter.getMedia();

          assertTrue(result.isEmpty());
        }

        @Test
        void thenItShouldMapEachTraceIndependentlyWhenMultipleTracesHaveAttachments() {
          BddLogger.then("each trace with an attachment should produce its own entry");

          File attachmentA = mock(File.class);
          File attachmentB = mock(File.class);
          Trace traceA = mock(Trace.class);
          Trace traceB = mock(Trace.class);
          when(traceA.getTitle()).thenReturn("Trace A");
          when(traceA.getAttachment()).thenReturn(Optional.of(attachmentA));
          when(traceB.getTitle()).thenReturn("Trace B");
          when(traceB.getAttachment()).thenReturn(Optional.of(attachmentB));
          when(traceRepository.findAll(student, VALORIZED_FILTER))
              .thenReturn(List.of(traceA, traceB));

          List<KitMediaEntry> result = adapter.getMedia();

          assertEquals(2, result.size());
          assertTrue(result.stream().anyMatch(e -> e.file() == attachmentA));
          assertTrue(result.stream().anyMatch(e -> e.file() == attachmentB));
        }
      }

      @Nested
      class AndTheUserLacksTheRequiredAuthority {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the logged-in user does not have the trace:list:own authority");
          grantNoAuthority();
        }

        @Test
        void thenItShouldReturnEmptyListWithoutQueryingAnything() {
          BddLogger.then(
              "it should return an empty list without resolving the student or querying the"
                  + " repository");

          List<KitMediaEntry> result = adapter.getMedia();

          assertTrue(result.isEmpty());
          verifyNoInteractions(loggedInUserService);
          verifyNoInteractions(traceRepository);
        }
      }

      @Nested
      class AndNoAuthenticationIsPresent {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the security context holds no authentication at all");
          when(securityContext.getAuthentication()).thenReturn(null);
        }

        @Test
        void thenItShouldReturnEmptyListWithoutThrowing() {
          BddLogger.then("it should return an empty list instead of throwing");

          List<KitMediaEntry> result = adapter.getMedia();

          assertTrue(result.isEmpty());
          verifyNoInteractions(loggedInUserService);
        }
      }
    }
  }

  private void grantAuthority(String authority) {
    Authentication authentication = mock(Authentication.class);
    doReturn(List.of(new SimpleGrantedAuthority(authority))).when(authentication).getAuthorities();
    when(securityContext.getAuthentication()).thenReturn(authentication);
  }

  private void grantNoAuthority() {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getAuthorities()).thenReturn(List.of());
    when(securityContext.getAuthentication()).thenReturn(authentication);
  }
}
