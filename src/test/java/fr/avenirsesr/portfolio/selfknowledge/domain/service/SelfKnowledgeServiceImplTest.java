package fr.avenirsesr.portfolio.selfknowledge.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.selfknowledge.domain.data.SelfKnowledgeElementDetails;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeCategoryIsMandatoryException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeCategoryListIsEmptyException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeCategoryNotAvailableException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeElementNotFoundException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeElementsAreNotInSameCategoryException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeInvalidDescriptionException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeInvalidRatingException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeInvalidTitleException;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeElementRepository;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.fixture.SelfKnowledgeElementFixture;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SelfKnowledgeServiceImplTest {

  @Mock private StudentService studentService;
  @Mock private SelfKnowledgeElementRepository selfKnowledgeElementRepository;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private SelfKnowledgeServiceImpl selfKnowledgeService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
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
        when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      }

      @Nested
      class AndSelfKnowledgeCategoriesAssociatedToThisStudent {

        private List<ESelfKnowledgeCategory> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("self knowledge categories associated to this student");
          student.setSelfKnowledgeCategories(
              List.of(ESelfKnowledgeCategory.INTERESTS, ESelfKnowledgeCategory.MOTIVATION));
        }

        @Nested
        class WhenGettingSelfKnowledgeCategories {

          @BeforeEach
          void setupWhen() {
            BddLogger.when("getting self knowledge categories for the current student");
            result = selfKnowledgeService.getSelfKnowledgeCategories();
          }

          @Test
          void thenItShouldReturnMandatoryAndStudentCategoriesSortedByOrder() {
            BddLogger.then(
                "it should return mandatory categories plus the student's own categories,"
                    + " sorted by ESelfKnowledgeCategory.order");

            assertThat(result)
                .containsExactly(
                    ESelfKnowledgeCategory.STRENGTHS,
                    ESelfKnowledgeCategory.VALUES,
                    ESelfKnowledgeCategory.ASPIRATIONS,
                    ESelfKnowledgeCategory.MOTIVATION,
                    ESelfKnowledgeCategory.INTERESTS);
          }
        }
      }

      @Nested
      class AndAMandatoryCategoryAlreadyChosenByTheStudent {

        private List<ESelfKnowledgeCategory> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a mandatory category already present in the student's own categories");
          student.setSelfKnowledgeCategories(
              List.of(ESelfKnowledgeCategory.STRENGTHS, ESelfKnowledgeCategory.MOTIVATION));
        }

        @Nested
        class WhenGettingSelfKnowledgeCategories {

          @BeforeEach
          void setupWhen() {
            BddLogger.when("getting self knowledge categories for the current student");
            result = selfKnowledgeService.getSelfKnowledgeCategories();
          }

          @Test
          void thenItShouldNotReturnDuplicates() {
            BddLogger.then("it should return the category only once");

            assertThat(result)
                .containsExactly(
                    ESelfKnowledgeCategory.STRENGTHS,
                    ESelfKnowledgeCategory.VALUES,
                    ESelfKnowledgeCategory.ASPIRATIONS,
                    ESelfKnowledgeCategory.MOTIVATION);
          }
        }
      }

      @Nested
      class AndNoSelfKnowledgeCategoryForThisStudent {

        private List<ESelfKnowledgeCategory> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("no self knowledge category for this student");
          student.setSelfKnowledgeCategories(List.of());
        }

        @Nested
        class WhenGettingSelfKnowledgeCategories {

          @BeforeEach
          void setupWhen() {
            BddLogger.when("getting self knowledge categories for the current student");
            result = selfKnowledgeService.getSelfKnowledgeCategories();
          }

          @Test
          void thenItShouldReturnOnlyMandatoryCategories() {
            BddLogger.then("it should return only the mandatory categories");
            assertThat(result)
                .containsExactly(
                    ESelfKnowledgeCategory.STRENGTHS,
                    ESelfKnowledgeCategory.VALUES,
                    ESelfKnowledgeCategory.ASPIRATIONS);
          }
        }
      }

      @Nested
      class AndAvailableSelfKnowledgeCategoriesForThisStudent {

        private List<ESelfKnowledgeCategory> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("available self knowledge categories for this student");
          student.setSelfKnowledgeCategories(List.of(ESelfKnowledgeCategory.MOTIVATION));
        }

        @Nested
        class WhenGettingSelfKnowledgeCategoriesAvailable {

          @BeforeEach
          void setupWhen() {
            BddLogger.when("getting available self knowledge categories for the current student");
            result = selfKnowledgeService.getSelfKnowledgeCategoriesAvailable();
          }

          @Test
          void thenItShouldReturnNonMandatoryNotAlreadyChosenCategoriesSortedByOrder() {
            BddLogger.then(
                "it should return non mandatory categories not already chosen by the student,"
                    + " sorted by ESelfKnowledgeCategory.order");

            assertThat(result)
                .containsExactly(
                    ESelfKnowledgeCategory.IMPROVEMENT,
                    ESelfKnowledgeCategory.INTERESTS,
                    ESelfKnowledgeCategory.INSPIRATIONS,
                    ESelfKnowledgeCategory.OBLIGATIONS,
                    ESelfKnowledgeCategory.TESTIMONIALS);
          }
        }
      }

      @Nested
      class AndNoAvailableSelfKnowledgeCategoryForThisStudent {

        private List<ESelfKnowledgeCategory> result;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("no available self knowledge category for this student");
          student.setSelfKnowledgeCategories(
              List.of(
                  ESelfKnowledgeCategory.MOTIVATION,
                  ESelfKnowledgeCategory.IMPROVEMENT,
                  ESelfKnowledgeCategory.INTERESTS,
                  ESelfKnowledgeCategory.INSPIRATIONS,
                  ESelfKnowledgeCategory.OBLIGATIONS,
                  ESelfKnowledgeCategory.TESTIMONIALS));
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
          }
        }
      }

      @Nested
      class AndSelfKnowledgeElement {

        private SelfKnowledgeElement selfKnowledgeElement;
        private ESelfKnowledgeCategory selfKnowledgeCategory;
        private UUID selfKnowledgeElementId;
        private PageCriteria pageCriteria;

        @BeforeEach
        void setupAnd() {
          selfKnowledgeElement =
              SelfKnowledgeElementFixture.create().withStudent(student).toModel();
          selfKnowledgeCategory = selfKnowledgeElement.getSelfKnowledgeCategory();
          selfKnowledgeElementId = selfKnowledgeElement.getId();
          pageCriteria = new PageCriteria(0, 8);
        }

        @Nested
        class WhenGettingSelfKnowledgeElementsPaginated {

          @Test
          void thenItShouldGetSelfKnowledgeElementsPaginated() {
            PagedResult<SelfKnowledgeElement> expectedResult =
                new PagedResult<>(List.of(selfKnowledgeElement), new PageInfo(0, 8, 1));

            when(selfKnowledgeElementRepository.findAllByStudentIdAndCategory(
                    student.getId(), selfKnowledgeCategory, pageCriteria, null))
                .thenReturn(expectedResult);

            BddLogger.when("getting self knowledge elements paginated");
            PagedResult<SelfKnowledgeElement> actualResult =
                selfKnowledgeService.getSelfKnowledgeElements(
                    selfKnowledgeCategory, pageCriteria, null);

            BddLogger.then("it should retrieve self knowledge elements paginated");
            assertThat(actualResult).isEqualTo(expectedResult);

            verify(loggedInUserService).getLoggedInStudent();
            verify(selfKnowledgeElementRepository)
                .findAllByStudentIdAndCategory(
                    student.getId(), selfKnowledgeCategory, pageCriteria, null);
          }

          @Test
          void thenItShouldDelegateIsValorizedFilterToRepository() {
            PagedResult<SelfKnowledgeElement> expectedResult =
                new PagedResult<>(List.of(selfKnowledgeElement), new PageInfo(0, 8, 1));

            when(selfKnowledgeElementRepository.findAllByStudentIdAndCategory(
                    student.getId(), selfKnowledgeCategory, pageCriteria, true))
                .thenReturn(expectedResult);

            BddLogger.when("getting self knowledge elements paginated with isValorized=true");
            PagedResult<SelfKnowledgeElement> actualResult =
                selfKnowledgeService.getSelfKnowledgeElements(
                    selfKnowledgeCategory, pageCriteria, true);

            BddLogger.then("it should delegate the isValorized filter to the repository");
            assertThat(actualResult).isEqualTo(expectedResult);
            verify(selfKnowledgeElementRepository)
                .findAllByStudentIdAndCategory(
                    student.getId(), selfKnowledgeCategory, pageCriteria, true);
          }
        }

        @Nested
        class WhenGettingSelfKnowledgeElementDetails {

          @Test
          void thenItShouldGetSelfKnowledgeElementDetails() {
            when(selfKnowledgeElementRepository.findById(selfKnowledgeElementId))
                .thenReturn(Optional.of(selfKnowledgeElement));

            BddLogger.when("getting self knowledge elements details");
            SelfKnowledgeElementDetails actualResult =
                selfKnowledgeService.getSelfKnowledgeElementDetails(selfKnowledgeElement.getId());

            BddLogger.then("it should retrieve self knowledge elements details");
            assertThat(actualResult.selfKnowledgeElement()).isEqualTo(selfKnowledgeElement);

            verify(loggedInUserService).getLoggedInStudent();
            verify(selfKnowledgeElementRepository).findById(selfKnowledgeElementId);
          }
        }

        @Nested
        class WhenGettingSelfKnowledgeElementDetailsButNotOwnedByStudent {

          @Test
          void thenItShouldThrowUserNotAuthorizedException() {
            BddLogger.when("getting element details but element belongs to another student");

            Student otherStudent = StudentFixture.create().toModel();
            SelfKnowledgeElement elementOfAnotherStudent =
                SelfKnowledgeElementFixture.create().withStudent(otherStudent).toModel();

            when(selfKnowledgeElementRepository.findById(selfKnowledgeElementId))
                .thenReturn(Optional.of(elementOfAnotherStudent));

            assertThrows(
                UserNotAuthorizedException.class,
                () -> selfKnowledgeService.getSelfKnowledgeElementDetails(selfKnowledgeElementId));

            verify(selfKnowledgeElementRepository).findById(selfKnowledgeElementId);
          }
        }

        @Nested
        class WhenUpdatingSelfKnowledgeElement {

          @Test
          void thenItShouldUpdateSelfKnowledgeElement() {
            String newTitle = "Titre Mis à jour";
            String newDescription = "Description Mise à jour";
            Integer newRating = 4;

            when(selfKnowledgeElementRepository.findById(selfKnowledgeElementId))
                .thenReturn(Optional.of(selfKnowledgeElement));
            when(selfKnowledgeElementRepository.save(any(SelfKnowledgeElement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            BddLogger.when("updating self knowledge element");
            SelfKnowledgeElement result =
                selfKnowledgeService.updateSelfKnowledgeElement(
                    selfKnowledgeElementId, newTitle, newDescription, newRating, true);

            BddLogger.then("it should update self knowledge element");

            assertThat(result.getTitle()).isEqualTo(newTitle);
            assertThat(result.getDescription()).isEqualTo(newDescription);
            assertThat(result.getRating()).isEqualTo(newRating);
            assertThat(result.isValorized()).isTrue();

            verify(loggedInUserService).getLoggedInStudent();
            verify(selfKnowledgeElementRepository).findById(selfKnowledgeElementId);
            verify(selfKnowledgeElementRepository).save(selfKnowledgeElement);
          }
        }

        @Nested
        class WhenUpdatingSelfKnowledgeElementValorizedFlagToFalse {

          @Test
          void thenItShouldSetValorizedToFalse() {
            BddLogger.when("updating self knowledge element with valorized set to false");

            when(selfKnowledgeElementRepository.findById(selfKnowledgeElementId))
                .thenReturn(Optional.of(selfKnowledgeElement));
            when(selfKnowledgeElementRepository.save(any(SelfKnowledgeElement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            SelfKnowledgeElement result =
                selfKnowledgeService.updateSelfKnowledgeElement(
                    selfKnowledgeElementId, "Title", "Description", 1, false);

            BddLogger.then("it should set valorized to false");
            assertThat(result.isValorized()).isFalse();

            verify(selfKnowledgeElementRepository).save(selfKnowledgeElement);
          }
        }

        @Nested
        class WhenUpdatingSelfKnowledgeElementWithNullRating {

          @Test
          void thenItShouldNotOverrideExistingRating() {
            BddLogger.when("updating element with null rating");

            Integer existingRating = selfKnowledgeElement.getRating();
            when(selfKnowledgeElementRepository.findById(selfKnowledgeElementId))
                .thenReturn(Optional.of(selfKnowledgeElement));
            when(selfKnowledgeElementRepository.save(any(SelfKnowledgeElement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            selfKnowledgeService.updateSelfKnowledgeElement(
                selfKnowledgeElementId, "New title", "New desc", null, false);

            BddLogger.then("it should keep previous rating");
            assertThat(selfKnowledgeElement.getRating()).isEqualTo(existingRating);

            verify(selfKnowledgeElementRepository).save(selfKnowledgeElement);
          }
        }

        @Nested
        class WhenUpdatingSelfKnowledgeElementButNotOwnedByStudent {

          @Test
          void thenItShouldThrowUserNotAuthorizedException() {
            BddLogger.when("updating element but element belongs to another student");

            Student otherStudent = StudentFixture.create().toModel();
            SelfKnowledgeElement elementOfAnotherStudent =
                SelfKnowledgeElementFixture.create().withStudent(otherStudent).toModel();

            when(selfKnowledgeElementRepository.findById(selfKnowledgeElementId))
                .thenReturn(Optional.of(elementOfAnotherStudent));

            assertThrows(
                UserNotAuthorizedException.class,
                () ->
                    selfKnowledgeService.updateSelfKnowledgeElement(
                        selfKnowledgeElementId, "Title", "Description", 1, false));

            verify(selfKnowledgeElementRepository).findById(selfKnowledgeElementId);
            verify(selfKnowledgeElementRepository, never()).save(any());
          }
        }

        @Nested
        class WhenUpdatingSelfKnowledgeElementWithInvalidTitle {

          @Test
          void thenItShouldThrowSelfKnowledgeInvalidTitleException() {
            BddLogger.when("updating with an invalid title length");
            String tooLong = "T".repeat(500);

            assertThrows(
                SelfKnowledgeInvalidTitleException.class,
                () ->
                    selfKnowledgeService.updateSelfKnowledgeElement(
                        selfKnowledgeElementId, tooLong, "Description", 1, false));

            verifyNoInteractions(selfKnowledgeElementRepository);
          }
        }

        @Nested
        class WhenUpdatingSelfKnowledgeElementWithInvalidDescription {

          @Test
          void thenItShouldThrowSelfKnowledgeInvalidDescriptionException() {
            BddLogger.when("updating with an invalid description length");
            String tooLong = "D".repeat(5000);

            assertThrows(
                SelfKnowledgeInvalidDescriptionException.class,
                () ->
                    selfKnowledgeService.updateSelfKnowledgeElement(
                        selfKnowledgeElementId, "Title", tooLong, 1, false));

            verifyNoInteractions(selfKnowledgeElementRepository);
          }
        }

        @Nested
        class WhenUpdatingSelfKnowledgeElementWithInvalidRating {

          @Test
          void thenItShouldThrowSelfKnowledgeInvalidRatingException() {
            BddLogger.when("updating with invalid rating");
            Integer invalid = -999;

            assertThrows(
                SelfKnowledgeInvalidRatingException.class,
                () ->
                    selfKnowledgeService.updateSelfKnowledgeElement(
                        selfKnowledgeElementId, "Title", "Description", invalid, false));

            verifyNoInteractions(selfKnowledgeElementRepository);
          }
        }

        @Nested
        class WhenDeletingSelfKnowledgeElement {

          @Test
          void thenItShouldDeleteSelfKnowledgeElement() {
            List<UUID> idsToDelete = List.of(selfKnowledgeElementId);

            when(selfKnowledgeElementRepository.findAllById(idsToDelete))
                .thenReturn(List.of(selfKnowledgeElement));

            BddLogger.when("deleting self knowledge element");
            selfKnowledgeService.deleteSelfKnowledgeElements(idsToDelete);

            BddLogger.then("it should delete self knowledge element");
            verify(loggedInUserService).getLoggedInStudent();
            verify(selfKnowledgeElementRepository).findAllById(idsToDelete);
            verify(selfKnowledgeElementRepository)
                .removeAllFromDatabase(List.of(selfKnowledgeElement));
          }
        }

        @Nested
        class WhenDeletingSelfKnowledgeElementsButNotOwnedByStudent {

          @Test
          void thenItShouldThrowUserNotAuthorizedException() {
            BddLogger.when("deleting elements but at least one belongs to another student");

            Student otherStudent = StudentFixture.create().toModel();
            SelfKnowledgeElement elementOfAnotherStudent =
                SelfKnowledgeElementFixture.create().withStudent(otherStudent).toModel();

            List<UUID> ids = List.of(selfKnowledgeElementId);

            when(selfKnowledgeElementRepository.findAllById(ids))
                .thenReturn(List.of(elementOfAnotherStudent));

            assertThrows(
                UserNotAuthorizedException.class,
                () -> selfKnowledgeService.deleteSelfKnowledgeElements(ids));

            verify(selfKnowledgeElementRepository, never()).removeAllFromDatabase(anyList());
          }
        }

        @Nested
        class WhenDeletingSelfKnowledgeElementsFromDifferentCategories {

          @Test
          void thenItShouldThrowSelfKnowledgeElementsAreNotInSameCategoryException() {
            BddLogger.when("deleting elements from different categories");

            SelfKnowledgeElement e1 =
                SelfKnowledgeElementFixture.create()
                    .withStudent(student)
                    .withSelfKnowledgeCategory(ESelfKnowledgeCategory.STRENGTHS)
                    .toModel();

            SelfKnowledgeElement e2 =
                SelfKnowledgeElementFixture.create()
                    .withStudent(student)
                    .withSelfKnowledgeCategory(ESelfKnowledgeCategory.VALUES)
                    .toModel();

            List<UUID> ids = List.of(e1.getId(), e2.getId());
            when(selfKnowledgeElementRepository.findAllById(ids)).thenReturn(List.of(e1, e2));

            assertThrows(
                SelfKnowledgeElementsAreNotInSameCategoryException.class,
                () -> selfKnowledgeService.deleteSelfKnowledgeElements(ids));

            verify(selfKnowledgeElementRepository, never()).removeAllFromDatabase(anyList());
          }
        }
      }

      @Nested
      class AndUnknownElements {

        private UUID unknownId;

        @BeforeEach
        void setupAnd() {
          unknownId = UUID.randomUUID();

          when(selfKnowledgeElementRepository.findById(eq(unknownId))).thenReturn(Optional.empty());
        }

        @Nested
        class WhenGettingSelfKnowledgeElementDetails {

          @Test
          void thenItShouldThrowSelfKnowledgeElementNotFoundException() {
            BddLogger.when("getting self knowledge elements details with unknown element");
            BddLogger.then("it should throw SelfKnowledgeElementNotFoundException");

            assertThrows(
                SelfKnowledgeElementNotFoundException.class,
                () -> selfKnowledgeService.getSelfKnowledgeElementDetails(unknownId));
          }
        }

        @Nested
        class WhenUpdatingSelfKnowledgeElement {

          @Test
          void thenItShouldThrowSelfKnowledgeElementNotFoundException() {
            BddLogger.when("updating self knowledge element with unknown element");
            BddLogger.then("it should throw SelfKnowledgeElementNotFoundException");

            assertThrows(
                SelfKnowledgeElementNotFoundException.class,
                () ->
                    selfKnowledgeService.updateSelfKnowledgeElement(
                        unknownId, "Title", "Description", 1, false));
          }
        }
      }

      @Nested
      class WhenCreatingSelfKnowledgeElement {

        @Test
        void thenItShouldCreateSelfKnowledgeElement() {
          ESelfKnowledgeCategory selfKnowledgeCategory = ESelfKnowledgeCategory.STRENGTHS;
          String title = "Empathie";
          String description = "J’ai une bonne capacité à écouter et comprendre les autres.";
          Integer rating = 5;

          when(selfKnowledgeElementRepository.save(any(SelfKnowledgeElement.class)))
              .thenAnswer(invocation -> invocation.getArgument(0));

          BddLogger.when("creating self knowledge element");
          SelfKnowledgeElement result =
              selfKnowledgeService.createSelfKnowledgeElement(
                  selfKnowledgeCategory, title, description, rating);

          BddLogger.then("it should create self knowledge element");

          assertThat(result).isNotNull();
          assertThat(result.getId()).isNotNull();
          assertThat(result.getTitle()).isEqualTo(title);
          assertThat(result.getDescription()).isEqualTo(description);
          assertThat(result.getRating()).isEqualTo(rating);
          assertThat(result.getStudent()).isEqualTo(student);
          assertThat(result.getSelfKnowledgeCategory()).isEqualTo(selfKnowledgeCategory);

          verify(loggedInUserService).getLoggedInStudent();
          verify(selfKnowledgeElementRepository).save(any(SelfKnowledgeElement.class));
        }
      }

      @Nested
      class WhenCreatingSelfKnowledgeElementWithInvalidTitle {

        @Test
        void thenItShouldThrowSelfKnowledgeInvalidTitleException() {
          BddLogger.when("creating self knowledge element with invalid title");

          String tooLongTitle = "T".repeat(500);

          assertThrows(
              SelfKnowledgeInvalidTitleException.class,
              () ->
                  selfKnowledgeService.createSelfKnowledgeElement(
                      ESelfKnowledgeCategory.STRENGTHS, tooLongTitle, "Description", 1));

          verify(selfKnowledgeElementRepository, never()).save(any());
        }
      }

      @Nested
      class WhenCreatingSelfKnowledgeElementWithInvalidDescription {

        @Test
        void thenItShouldThrowSelfKnowledgeInvalidDescriptionException() {
          BddLogger.when("creating self knowledge element with invalid description");

          String tooLongDescription = "D".repeat(5000);

          assertThrows(
              SelfKnowledgeInvalidDescriptionException.class,
              () ->
                  selfKnowledgeService.createSelfKnowledgeElement(
                      ESelfKnowledgeCategory.STRENGTHS, "Title", tooLongDescription, 1));

          verify(selfKnowledgeElementRepository, never()).save(any());
        }
      }

      @Nested
      class WhenCreatingSelfKnowledgeElementWithInvalidRating {

        @Test
        void thenItShouldThrowSelfKnowledgeInvalidRatingException() {
          BddLogger.when("creating self knowledge element with invalid rating");

          assertThrows(
              SelfKnowledgeInvalidRatingException.class,
              () ->
                  selfKnowledgeService.createSelfKnowledgeElement(
                      ESelfKnowledgeCategory.STRENGTHS, "Title", "Description", -999));

          verify(selfKnowledgeElementRepository, never()).save(any());
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

          verifyNoInteractions(studentService);
        }
      }

      @Nested
      class WhenAddingASelfKnowledgeCategoryThatIsNotAvailable {

        @Test
        void thenItShouldThrowSelfKnowledgeCategoryNotAvailableException() {
          BddLogger.when(
              "adding a self knowledge category that is not available (mandatory or already"
                  + " chosen)");
          BddLogger.then("it should throw SelfKnowledgeCategoryNotAvailableException");

          List<ESelfKnowledgeCategory> requested = List.of(ESelfKnowledgeCategory.STRENGTHS);

          assertThrows(
              SelfKnowledgeCategoryNotAvailableException.class,
              () -> selfKnowledgeService.addSelfKnowledgeCategories(requested));

          verify(studentService, never()).addSelfKnowledgeCategories(any(), anyList());
        }
      }

      @Nested
      class AndAValidSelfKnowledgeCategoryList {

        private List<ESelfKnowledgeCategory> categories;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a valid self knowledge category list for this student");
          categories = List.of(ESelfKnowledgeCategory.MOTIVATION, ESelfKnowledgeCategory.INTERESTS);
        }

        @Nested
        class WhenAddingSelfKnowledgeCategories {

          @BeforeEach
          void setupWhen() {
            BddLogger.when("adding self knowledge categories to the current student");
            selfKnowledgeService.addSelfKnowledgeCategories(categories);
          }

          @Test
          void thenItShouldDelegateToStudentServiceToAssociateCategories() {
            BddLogger.then("it should delegate to studentService to associate the categories");

            verify(studentService).addSelfKnowledgeCategories(eq(student), eq(categories));
          }
        }
      }

      @Nested
      class AndANonMandatorySelfKnowledgeCategory {

        private ESelfKnowledgeCategory removableCategory;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a non mandatory self knowledge category linked to this student");
          removableCategory = ESelfKnowledgeCategory.MOTIVATION;
        }

        @Nested
        class WhenRemovingSelfKnowledgeCategory {

          @BeforeEach
          void setupWhen() {
            BddLogger.when(
                "removing a non mandatory self knowledge category for the current student");
            selfKnowledgeService.removeSelfKnowledgeCategory(removableCategory);
          }

          @Test
          void thenItShouldDeleteElementsAndRemoveCategoryForStudent() {
            BddLogger.then(
                "it should delete all elements for this student and category, then remove the"
                    + " category link for the student");

            verify(selfKnowledgeElementRepository)
                .deleteAllByStudentAndCategory(eq(student), eq(removableCategory));
            verify(studentService).removeSelfKnowledgeCategory(eq(student), eq(removableCategory));
          }
        }
      }

      @Nested
      class WhenRemovingMandatorySelfKnowledgeCategory {

        @Test
        void thenItShouldThrowSelfKnowledgeCategoryIsMandatoryException() {
          BddLogger.when("removing a mandatory self knowledge category");
          BddLogger.then("it should throw SelfKnowledgeCategoryIsMandatoryException");

          assertThrows(
              SelfKnowledgeCategoryIsMandatoryException.class,
              () ->
                  selfKnowledgeService.removeSelfKnowledgeCategory(
                      ESelfKnowledgeCategory.STRENGTHS));

          verifyNoInteractions(selfKnowledgeElementRepository);
          verify(studentService, never()).removeSelfKnowledgeCategory(any(), any());
        }
      }
    }

    @Nested
    class AndNoLoggedInUser {

      @BeforeEach
      void setupAnd() {
        BddLogger.and("no logged in user");
        when(loggedInUserService.getLoggedInStudent()).thenThrow(UserNotFoundException.class);
      }

      @Nested
      class WhenGettingSelfKnowledgeCategories {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when("getting self knowledge categories without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          assertThrows(
              UserNotFoundException.class, () -> selfKnowledgeService.getSelfKnowledgeCategories());

          verifyNoInteractions(studentService);
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

          verifyNoInteractions(studentService);
        }
      }

      @Nested
      class WhenAddingSelfKnowledgeCategories {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when("adding self knowledge categories without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          List<ESelfKnowledgeCategory> categories = List.of(ESelfKnowledgeCategory.STRENGTHS);

          assertThrows(
              UserNotFoundException.class,
              () -> selfKnowledgeService.addSelfKnowledgeCategories(categories));

          verifyNoInteractions(studentService);
        }
      }

      @Nested
      class WhenRemovingSelfKnowledgeCategory {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when("removing a self knowledge category without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          assertThrows(
              UserNotFoundException.class,
              () ->
                  selfKnowledgeService.removeSelfKnowledgeCategory(
                      ESelfKnowledgeCategory.STRENGTHS));

          verifyNoInteractions(studentService);
          verifyNoInteractions(selfKnowledgeElementRepository);
        }
      }

      @Nested
      class WhenGettingSelfKnowledgeElementsPaginated {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when("getting self knowledge elements paginated without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          assertThrows(
              UserNotFoundException.class,
              () ->
                  selfKnowledgeService.getSelfKnowledgeElements(
                      ESelfKnowledgeCategory.STRENGTHS, new PageCriteria(0, 8), null));
        }
      }

      @Nested
      class WhenGettingSelfKnowledgeElementDetails {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when("getting self knowledge elements details without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          assertThrows(
              UserNotFoundException.class,
              () -> selfKnowledgeService.getSelfKnowledgeElementDetails(UUID.randomUUID()));
        }
      }

      @Nested
      class WhenCreatingSelfKnowledgeElement {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when("creating self knowledge element without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          assertThrows(
              UserNotFoundException.class,
              () ->
                  selfKnowledgeService.createSelfKnowledgeElement(
                      ESelfKnowledgeCategory.STRENGTHS, "Title", "Description", 1));
        }
      }

      @Nested
      class WhenUpdatingSelfKnowledgeElement {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when("updating self knowledge element without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          assertThrows(
              UserNotFoundException.class,
              () ->
                  selfKnowledgeService.updateSelfKnowledgeElement(
                      UUID.randomUUID(), "Title", "Description", 1, false));
        }
      }

      @Nested
      class WhenDeletingSelfKnowledgeElement {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when("deleting self knowledge element without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          assertThrows(
              UserNotFoundException.class,
              () ->
                  selfKnowledgeService.deleteSelfKnowledgeElements(
                      List.of(UUID.randomUUID(), UUID.randomUUID())));
        }
      }
    }

    @Nested
    class AndALoggedInUserThatIsNotStudent {

      @BeforeEach
      void setupAnd() {
        BddLogger.and("a logged in user that is not a student");
        when(loggedInUserService.getLoggedInStudent()).thenThrow(UserIsNotStudentException.class);
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
        }
      }

      @Nested
      class WhenAddingSelfKnowledgeCategories {

        @Test
        void thenItShouldThrowUserIsNotStudentException() {
          BddLogger.when("adding self knowledge categories for a non student user");
          BddLogger.then("it should throw UserIsNotStudentException");

          List<ESelfKnowledgeCategory> categories = List.of(ESelfKnowledgeCategory.STRENGTHS);

          assertThrows(
              UserIsNotStudentException.class,
              () -> selfKnowledgeService.addSelfKnowledgeCategories(categories));
        }
      }

      @Nested
      class WhenRemovingSelfKnowledgeCategory {

        @Test
        void thenItShouldThrowUserIsNotStudentException() {
          BddLogger.when("removing self knowledge category for a non student user");
          BddLogger.then("it should throw UserIsNotStudentException");

          assertThrows(
              UserIsNotStudentException.class,
              () ->
                  selfKnowledgeService.removeSelfKnowledgeCategory(
                      ESelfKnowledgeCategory.STRENGTHS));
        }
      }

      @Nested
      class WhenGettingSelfKnowledgeElementsPaginated {

        @Test
        void thenItShouldThrowUserIsNotStudentException() {
          BddLogger.when("getting self knowledge elements paginated for a non student user");
          BddLogger.then("it should throw UserIsNotStudentException");

          assertThrows(
              UserIsNotStudentException.class,
              () ->
                  selfKnowledgeService.getSelfKnowledgeElements(
                      ESelfKnowledgeCategory.STRENGTHS, new PageCriteria(0, 8), null));
        }
      }

      @Nested
      class WhenGettingSelfKnowledgeElementDetails {

        @Test
        void thenItShouldThrowUserIsNotStudentException() {
          BddLogger.when("getting self knowledge elements details for a non student user");
          BddLogger.then("it should throw UserIsNotStudentException");

          assertThrows(
              UserIsNotStudentException.class,
              () -> selfKnowledgeService.getSelfKnowledgeElementDetails(UUID.randomUUID()));
        }
      }

      @Nested
      class WhenCreatingSelfKnowledgeElement {

        @Test
        void thenItShouldThrowUserIsNotStudentException() {
          BddLogger.when("creating self knowledge element for a non student user");
          BddLogger.then("it should throw UserIsNotStudentException");

          assertThrows(
              UserIsNotStudentException.class,
              () ->
                  selfKnowledgeService.createSelfKnowledgeElement(
                      ESelfKnowledgeCategory.STRENGTHS, "Title", "Description", 1));
        }
      }

      @Nested
      class WhenUpdatingSelfKnowledgeElement {

        @Test
        void thenItShouldThrowUserIsNotStudentException() {
          BddLogger.when("updating self knowledge element for a non student user");
          BddLogger.then("it should throw UserIsNotStudentException");

          assertThrows(
              UserIsNotStudentException.class,
              () ->
                  selfKnowledgeService.updateSelfKnowledgeElement(
                      UUID.randomUUID(), "Title", "Description", 1, false));
        }
      }

      @Nested
      class WhenDeletingSelfKnowledgeElement {

        @Test
        void thenItShouldThrowUserIsNotStudentException() {
          BddLogger.when("deleting self knowledge element for a non student user");
          BddLogger.then("it should throw UserIsNotStudentException");

          assertThrows(
              UserIsNotStudentException.class,
              () ->
                  selfKnowledgeService.deleteSelfKnowledgeElements(
                      List.of(UUID.randomUUID(), UUID.randomUUID())));
        }
      }
    }
  }
}
