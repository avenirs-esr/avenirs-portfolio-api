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
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeCategoryNotFoundException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeElementNotFoundException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeElementsAreNotInSameCategoryException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeInvalidDescriptionException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeInvalidRatingException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeInvalidTitleException;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategoryType;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeCategoryRepository;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeElementRepository;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.fixture.SelfKnowledgeCategoryFixture;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.fixture.SelfKnowledgeElementFixture;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SelfKnowledgeServiceImplTest {

  @Mock private StudentService studentService;
  @Mock private SelfKnowledgeCategoryRepository selfKnowledgeCategoryRepository;
  @Mock private SelfKnowledgeElementRepository selfKnowledgeElementRepository;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private SelfKnowledgeServiceImpl selfKnowledgeService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
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

            verify(selfKnowledgeCategoryRepository).findAllAvailableByStudent(eq(student));
          }
        }
      }

      @Nested
      class AndUnknownCategory {

        private UUID unknownId;

        @BeforeEach
        void setupAnd() {
          unknownId = UUID.randomUUID();

          when(selfKnowledgeCategoryRepository.findById(eq(unknownId)))
              .thenReturn(Optional.empty());
        }

        @Nested
        class WhenGettingSelfKnowledgeElementsPaginated {

          @Test
          void thenItShouldThrowSelfKnowledgeCategoryNotFoundException() {
            BddLogger.when("getting self knowledge elements paginated with unknown category");
            BddLogger.then("it should throw SelfKnowledgeCategoryNotFoundException");

            assertThrows(
                SelfKnowledgeCategoryNotFoundException.class,
                () ->
                    selfKnowledgeService.getSelfKnowledgeElements(
                        unknownId, new PageCriteria(0, 8)));
          }
        }

        @Nested
        class WhenCreatingSelfKnowledgeElement {

          @Test
          void thenItShouldThrowSelfKnowledgeCategoryNotFoundException() {
            BddLogger.when("creating self knowledge element with unknown category");
            BddLogger.then("it should throw SelfKnowledgeCategoryNotFoundException");

            assertThrows(
                SelfKnowledgeCategoryNotFoundException.class,
                () ->
                    selfKnowledgeService.createSelfKnowledgeElement(
                        unknownId, "Title", "Description", 1));
          }
        }
      }

      @Nested
      class AndSelfKnowledgeElement {

        private SelfKnowledgeElement selfKnowledgeElement;
        private SelfKnowledgeCategory selfKnowledgeCategory;
        private UUID selfKnowledgeElementId;
        private UUID selfKnowledgeCategoryId;
        private PageCriteria pageCriteria;

        @BeforeEach
        void setupAnd() {
          selfKnowledgeElement =
              SelfKnowledgeElementFixture.create().withStudent(student).toModel();
          selfKnowledgeCategory = selfKnowledgeElement.getSelfKnowledgeCategory();
          selfKnowledgeElementId = selfKnowledgeElement.getId();
          selfKnowledgeCategoryId = selfKnowledgeCategory.getId();
          pageCriteria = new PageCriteria(0, 8);
        }

        @Nested
        class WhenGettingSelfKnowledgeElementsPaginated {

          @Test
          void thenItShouldGetSelfKnowledgeElementsPaginated() {
            PagedResult<SelfKnowledgeElement> expectedResult =
                new PagedResult<>(List.of(selfKnowledgeElement), new PageInfo(0, 8, 1));

            when(selfKnowledgeCategoryRepository.findById(selfKnowledgeCategoryId))
                .thenReturn(Optional.of(selfKnowledgeCategory));
            when(selfKnowledgeElementRepository.findAllByStudentIdAndCategoryId(
                    student.getId(), selfKnowledgeCategoryId, pageCriteria))
                .thenReturn(expectedResult);

            BddLogger.when("getting self knowledge elements paginated");
            PagedResult<SelfKnowledgeElement> actualResult =
                selfKnowledgeService.getSelfKnowledgeElements(
                    selfKnowledgeCategoryId, pageCriteria);

            BddLogger.then("it should retrieve self knowledge elements paginated");
            assertThat(actualResult).isEqualTo(expectedResult);

            verify(loggedInUserService).getLoggedInStudent();
            verify(selfKnowledgeCategoryRepository).findById(selfKnowledgeCategoryId);
            verify(selfKnowledgeElementRepository)
                .findAllByStudentIdAndCategoryId(
                    student.getId(), selfKnowledgeCategoryId, pageCriteria);
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
                    selfKnowledgeElementId, newTitle, newDescription, newRating);

            BddLogger.then("it should update self knowledge element");

            assertThat(result.getTitle()).isEqualTo(newTitle);
            assertThat(result.getDescription()).isEqualTo(newDescription);
            assertThat(result.getRating()).isEqualTo(newRating);

            verify(loggedInUserService).getLoggedInStudent();
            verify(selfKnowledgeElementRepository).findById(selfKnowledgeElementId);
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
                selfKnowledgeElementId, "New title", "New desc", null);

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
                        selfKnowledgeElementId, "Title", "Description", 1));

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
                        selfKnowledgeElementId, tooLong, "Description", 1));

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
                        selfKnowledgeElementId, "Title", tooLong, 1));

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
                        selfKnowledgeElementId, "Title", "Description", invalid));

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

            SelfKnowledgeCategory cat1 =
                SelfKnowledgeCategoryFixture.create()
                    .withType(ESelfKnowledgeCategoryType.STRENGTHS)
                    .toModel();
            SelfKnowledgeCategory cat2 =
                SelfKnowledgeCategoryFixture.create()
                    .withType(ESelfKnowledgeCategoryType.VALUES)
                    .toModel();

            SelfKnowledgeElement e1 =
                SelfKnowledgeElementFixture.create()
                    .withStudent(student)
                    .withSelfKnowledgeCategory(cat1)
                    .toModel();

            SelfKnowledgeElement e2 =
                SelfKnowledgeElementFixture.create()
                    .withStudent(student)
                    .withSelfKnowledgeCategory(cat2)
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
          when(selfKnowledgeElementRepository.findAllById(eq(List.of(unknownId))))
              .thenReturn(Collections.emptyList());
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
                        unknownId, "Title", "Description", 1));
          }
        }
      }

      @Nested
      class WhenCreatingSelfKnowledgeElement {

        @Test
        void thenItShouldCreateSelfKnowledgeElement() {
          SelfKnowledgeCategory selfKnowledgeCategory =
              SelfKnowledgeCategoryFixture.create().toModel();
          UUID selfKnowledgeCategoryId = selfKnowledgeCategory.getId();
          String title = "Empathie";
          String description = "J’ai une bonne capacité à écouter et comprendre les autres.";
          Integer rating = 5;

          when(selfKnowledgeCategoryRepository.findById(selfKnowledgeCategoryId))
              .thenReturn(Optional.of(selfKnowledgeCategory));
          when(selfKnowledgeElementRepository.save(any(SelfKnowledgeElement.class)))
              .thenAnswer(invocation -> invocation.getArgument(0));

          BddLogger.when("creating self knowledge element");
          SelfKnowledgeElement result =
              selfKnowledgeService.createSelfKnowledgeElement(
                  selfKnowledgeCategoryId, title, description, rating);

          BddLogger.then("it should create self knowledge element");

          assertThat(result).isNotNull();
          assertThat(result.getId()).isNotNull();
          assertThat(result.getTitle()).isEqualTo(title);
          assertThat(result.getDescription()).isEqualTo(description);
          assertThat(result.getRating()).isEqualTo(rating);
          assertThat(result.getStudent()).isEqualTo(student);
          assertThat(result.getSelfKnowledgeCategory()).isEqualTo(selfKnowledgeCategory);

          verify(loggedInUserService).getLoggedInStudent();
          verify(selfKnowledgeCategoryRepository).findById(selfKnowledgeCategoryId);
          verify(selfKnowledgeElementRepository).save(any(SelfKnowledgeElement.class));
        }
      }

      @Nested
      class WhenCreatingSelfKnowledgeElementWithInvalidTitle {

        @Test
        void thenItShouldThrowSelfKnowledgeInvalidTitleException() {
          BddLogger.when("creating self knowledge element with invalid title");
          SelfKnowledgeCategory selfKnowledgeCategory =
              SelfKnowledgeCategoryFixture.create().toModel();

          when(selfKnowledgeCategoryRepository.findById(selfKnowledgeCategory.getId()))
              .thenReturn(Optional.of(selfKnowledgeCategory));

          String tooLongTitle = "T".repeat(500);

          assertThrows(
              SelfKnowledgeInvalidTitleException.class,
              () ->
                  selfKnowledgeService.createSelfKnowledgeElement(
                      selfKnowledgeCategory.getId(), tooLongTitle, "Description", 1));

          verify(selfKnowledgeElementRepository, never()).save(any());
        }
      }

      @Nested
      class WhenCreatingSelfKnowledgeElementWithInvalidDescription {

        @Test
        void thenItShouldThrowSelfKnowledgeInvalidDescriptionException() {
          BddLogger.when("creating self knowledge element with invalid description");
          SelfKnowledgeCategory selfKnowledgeCategory =
              SelfKnowledgeCategoryFixture.create().toModel();

          when(selfKnowledgeCategoryRepository.findById(selfKnowledgeCategory.getId()))
              .thenReturn(Optional.of(selfKnowledgeCategory));

          String tooLongDescription = "D".repeat(5000);

          assertThrows(
              SelfKnowledgeInvalidDescriptionException.class,
              () ->
                  selfKnowledgeService.createSelfKnowledgeElement(
                      selfKnowledgeCategory.getId(), "Title", tooLongDescription, 1));

          verify(selfKnowledgeElementRepository, never()).save(any());
        }
      }

      @Nested
      class WhenCreatingSelfKnowledgeElementWithInvalidRating {

        @Test
        void thenItShouldThrowSelfKnowledgeInvalidRatingException() {
          BddLogger.when("creating self knowledge element with invalid rating");
          SelfKnowledgeCategory selfKnowledgeCategory =
              SelfKnowledgeCategoryFixture.create().toModel();

          when(selfKnowledgeCategoryRepository.findById(selfKnowledgeCategory.getId()))
              .thenReturn(Optional.of(selfKnowledgeCategory));

          assertThrows(
              SelfKnowledgeInvalidRatingException.class,
              () ->
                  selfKnowledgeService.createSelfKnowledgeElement(
                      selfKnowledgeCategory.getId(), "Title", "Description", -999));

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

          verifyNoInteractions(selfKnowledgeCategoryRepository);
        }
      }

      @Nested
      class WhenAddingSelfKnowledgeCategoriesAndNoneSelectedFromAvailable {

        @Test
        void thenItShouldThrowSelfKnowledgeCategoryNotFoundException() {
          BddLogger.when("adding self knowledge categories but none match the available list");
          BddLogger.then("it should throw SelfKnowledgeCategoryNotFoundException");

          SelfKnowledgeCategory strengthsCategory =
              SelfKnowledgeCategoryFixture.create()
                  .withType(ESelfKnowledgeCategoryType.STRENGTHS)
                  .toModel();

          when(selfKnowledgeCategoryRepository.findAllAvailableByStudent(eq(student)))
              .thenReturn(List.of(strengthsCategory));

          List<String> requestedIdsAsString = List.of(UUID.randomUUID().toString());

          assertThrows(
              SelfKnowledgeCategoryNotAvailableException.class,
              () -> selfKnowledgeService.addSelfKnowledgeCategories(requestedIdsAsString));

          verify(selfKnowledgeCategoryRepository).findAllAvailableByStudent(eq(student));
          verify(studentService, never()).addSelfKnowledgeCategories(any(), anyList());
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
                "it should use available categories and delegate to studentService to"
                    + " associate them");

            verify(selfKnowledgeCategoryRepository).findAllAvailableByStudent(eq(student));
            verify(studentService)
                .addSelfKnowledgeCategories(
                    eq(student), eq(List.of(strengthsCategory, valuesCategory)));
          }
        }
      }

      @Nested
      class AndANonMandatorySelfKnowledgeCategory {

        private SelfKnowledgeCategory removableCategory;
        private UUID categoryId;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("a non mandatory self knowledge category linked to this student");
          removableCategory =
              SelfKnowledgeCategoryFixture.create()
                  .withType(ESelfKnowledgeCategoryType.STRENGTHS)
                  .withMandatory(false)
                  .toModel();
          categoryId = removableCategory.getId();

          when(selfKnowledgeCategoryRepository.findById(eq(categoryId)))
              .thenReturn(Optional.of(removableCategory));
        }

        @Nested
        class WhenRemovingSelfKnowledgeCategory {

          @BeforeEach
          void setupWhen() {
            BddLogger.when(
                "removing a non mandatory self knowledge category for the current student");
            selfKnowledgeService.removeSelfKnowledgeCategory(categoryId);
          }

          @Test
          void thenItShouldDeleteElementsAndRemoveCategoryForStudent() {
            BddLogger.then(
                "it should delete all elements for this student and category, then remove the"
                    + " category link for the student");

            verify(selfKnowledgeCategoryRepository).findById(eq(categoryId));
            verify(selfKnowledgeElementRepository)
                .deleteAllByStudentAndCategory(eq(student), eq(removableCategory));
            verify(studentService).removeSelfKnowledgeCategory(eq(student), eq(removableCategory));
          }
        }
      }

      @Nested
      class WhenRemovingUnknownSelfKnowledgeCategory {

        @Test
        void thenItShouldThrowSelfKnowledgeCategoryNotFoundException() {
          BddLogger.when("removing a self knowledge category that does not exist");
          BddLogger.then("it should throw SelfKnowledgeCategoryNotFoundException");

          UUID unknownId = UUID.randomUUID();

          when(selfKnowledgeCategoryRepository.findById(eq(unknownId)))
              .thenReturn(Optional.empty());

          assertThrows(
              SelfKnowledgeCategoryNotFoundException.class,
              () -> selfKnowledgeService.removeSelfKnowledgeCategory(unknownId));

          verify(selfKnowledgeCategoryRepository).findById(eq(unknownId));
          verifyNoInteractions(selfKnowledgeElementRepository);
          verify(studentService, never())
              .removeSelfKnowledgeCategory(any(), any(SelfKnowledgeCategory.class));
        }
      }

      @Nested
      class WhenRemovingMandatorySelfKnowledgeCategory {

        @Test
        void thenItShouldThrowSelfKnowledgeCategoryIsMandatoryException() {
          BddLogger.when("removing a mandatory self knowledge category");
          BddLogger.then("it should throw SelfKnowledgeCategoryIsMandatoryException");

          UUID categoryId = UUID.randomUUID();
          SelfKnowledgeCategory mandatoryCategory = mock(SelfKnowledgeCategory.class);

          when(mandatoryCategory.isMandatory()).thenReturn(true);
          when(selfKnowledgeCategoryRepository.findById(eq(categoryId)))
              .thenReturn(Optional.of(mandatoryCategory));

          assertThrows(
              SelfKnowledgeCategoryIsMandatoryException.class,
              () -> selfKnowledgeService.removeSelfKnowledgeCategory(categoryId));

          verify(selfKnowledgeCategoryRepository).findById(eq(categoryId));
          verifyNoInteractions(selfKnowledgeElementRepository);
          verify(studentService, never())
              .removeSelfKnowledgeCategory(eq(student), any(SelfKnowledgeCategory.class));
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

          verifyNoInteractions(studentService);
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

          verifyNoInteractions(studentService);
          verifyNoInteractions(selfKnowledgeCategoryRepository);
        }
      }

      @Nested
      class WhenRemovingSelfKnowledgeCategory {

        @Test
        void thenItShouldThrowUserNotFoundException() {
          BddLogger.when("removing a self knowledge category without logged in user");
          BddLogger.then("it should throw UserNotFoundException");

          UUID categoryId = UUID.randomUUID();

          assertThrows(
              UserNotFoundException.class,
              () -> selfKnowledgeService.removeSelfKnowledgeCategory(categoryId));

          verifyNoInteractions(studentService);
          verifyNoInteractions(selfKnowledgeCategoryRepository);
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
                      UUID.randomUUID(), new PageCriteria(0, 8)));
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
                      UUID.randomUUID(), "Title", "Description", 1));
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
                      UUID.randomUUID(), "Title", "Description", 1));
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

          verifyNoInteractions(selfKnowledgeCategoryRepository);
        }
      }

      @Nested
      class WhenRemovingSelfKnowledgeCategory {

        @Test
        void thenItShouldThrowUserIsNotStudentException() {
          BddLogger.when("removing self knowledge category for a non student user");
          BddLogger.then("it should throw UserIsNotStudentException");

          UUID categoryId = UUID.randomUUID();

          assertThrows(
              UserIsNotStudentException.class,
              () -> selfKnowledgeService.removeSelfKnowledgeCategory(categoryId));

          verifyNoInteractions(selfKnowledgeCategoryRepository);
          verifyNoInteractions(selfKnowledgeElementRepository);
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
                      UUID.randomUUID(), new PageCriteria(0, 8)));
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
                      UUID.randomUUID(), "Title", "Description", 1));
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
                      UUID.randomUUID(), "Title", "Description", 1));
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
