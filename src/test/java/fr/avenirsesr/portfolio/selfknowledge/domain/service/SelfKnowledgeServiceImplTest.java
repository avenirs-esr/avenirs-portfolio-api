package fr.avenirsesr.portfolio.selfknowledge.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeCategoryListIsEmptyException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeCategoryNotAvailableException;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategoryType;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeCategoryRepository;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.fixture.SelfKnowledgeCategoryFixture;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SelfKnowledgeServiceImplTest {

  @Mock private StudentRepository studentRepository;
  @Mock private SelfKnowledgeCategoryRepository selfKnowledgeCategoryRepository;

  @InjectMocks private SelfKnowledgeServiceImpl selfKnowledgeService;

  private Student student;
  private MockedStatic<RequestContext> mockedRequestContext;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    mockedRequestContext = mockStatic(RequestContext.class);
  }

  @AfterEach
  void tearDown() {
    mockedRequestContext.close();
  }

  @Nested
  class GivenASelfKnowledgeService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a SelfKnowledgeService");
    }

    @Nested
    class AndALoggedInStudent {

      @BeforeEach
      void setupAnd() {
        BddLogger.and("a logged in student");
        mockedRequestContext
            .when(RequestContext::get)
            .thenReturn(new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
        when(studentRepository.findById(eq(student.getId()))).thenReturn(Optional.of(student));
      }

      @Nested
      class AndSelfKnowledgeCategoriesAssociatedToThisStudent {

        private SelfKnowledgeCategory strengthsCategory;
        private SelfKnowledgeCategory valuesCategory;
        private SelfKnowledgeCategory aspirationsCategory;
        private List<SelfKnowledgeCategory> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("self knowledge categories associated to this student");

          strengthsCategory =
              SelfKnowledgeCategoryFixture.create()
                  .withType(ESelfKnowledgeCategoryType.STRENGTHS)
                  .toModel();
          valuesCategory =
              SelfKnowledgeCategoryFixture.create()
                  .withType(ESelfKnowledgeCategoryType.VALUES)
                  .toModel();
          aspirationsCategory =
              SelfKnowledgeCategoryFixture.create()
                  .withType(ESelfKnowledgeCategoryType.ASPIRATIONS)
                  .toModel();

          when(selfKnowledgeCategoryRepository.findAllByStudent(eq(student)))
              .thenReturn(List.of(aspirationsCategory, strengthsCategory, valuesCategory));
        }

        @Nested
        class WhenGettingSelfKnowledgeCategories {

          @BeforeEach
          void setupWhen() {
            BddLogger.when("getting self knowledge categories for the current student");
            result = selfKnowledgeService.getSelfKnowledgeCategories();
          }

          @Test
          void thenItShouldReturnCategoriesSortedByTypeOrder() {
            BddLogger.then(
                "it should return categories sorted by ESelfKnowledgeCategoryType.order");

            assertThat(result).hasSize(3);

            assertThat(result.get(0).getType()).isEqualTo(ESelfKnowledgeCategoryType.STRENGTHS);
            assertThat(result.get(1).getType()).isEqualTo(ESelfKnowledgeCategoryType.VALUES);
            assertThat(result.get(2).getType()).isEqualTo(ESelfKnowledgeCategoryType.ASPIRATIONS);

            verify(studentRepository).findById(eq(student.getId()));
            verify(selfKnowledgeCategoryRepository).findAllByStudent(eq(student));
          }
        }
      }

      @Nested
      class AndNoSelfKnowledgeCategoryForThisStudent {

        private List<SelfKnowledgeCategory> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("no self knowledge category for this student");
          when(selfKnowledgeCategoryRepository.findAllByStudent(eq(student))).thenReturn(List.of());
        }

        @Nested
        class WhenGettingSelfKnowledgeCategories {

          @BeforeEach
          void setupWhen() {
            BddLogger.when("getting self knowledge categories for the current student");
            result = selfKnowledgeService.getSelfKnowledgeCategories();
          }

          @Test
          void thenItShouldReturnAnEmptyList() {
            BddLogger.then("it should return an empty list");
            assertThat(result).isEmpty();

            verify(studentRepository).findById(eq(student.getId()));
            verify(selfKnowledgeCategoryRepository).findAllByStudent(eq(student));
          }
        }
      }

      @Nested
      class AndAvailableSelfKnowledgeCategoriesForThisStudent {

        private SelfKnowledgeCategory motivationsCategory;
        private SelfKnowledgeCategory interestsCategory;
        private List<SelfKnowledgeCategory> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("available self knowledge categories for this student");

          motivationsCategory =
              SelfKnowledgeCategoryFixture.create()
                  .withType(ESelfKnowledgeCategoryType.MOTIVATION)
                  .toModel();
          interestsCategory =
              SelfKnowledgeCategoryFixture.create()
                  .withType(ESelfKnowledgeCategoryType.INTERESTS)
                  .toModel();

          when(selfKnowledgeCategoryRepository.findAllAvailableByStudent(eq(student)))
              .thenReturn(List.of(interestsCategory, motivationsCategory));
        }

        @Nested
        class WhenGettingSelfKnowledgeCategoriesAvailable {

          @BeforeEach
          void setupWhen() {
            BddLogger.when("getting available self knowledge categories for the current student");
            result = selfKnowledgeService.getSelfKnowledgeCategoriesAvailable();
          }

          @Test
          void thenItShouldReturnAvailableCategoriesSortedByTypeOrder() {
            BddLogger.then(
                "it should return available categories sorted by ESelfKnowledgeCategoryType.order");

            assertThat(result).hasSize(2);

            assertThat(result.get(0).getType()).isEqualTo(ESelfKnowledgeCategoryType.MOTIVATION);
            assertThat(result.get(1).getType()).isEqualTo(ESelfKnowledgeCategoryType.INTERESTS);

            verify(studentRepository).findById(eq(student.getId()));
            verify(selfKnowledgeCategoryRepository).findAllAvailableByStudent(eq(student));
          }
        }
      }

      @Nested
      class AndNoAvailableSelfKnowledgeCategoryForThisStudent {

        private List<SelfKnowledgeCategory> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("no available self knowledge category for this student");
          when(selfKnowledgeCategoryRepository.findAllAvailableByStudent(eq(student)))
              .thenReturn(List.of());
        }

        @Nested
        class WhenGettingSelfKnowledgeCategoriesAvailable {

          @BeforeEach
          void setupWhen() {
            BddLogger.when("getting available self knowledge categories for the current student");
            result = selfKnowledgeService.getSelfKnowledgeCategoriesAvailable();
          }

          @Test
          void thenItShouldReturnAnEmptyList() {
            BddLogger.then("it should return an empty list");
            assertThat(result).isEmpty();

            verify(studentRepository).findById(eq(student.getId()));
            verify(selfKnowledgeCategoryRepository).findAllAvailableByStudent(eq(student));
          }
        }
      }

      @Nested
      class AndAValidSelfKnowledgeCategoryList {

        private SelfKnowledgeCategory strengthsCategory;
        private SelfKnowledgeCategory valuesCategory;
        private List<String> categoryIdsAsString;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a valid self knowledge category id list for this student");

          strengthsCategory =
              SelfKnowledgeCategoryFixture.create()
                  .withType(ESelfKnowledgeCategoryType.STRENGTHS)
                  .toModel();
          valuesCategory =
              SelfKnowledgeCategoryFixture.create()
                  .withType(ESelfKnowledgeCategoryType.VALUES)
                  .toModel();

          categoryIdsAsString =
              List.of(strengthsCategory.getId().toString(), valuesCategory.getId().toString());

          when(selfKnowledgeCategoryRepository.findAllAvailableByStudent(eq(student)))
              .thenReturn(List.of(strengthsCategory, valuesCategory));
        }

        @Nested
        class WhenAddingSelfKnowledgeCategories {

          @BeforeEach
          void setupWhen() {
            BddLogger.when("adding self knowledge categories to the current student");
            selfKnowledgeService.addSelfKnowledgeCategories(categoryIdsAsString);
          }

          @Test
          void thenItShouldDelegateToRepositoriesAndAssociateCategories() {
            BddLogger.then(
                "it should use available categories and delegate to studentRepository to"
                    + " associate them");

            verify(selfKnowledgeCategoryRepository).findAllAvailableByStudent(eq(student));
            verify(studentRepository)
                .addSelfKnowledgeCategories(
                    eq(student), eq(List.of(strengthsCategory, valuesCategory)));
          }
        }
      }

      @Nested
      class WhenAddingSelfKnowledgeCategoriesWithEmptyList {

        @Test
        void thenItShouldThrowSelfKnowledgeCategoryListIsEmptyException() {
          BddLogger.when("adding self knowledge categories with an empty list");
          BddLogger.then("it should throw SelfKnowledgeCategoryListIsEmptyException");

          assertThrows(
              SelfKnowledgeCategoryListIsEmptyException.class,
              () -> selfKnowledgeService.addSelfKnowledgeCategories(List.of()));

          verify(studentRepository).findById(eq(student.getId()));
          verifyNoInteractions(selfKnowledgeCategoryRepository);
        }
      }

      @Nested
      class WhenAddingSelfKnowledgeCategoriesWithUnavailableIds {

        @Test
        void thenItShouldThrowSelfKnowledgeCategoryNotAvailableException() {
          BddLogger.when("adding self knowledge categories with ids not in available list");
          BddLogger.then("it should throw SelfKnowledgeCategoryNotAvailableException");

          when(selfKnowledgeCategoryRepository.findAllAvailableByStudent(eq(student)))
              .thenReturn(List.of());

          List<String> categoryIdsAsString =
              List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

          assertThrows(
              SelfKnowledgeCategoryNotAvailableException.class,
              () -> selfKnowledgeService.addSelfKnowledgeCategories(categoryIdsAsString));

          verify(studentRepository).findById(eq(student.getId()));
          verify(selfKnowledgeCategoryRepository).findAllAvailableByStudent(eq(student));
          verifyNoMoreInteractions(studentRepository);
        }
      }
    }

    @Nested
    class AndNoLoggedInUser {

      @BeforeEach
      void setupAnd() {
        BddLogger.and("no logged in user");
        mockedRequestContext
            .when(RequestContext::get)
            .thenReturn(new RequestData(Optional.empty(), ELanguage.FRENCH));
      }

      @Nested
      class WhenGettingSelfKnowledgeCategories {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when("getting self knowledge categories without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          assertThrows(
              UserNotFoundException.class, () -> selfKnowledgeService.getSelfKnowledgeCategories());

          verifyNoInteractions(studentRepository);
          verifyNoInteractions(selfKnowledgeCategoryRepository);
        }
      }

      @Nested
      class WhenGettingSelfKnowledgeCategoriesAvailable {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when(
              "getting available self knowledge categories available without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          assertThrows(
              UserNotFoundException.class,
              () -> selfKnowledgeService.getSelfKnowledgeCategoriesAvailable());

          verifyNoInteractions(studentRepository);
          verifyNoInteractions(selfKnowledgeCategoryRepository);
        }
      }

      @Nested
      class WhenAddingSelfKnowledgeCategories {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when("adding self knowledge categories without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          SelfKnowledgeCategory category =
              SelfKnowledgeCategoryFixture.create()
                  .withType(ESelfKnowledgeCategoryType.STRENGTHS)
                  .toModel();

          List<String> categoryIdsAsString = List.of(category.getId().toString());

          assertThrows(
              UserNotFoundException.class,
              () -> selfKnowledgeService.addSelfKnowledgeCategories(categoryIdsAsString));

          verifyNoInteractions(studentRepository);
          verifyNoInteractions(selfKnowledgeCategoryRepository);
        }
      }
    }

    @Nested
    class AndALoggedInUserThatIsNotStudent {

      @BeforeEach
      void setupAnd() {
        BddLogger.and("a logged in user that is not a student");
        mockedRequestContext
            .when(RequestContext::get)
            .thenReturn(new RequestData(Optional.ofNullable(student.getUser()), ELanguage.FRENCH));
        when(studentRepository.findById(eq(student.getId()))).thenReturn(Optional.empty());
      }

      @Nested
      class WhenGettingSelfKnowledgeCategories {

        @Test
        void thenItShouldThrowUserIsNotStudentException() {
          BddLogger.when("getting self knowledge categories for a non student user");
          BddLogger.then("it should throw UserIsNotStudentException");

          assertThrows(
              UserIsNotStudentException.class,
              () -> selfKnowledgeService.getSelfKnowledgeCategories());

          verify(studentRepository).findById(eq(student.getId()));
          verifyNoInteractions(selfKnowledgeCategoryRepository);
        }
      }

      @Nested
      class WhenGettingSelfKnowledgeCategoriesAvailable {

        @Test
        void thenItShouldThrowUserIsNotStudentException() {
          BddLogger.when("getting self knowledge categories available for a non student user");
          BddLogger.then("it should throw UserIsNotStudentException");

          assertThrows(
              UserIsNotStudentException.class,
              () -> selfKnowledgeService.getSelfKnowledgeCategoriesAvailable());

          verify(studentRepository).findById(eq(student.getId()));
          verifyNoInteractions(selfKnowledgeCategoryRepository);
        }
      }

      @Nested
      class WhenAddingSelfKnowledgeCategories {

        @Test
        void thenItShouldThrowUserIsNotStudentException() {
          BddLogger.when("adding self knowledge categories for a non student user");
          BddLogger.then("it should throw UserIsNotStudentException");

          SelfKnowledgeCategory category =
              SelfKnowledgeCategoryFixture.create()
                  .withType(ESelfKnowledgeCategoryType.STRENGTHS)
                  .toModel();

          List<String> categoryIdsAsString = List.of(category.getId().toString());

          assertThrows(
              UserIsNotStudentException.class,
              () -> selfKnowledgeService.addSelfKnowledgeCategories(categoryIdsAsString));

          verify(studentRepository).findById(eq(student.getId()));
          verifyNoInteractions(selfKnowledgeCategoryRepository);
        }
      }
    }
  }
}
