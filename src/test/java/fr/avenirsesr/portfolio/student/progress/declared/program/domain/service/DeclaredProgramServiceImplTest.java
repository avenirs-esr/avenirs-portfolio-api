package fr.avenirsesr.portfolio.student.progress.declared.program.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.exception.DeclaredProgramNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.output.DeclaredProgramRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredProgramServiceImplTest {

  @Mock private StudentService studentService;
  @Mock private DeclaredProgramRepository declaredProgramRepository;
  @Mock private LoggedInUserService loggedInUserService;

  private DeclaredProgramServiceImpl declaredProgramService;

  @BeforeEach
  void setup() {
    declaredProgramService =
        new DeclaredProgramServiceImpl(
            studentService, declaredProgramRepository, loggedInUserService);
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
          when(studentService.getStudentById(studentId)).thenReturn(student);

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
                  startDate,
                  endDate);

          assertNotNull(declaredProgram);
          assertEquals(savedProgram, declaredProgram);
          verify(studentService).getStudentById(studentId);
          verify(loggedInUserService, never()).getLoggedInStudent();
          verify(declaredProgramRepository).save(any(DeclaredProgram.class));
        }
      }

      @Nested
      class AndStudentDoesNotExistInRepository {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("no student exists in repository for given id");
          when(studentService.getStudentById(studentId)).thenThrow(new UserIsNotStudentException());
        }

        @Test
        void thenItShouldThrowUUserIsNotStudentException() {
          BddLogger.then("it should throw UserIsNotStudentException");

          assertThrows(
              UserIsNotStudentException.class,
              () ->
                  declaredProgramService.create(
                      studentId,
                      status,
                      title,
                      description,
                      organization,
                      result,
                      sourceOfInformation,
                      startDate,
                      endDate));

          verify(studentService).getStudentById(studentId);
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
                  futureStartDate,
                  futureEndDate);

          assertNotNull(declaredProgram);
          assertEquals(savedProgram, declaredProgram);
          verify(loggedInUserService).getLoggedInStudent();
          verify(studentService, never()).getStudentById(any());
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
                  startDate,
                  endDate);

          assertNotNull(declaredProgram);
          assertEquals(savedProgram, declaredProgram);
          verify(loggedInUserService).getLoggedInStudent();
          verify(studentService, never()).getStudentById(any());
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
                  pastStartDate,
                  pastEndDate);

          assertNotNull(declaredProgram);
          assertEquals(savedProgram, declaredProgram);
          verify(loggedInUserService).getLoggedInStudent();
          verify(studentService, never()).getStudentById(any());
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
                      startDate,
                      endDate));

          verify(studentService, never()).getStudentById(any());
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

    @Nested
    class WhenGetDeclaredProgramsIsCalled {
      private List<DeclaredProgram> declaredProgramList;
      private Student loggedStudent;
      private int nbElements = 5;
      private PageCriteria pageCriteria = new PageCriteria(0, 8);
      private SortCriteria sortByDate = new SortCriteria(ESortField.DATE, ESortOrder.DESC);
      private SortCriteria sortByName = new SortCriteria(ESortField.NAME, ESortOrder.ASC);
      private PageInfo pageInfo = new PageInfo(0, 8, nbElements);

      @BeforeEach
      void setup() {
        declaredProgramList =
            Stream.generate(() -> mock(DeclaredProgram.class))
                .limit(nbElements)
                .collect(Collectors.toList());
        loggedStudent = mock(Student.class);
        when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedStudent);
      }

      @Test
      void thenItShouldReturnDeclaredPrograms() {
        BddLogger.when("getDeclaredPrograms(PageCriteria pageCriteria, null) is called");
        when(declaredProgramRepository.findAllByStudent(
                loggedStudent, pageCriteria, null, sortByDate, sortByName))
            .thenReturn(new PagedResult<>(declaredProgramList, pageInfo));

        PagedResult<DeclaredProgram> result =
            declaredProgramService.getDeclaredPrograms(pageCriteria, null);

        assertNotNull(result);

        List<DeclaredProgram> resultContent = result.content();
        PageInfo resultPageInfo = result.pageInfo();

        assertEquals(declaredProgramList.size(), resultContent.size());

        for (int i = 0; i < nbElements; i++) {
          assertEquals(declaredProgramList.get(i).getId(), resultContent.get(i).getId());
        }

        assertEquals(pageInfo.page(), resultPageInfo.page());
        assertEquals(pageInfo.pageSize(), resultPageInfo.pageSize());
        assertEquals(pageInfo.totalElements(), resultPageInfo.totalElements());

        verify(loggedInUserService).getLoggedInStudent();
        verify(declaredProgramRepository)
            .findAllByStudent(loggedStudent, pageCriteria, null, sortByDate, sortByName);
      }

      @Test
      void thenItShouldDelegateIsValorizedFilterToRepository() {
        BddLogger.when(
            "getDeclaredPrograms(PageCriteria pageCriteria, Boolean isValorized) is called with"
                + " isValorized=true");
        when(declaredProgramRepository.findAllByStudent(
                loggedStudent, pageCriteria, true, sortByDate, sortByName))
            .thenReturn(new PagedResult<>(declaredProgramList, pageInfo));

        PagedResult<DeclaredProgram> result =
            declaredProgramService.getDeclaredPrograms(pageCriteria, true);

        assertNotNull(result);
        verify(declaredProgramRepository)
            .findAllByStudent(loggedStudent, pageCriteria, true, sortByDate, sortByName);
      }
    }

    @Nested
    class WhenUpdateIsCalled {

      private UUID declaredProgramId;

      private String title;
      private String description;
      private String organization;
      private String result;
      private String sourceOfInformation;
      private LocalDate startDate;
      private LocalDate endDate;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("update(UUID declaredProgramId, ...) is called");
        declaredProgramId = UUID.randomUUID();

        title = "Updated title";
        description = "Updated description";
        organization = "Updated organization";
        result = "Updated result";
        sourceOfInformation = "Updated source";

        startDate = LocalDate.now().minusDays(10);
        endDate = LocalDate.now().plusDays(10);
      }

      @Nested
      class AndLoggedInStudentIsAvailable {

        private Student loggedStudent;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a logged in student is available");
          loggedStudent = mock(Student.class);
          when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedStudent);
        }

        @Nested
        class AndDeclaredProgramDoesNotExist {

          @BeforeEach
          void setupAnd2() {
            BddLogger.and("declared program does not exist in repository");
            when(declaredProgramRepository.findById(declaredProgramId))
                .thenReturn(Optional.empty());
          }

          @Test
          void thenItShouldThrowDeclaredProgramNotFoundException() {
            BddLogger.then("it should throw DeclaredProgramNotFoundException");

            assertThrows(
                DeclaredProgramNotFoundException.class,
                () ->
                    declaredProgramService.update(
                        declaredProgramId,
                        title,
                        description,
                        organization,
                        result,
                        sourceOfInformation,
                        startDate,
                        endDate,
                        false));

            verify(loggedInUserService).getLoggedInStudent();
            verify(declaredProgramRepository).findById(declaredProgramId);
            verify(declaredProgramRepository, never()).save(any());
          }
        }

        @Nested
        class AndDeclaredProgramExists {

          private DeclaredProgram declaredProgram;

          @BeforeEach
          void setupAnd2() {
            BddLogger.and("declared program exists in repository");
            declaredProgram = mock(DeclaredProgram.class);
            when(declaredProgramRepository.findById(declaredProgramId))
                .thenReturn(Optional.of(declaredProgram));
          }

          @Nested
          class AndItDoesNotBelongToLoggedInStudent {

            private Student otherStudent;

            @BeforeEach
            void setupAnd3() {
              BddLogger.and("declared program does not belong to logged in student");
              otherStudent = mock(Student.class);
              when(declaredProgram.getStudent()).thenReturn(otherStudent);
            }

            @Test
            void thenItShouldThrowUserNotAuthorizedException() {
              BddLogger.then("it should throw UserNotAuthorizedException");

              assertThrows(
                  UserNotAuthorizedException.class,
                  () ->
                      declaredProgramService.update(
                          declaredProgramId,
                          title,
                          description,
                          organization,
                          result,
                          sourceOfInformation,
                          startDate,
                          endDate,
                          false));

              verify(loggedInUserService).getLoggedInStudent();
              verify(declaredProgramRepository).findById(declaredProgramId);
              verify(declaredProgram).getStudent();
              verify(declaredProgramRepository, never()).save(any());
              verifyNoMoreInteractions(declaredProgram);
            }
          }

          @Nested
          class AndItBelongsToLoggedInStudent {

            @BeforeEach
            void setupAnd3() {
              BddLogger.and("declared program belongs to logged in student");
              when(declaredProgram.getStudent()).thenReturn(loggedStudent);
            }

            @Test
            void thenItShouldUpdateFieldsAndSaveAndReturnSavedProgram() {
              BddLogger.then("it should update fields, save and return saved program");

              DeclaredProgram savedProgram = mock(DeclaredProgram.class);
              when(declaredProgramRepository.save(declaredProgram)).thenReturn(savedProgram);

              DeclaredProgram resultProgram =
                  declaredProgramService.update(
                      declaredProgramId,
                      title,
                      description,
                      organization,
                      result,
                      sourceOfInformation,
                      startDate,
                      endDate,
                      true);

              assertNotNull(resultProgram);
              assertEquals(savedProgram, resultProgram);

              verify(loggedInUserService).getLoggedInStudent();
              verify(declaredProgramRepository).findById(declaredProgramId);
              verify(declaredProgram).getStudent();

              verify(declaredProgram).setTitle(title);
              verify(declaredProgram).setDescription(description);
              verify(declaredProgram).setOrganization(organization);
              verify(declaredProgram).setResult(result);
              verify(declaredProgram).setSourceOfInformation(sourceOfInformation);
              verify(declaredProgram).setStartDate(startDate);
              verify(declaredProgram).setEndDate(endDate);
              verify(declaredProgram).setValorized(true);

              verify(declaredProgram).setStatus(EProgramStatus.IN_PROGRESS);

              verify(declaredProgramRepository).save(declaredProgram);
            }

            @Test
            void thenItShouldUpdateValorizedFieldToTrue() {
              BddLogger.then("it should update valorized to true");

              DeclaredProgram savedProgram = mock(DeclaredProgram.class);
              when(declaredProgramRepository.save(declaredProgram)).thenReturn(savedProgram);

              declaredProgramService.update(
                  declaredProgramId,
                  title,
                  description,
                  organization,
                  result,
                  sourceOfInformation,
                  startDate,
                  endDate,
                  true);

              verify(declaredProgram).setValorized(true);
            }

            @Test
            void thenItShouldUpdateValorizedFieldToFalse() {
              BddLogger.then("it should update valorized to false");

              DeclaredProgram savedProgram = mock(DeclaredProgram.class);
              when(declaredProgramRepository.save(declaredProgram)).thenReturn(savedProgram);

              declaredProgramService.update(
                  declaredProgramId,
                  title,
                  description,
                  organization,
                  result,
                  sourceOfInformation,
                  startDate,
                  endDate,
                  false);

              verify(declaredProgram).setValorized(false);
            }

            @Test
            void thenItShouldSetStatusNotStartedWhenPeriodIsBefore() {
              BddLogger.then("it should set status NOT_STARTED when startDate is in the future");

              LocalDate futureStartDate = LocalDate.now().plusDays(10);
              LocalDate futureEndDate = futureStartDate.plusDays(30);

              declaredProgramService.update(
                  declaredProgramId,
                  title,
                  description,
                  organization,
                  result,
                  sourceOfInformation,
                  futureStartDate,
                  futureEndDate,
                  false);

              verify(declaredProgram).setStatus(EProgramStatus.NOT_STARTED);
              verify(declaredProgramRepository).save(declaredProgram);
            }

            @Test
            void thenItShouldSetStatusCompletedWhenPeriodIsAfter() {
              BddLogger.then("it should set status COMPLETED when endDate is in the past");

              LocalDate pastStartDate = LocalDate.now().minusDays(30);
              LocalDate pastEndDate = LocalDate.now().minusDays(10);

              declaredProgramService.update(
                  declaredProgramId,
                  title,
                  description,
                  organization,
                  result,
                  sourceOfInformation,
                  pastStartDate,
                  pastEndDate,
                  false);

              verify(declaredProgram).setStatus(EProgramStatus.COMPLETED);
              verify(declaredProgramRepository).save(declaredProgram);
            }

            @Nested
            class AndValidationFails {

              @Test
              void thenItShouldThrowWhenTitleIsBlank() {
                BddLogger.then("it should throw when title is blank");

                assertThrows(
                    FieldValidationException.class,
                    () ->
                        declaredProgramService.update(
                            declaredProgramId,
                            "   ",
                            description,
                            organization,
                            result,
                            sourceOfInformation,
                            startDate,
                            endDate,
                            false));

                verify(declaredProgramRepository, never()).save(any());
                verify(declaredProgram, never()).setTitle(any());
              }

              @Test
              void thenItShouldThrowWhenOrganizationIsBlank() {
                BddLogger.then("it should throw when organization is blank");

                assertThrows(
                    FieldValidationException.class,
                    () ->
                        declaredProgramService.update(
                            declaredProgramId,
                            title,
                            description,
                            " ",
                            result,
                            sourceOfInformation,
                            startDate,
                            endDate,
                            false));

                verify(declaredProgramRepository, never()).save(any());
                verify(declaredProgram, never()).setOrganization(any());
              }

              @Test
              void thenItShouldThrowWhenStartDateIsNull() {
                BddLogger.then("it should throw when startDate is null");

                assertThrows(
                    FieldValidationException.class,
                    () ->
                        declaredProgramService.update(
                            declaredProgramId,
                            title,
                            description,
                            organization,
                            result,
                            sourceOfInformation,
                            null,
                            endDate,
                            false));

                verify(declaredProgramRepository, never()).save(any());
                verify(declaredProgram, never()).setStartDate(any());
              }

              @Test
              void thenItShouldThrowWhenEndDateIsBeforeStartDate() {
                BddLogger.then("it should throw when endDate is before startDate");

                LocalDate s = LocalDate.now();
                LocalDate e = s.minusDays(1);

                assertThrows(
                    FieldValidationException.class,
                    () ->
                        declaredProgramService.update(
                            declaredProgramId,
                            title,
                            description,
                            organization,
                            result,
                            sourceOfInformation,
                            s,
                            e,
                            false));

                verify(declaredProgramRepository, never()).save(any());
                verify(declaredProgram, never()).setEndDate(any());
              }
            }
          }
        }
      }

      @Nested
      class AndLoggedInUserServiceFails {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("loggedInUserService fails to provide a logged in student");
          when(loggedInUserService.getLoggedInStudent())
              .thenThrow(new RuntimeException("no logged user"));
        }

        @Test
        void thenItShouldPropagateExceptionAndNotCallRepository() {
          BddLogger.then("it should propagate exception and not call repository");

          assertThrows(
              RuntimeException.class,
              () ->
                  declaredProgramService.update(
                      declaredProgramId,
                      title,
                      description,
                      organization,
                      result,
                      sourceOfInformation,
                      startDate,
                      endDate,
                      false));

          verify(declaredProgramRepository, never()).findById(any());
          verify(declaredProgramRepository, never()).save(any());
        }
      }
    }

    @Nested
    class WhenDeleteIsCalled {

      private List<UUID> declaredProgramIds;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("delete(List<UUID> declaredProgramIds) is called");
        declaredProgramIds = List.of(UUID.randomUUID(), UUID.randomUUID());
      }

      @Nested
      class AndLoggedInStudentIsAvailable {

        private Student loggedStudent;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a logged in student is available");
          loggedStudent = mock(Student.class);
          when(loggedInUserService.getLoggedInStudent()).thenReturn(loggedStudent);
        }

        @Nested
        class AndAllProgramsBelongToLoggedInStudent {

          private List<DeclaredProgram> programs;

          @BeforeEach
          void setupAnd2() {
            BddLogger.and("all declared programs belong to logged in student");
            DeclaredProgram p1 = mock(DeclaredProgram.class);
            DeclaredProgram p2 = mock(DeclaredProgram.class);

            when(p1.getStudent()).thenReturn(loggedStudent);
            when(p2.getStudent()).thenReturn(loggedStudent);

            programs = List.of(p1, p2);
            when(declaredProgramRepository.findAllById(declaredProgramIds)).thenReturn(programs);
          }

          @Test
          void thenItShouldRemoveAllProgramsFromDatabase() {
            BddLogger.then("it should remove all declared programs from database");

            declaredProgramService.delete(declaredProgramIds);

            verify(loggedInUserService).getLoggedInStudent();
            verify(declaredProgramRepository).findAllById(declaredProgramIds);
            verify(declaredProgramRepository).removeAllFromDatabase(programs);
          }
        }

        @Nested
        class AndAtLeastOneProgramDoesNotBelongToLoggedInStudent {

          @BeforeEach
          void setupAnd2() {
            BddLogger.and("at least one declared program does not belong to logged in student");
            DeclaredProgram p1 = mock(DeclaredProgram.class);
            DeclaredProgram p2 = mock(DeclaredProgram.class);

            when(p1.getStudent()).thenReturn(loggedStudent);
            when(p2.getStudent()).thenReturn(mock(Student.class));

            when(declaredProgramRepository.findAllById(declaredProgramIds))
                .thenReturn(List.of(p1, p2));
          }

          @Test
          void thenItShouldThrowUserNotAuthorizedExceptionAndNotDelete() {
            BddLogger.then("it should throw UserNotAuthorizedException and not delete");

            assertThrows(
                UserNotAuthorizedException.class,
                () -> declaredProgramService.delete(declaredProgramIds));

            verify(loggedInUserService).getLoggedInStudent();
            verify(declaredProgramRepository).findAllById(declaredProgramIds);
            verify(declaredProgramRepository, never()).removeAllFromDatabase(any());
          }
        }

        @Nested
        class AndProgramIdsListIsEmpty {

          @Test
          void thenItShouldCallRepositoryWithEmptyListAndRemoveNothing() {
            BddLogger.then("it should call repository with empty list and remove nothing");

            List<UUID> emptyIds = Collections.emptyList();
            when(declaredProgramRepository.findAllById(emptyIds))
                .thenReturn(Collections.emptyList());

            declaredProgramService.delete(emptyIds);

            verify(loggedInUserService).getLoggedInStudent();
            verify(declaredProgramRepository).findAllById(emptyIds);
            verify(declaredProgramRepository).removeAllFromDatabase(Collections.emptyList());
          }
        }
      }

      @Nested
      class AndLoggedInUserServiceFails {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("loggedInUserService fails");
          when(loggedInUserService.getLoggedInStudent())
              .thenThrow(new RuntimeException("no logged user"));
        }

        @Test
        void thenItShouldPropagateExceptionAndNotCallRepository() {
          BddLogger.then("it should propagate exception and not call repository");

          assertThrows(
              RuntimeException.class, () -> declaredProgramService.delete(declaredProgramIds));

          verify(declaredProgramRepository, never()).findAllById(any());
          verify(declaredProgramRepository, never()).removeAllFromDatabase(any());
        }
      }
    }
  }
}
