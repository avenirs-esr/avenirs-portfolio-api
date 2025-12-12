package fr.avenirsesr.portfolio.student.progress.declared.program.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.exception.DeclaredProgramNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.output.DeclaredProgramRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredProgramServiceImplTest {

  @Mock private StudentRepository studentRepository;
  @Mock private DeclaredProgramRepository declaredProgramRepository;
  @Mock private LoggedInUserService loggedInUserService;

  private DeclaredProgramServiceImpl declaredProgramService;

  @BeforeEach
  void setup() {
    declaredProgramService =
        new DeclaredProgramServiceImpl(
            studentRepository, declaredProgramRepository, loggedInUserService);
  }

  @Nested
  class GivenDeclaredProgramService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a declared program service");
    }

    @Nested
    class WhenCreateIsCalledWithStudentId {

      private UUID studentId;
      private EProgramStatus status;
      private String title;
      private String description;
      private String organization;
      private String result;
      private String sourceOfInformation;
      private String link;
      private LocalDate startDate;
      private LocalDate endDate;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("create(UUID studentId, ...) is called");
        studentId = UUID.randomUUID();
        status = EProgramStatus.IN_PROGRESS;
        title = "My program";
        description = "Description";
        organization = "Organization";
        result = "Result";
        sourceOfInformation = "Source";
        link = "https://example.com";
        startDate = LocalDate.now().minusMonths(1);
        endDate = LocalDate.now().plusMonths(1);
      }

      @Nested
      class AndStudentExistsInRepository {

        private Student student;
        private DeclaredProgram savedProgram;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("student exists in repository for given id");
          student = mock(Student.class);
          when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

          savedProgram = mock(DeclaredProgram.class);
          when(declaredProgramRepository.save(any(DeclaredProgram.class))).thenReturn(savedProgram);
        }

        @Test
        void thenItShouldCreateDeclaredProgramForThatStudent() {
          BddLogger.then("it should create and save a declared program for that student");

          DeclaredProgram declaredProgram =
              declaredProgramService.create(
                  studentId,
                  status,
                  title,
                  description,
                  organization,
                  result,
                  sourceOfInformation,
                  link,
                  startDate,
                  endDate);

          assertNotNull(declaredProgram);
          assertEquals(savedProgram, declaredProgram);
          verify(studentRepository).findById(studentId);
          verify(loggedInUserService, never()).getLoggedInStudent();
          verify(declaredProgramRepository).save(any(DeclaredProgram.class));
        }
      }

      @Nested
      class AndStudentDoesNotExistInRepository {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("no student exists in repository for given id");
          when(studentRepository.findById(studentId)).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldThrowUserNotAuthorizedException() {
          BddLogger.then("it should throw UserNotAuthorizedException");

          assertThrows(
              UserNotAuthorizedException.class,
              () ->
                  declaredProgramService.create(
                      studentId,
                      status,
                      title,
                      description,
                      organization,
                      result,
                      sourceOfInformation,
                      link,
                      startDate,
                      endDate));

          verify(studentRepository).findById(studentId);
          verify(declaredProgramRepository, never()).save(any());
          verify(loggedInUserService, never()).getLoggedInStudent();
        }
      }
    }

    @Nested
    class WhenCreateIsCalledWithLoggedInStudentOnly {

      private EProgramStatus status;
      private String title;
      private String description;
      private String organization;
      private String result;
      private String sourceOfInformation;
      private String link;
      private LocalDate startDate;
      private LocalDate endDate;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("create(...) without studentId is called");
        status = EProgramStatus.IN_PROGRESS;
        title = "My program";
        description = "Description";
        organization = "Organization";
        result = "Result";
        sourceOfInformation = "Source";
        link = "https://example.com";
        startDate = LocalDate.now().minusMonths(1);
        endDate = LocalDate.now();
      }

      @Nested
      class AndLoggedInStudentIsAvailable {

        private Student loggedStudent;
        private DeclaredProgram savedProgram;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a logged in student is available");
          loggedStudent = mock(Student.class);
          savedProgram = mock(DeclaredProgram.class);

          when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedStudent);
          when(declaredProgramRepository.save(any(DeclaredProgram.class))).thenReturn(savedProgram);
        }

        @Test
        void thenItShouldCreateNotStartedDeclaredProgramForLoggedInStudent() {
          BddLogger.then(
              "it should create and save a not started declared program for the logged in student");

          LocalDate futureStartDate = LocalDate.now().plusMonths(1);
          LocalDate futureEndDate = futureStartDate.plusMonths(3);

          DeclaredProgram declaredProgram =
              declaredProgramService.create(
                  title,
                  description,
                  organization,
                  result,
                  sourceOfInformation,
                  link,
                  futureStartDate,
                  futureEndDate);

          assertNotNull(declaredProgram);
          assertEquals(savedProgram, declaredProgram);
          verify(loggedInUserService).getLoggedInStudent();
          verify(studentRepository, never()).findById(any());
          verify(declaredProgramRepository).save(any(DeclaredProgram.class));
        }

        @Test
        void thenItShouldCreateInProgressDeclaredProgramForLoggedInStudent() {
          BddLogger.then(
              "it should create and save a in progress declared program for the logged in student");

          DeclaredProgram declaredProgram =
              declaredProgramService.create(
                  title,
                  description,
                  organization,
                  result,
                  sourceOfInformation,
                  link,
                  startDate,
                  endDate);

          assertNotNull(declaredProgram);
          assertEquals(savedProgram, declaredProgram);
          verify(loggedInUserService).getLoggedInStudent();
          verify(studentRepository, never()).findById(any());
          verify(declaredProgramRepository).save(any(DeclaredProgram.class));
        }

        @Test
        void thenItShouldCreateCompletedDeclaredProgramForLoggedInStudent() {
          BddLogger.then(
              "it should create and save a completed declared program for the logged in student");

          LocalDate pastStartDate = LocalDate.now().minusMonths(3);
          LocalDate pastEndDate = LocalDate.now().minusMonths(1);

          DeclaredProgram declaredProgram =
              declaredProgramService.create(
                  title,
                  description,
                  organization,
                  result,
                  sourceOfInformation,
                  link,
                  pastStartDate,
                  pastEndDate);

          assertNotNull(declaredProgram);
          assertEquals(savedProgram, declaredProgram);
          verify(loggedInUserService).getLoggedInStudent();
          verify(studentRepository, never()).findById(any());
          verify(declaredProgramRepository).save(any(DeclaredProgram.class));
        }
      }

      @Nested
      class AndNoLoggedInStudentIsAvailable {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("no logged in student is available");
          when(loggedInUserService.getLoggedInStudent())
              .thenThrow(new RuntimeException("no logged user"));
        }

        @Test
        void thenItShouldPropagateException() {
          BddLogger.then("it should propagate the exception from LoggedInUserService");

          assertThrows(
              RuntimeException.class,
              () ->
                  declaredProgramService.create(
                      title,
                      description,
                      organization,
                      result,
                      sourceOfInformation,
                      link,
                      startDate,
                      endDate));

          verify(studentRepository, never()).findById(any());
          verify(declaredProgramRepository, never()).save(any());
        }
      }
    }

    @Nested
    class WhenGetByIdIsCalled {

      private UUID declaredProgramId;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getById(UUID declaredProgramId) is called");
        declaredProgramId = UUID.randomUUID();
      }

      @Nested
      class AndDeclaredProgramExists {

        private DeclaredProgram declaredProgram;
        private Student loggedStudent;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("declared program exists in repository");
          declaredProgram = mock(DeclaredProgram.class);
          when(declaredProgramRepository.findById(declaredProgramId))
              .thenReturn(Optional.of(declaredProgram));

          loggedStudent = mock(Student.class);
          when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedStudent);
        }

        @Nested
        class AndItBelongsToLoggedInStudent {

          @BeforeEach
          void setupAnd2() {
            BddLogger.and("declared program belongs to logged in student");
            when(declaredProgram.getStudent()).thenReturn(loggedStudent);
          }

          @Test
          void thenItShouldReturnDeclaredProgram() {
            BddLogger.then("it should return the declared program");

            DeclaredProgram result = declaredProgramService.getById(declaredProgramId);

            assertNotNull(result);
            assertEquals(declaredProgram, result);

            verify(declaredProgramRepository).findById(declaredProgramId);
            verify(loggedInUserService).getLoggedInStudent();
            verify(declaredProgram).getStudent();
          }
        }

        @Nested
        class AndItDoesNotBelongToLoggedInStudent {

          private Student otherStudent;

          @BeforeEach
          void setupAnd2() {
            BddLogger.and("declared program does not belong to logged in student");
            otherStudent = mock(Student.class);
            when(declaredProgram.getStudent()).thenReturn(otherStudent);
          }

          @Test
          void thenItShouldThrowUserNotAuthorizedException() {
            BddLogger.then("it should throw UserNotAuthorizedException");

            assertThrows(
                UserNotAuthorizedException.class,
                () -> declaredProgramService.getById(declaredProgramId));

            verify(declaredProgramRepository).findById(declaredProgramId);
            verify(loggedInUserService).getLoggedInStudent();
            verify(declaredProgram).getStudent();
          }
        }
      }

      @Nested
      class AndDeclaredProgramDoesNotExist {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("declared program does not exist in repository");
          when(declaredProgramRepository.findById(declaredProgramId)).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldThrowDeclaredProgramNotFoundException() {
          BddLogger.then("it should throw DeclaredProgramNotFoundException");

          assertThrows(
              DeclaredProgramNotFoundException.class,
              () -> declaredProgramService.getById(declaredProgramId));

          verify(declaredProgramRepository).findById(declaredProgramId);
          verify(loggedInUserService, never()).getLoggedInStudent();
          verifyNoMoreInteractions(declaredProgramRepository);
        }
      }
    }
  }
}
