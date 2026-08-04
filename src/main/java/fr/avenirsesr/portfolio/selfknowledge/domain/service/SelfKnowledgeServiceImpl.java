package fr.avenirsesr.portfolio.selfknowledge.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.DESCRIPTION_LENGTH;
import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RATING_MAX;
import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RATING_MIN;
import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.TITLE_LENGTH;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.selfknowledge.domain.data.SelfKnowledgeElementDetails;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.*;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeElementRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SelfKnowledgeServiceImpl implements SelfKnowledgeService {
  private final StudentService studentService;
  private final SelfKnowledgeElementRepository selfKnowledgeElementRepository;
  private final LoggedInUserService loggedInUserService;

  @Override
  public PagedResult<SelfKnowledgeElement> getSelfKnowledgeElements(
      ESelfKnowledgeCategory selfKnowledgeCategory,
      PageCriteria pageCriteria,
      Boolean isValorized) {
    Student student = loggedInUserService.getLoggedInStudent();

    return selfKnowledgeElementRepository.findAllByStudentIdAndCategory(
        student.getId(), selfKnowledgeCategory, pageCriteria, isValorized);
  }

  @Override
  public SelfKnowledgeElementDetails getSelfKnowledgeElementDetails(UUID selfKnowledgeElementId) {
    Student student = loggedInUserService.getLoggedInStudent();

    SelfKnowledgeElement selfKnowledgeElement =
        selfKnowledgeElementRepository
            .findById(selfKnowledgeElementId)
            .orElseThrow(SelfKnowledgeElementNotFoundException::new);

    if (!student.getId().equals(selfKnowledgeElement.getStudent().getId())) {
      throw new UserNotAuthorizedException();
    }

    return new SelfKnowledgeElementDetails(selfKnowledgeElement);
  }

  @Override
  public SelfKnowledgeElement createSelfKnowledgeElement(
      ESelfKnowledgeCategory selfKnowledgeCategory,
      String title,
      String description,
      Integer rating) {
    Student student = loggedInUserService.getLoggedInStudent();

    checkTitleField(title);
    checkDescriptionField(description);
    checkRating(rating);

    SelfKnowledgeElement selfKnowledgeElement =
        SelfKnowledgeElement.create(
            UUID.randomUUID(), student, title, description, rating, selfKnowledgeCategory);
    return selfKnowledgeElementRepository.save(selfKnowledgeElement);
  }

  @Override
  public SelfKnowledgeElement updateSelfKnowledgeElement(
      UUID selfKnowledgeElementId,
      String title,
      String description,
      Integer rating,
      boolean valorized) {
    Student student = loggedInUserService.getLoggedInStudent();

    checkTitleField(title);
    checkDescriptionField(description);
    checkRating(rating);

    SelfKnowledgeElement selfKnowledgeElement =
        selfKnowledgeElementRepository
            .findById(selfKnowledgeElementId)
            .orElseThrow(SelfKnowledgeElementNotFoundException::new);

    if (!student.getId().equals(selfKnowledgeElement.getStudent().getId())) {
      throw new UserNotAuthorizedException();
    }

    selfKnowledgeElement.setTitle(title);
    selfKnowledgeElement.setDescription(description);

    if (rating != null) {
      selfKnowledgeElement.setRating(rating);
    }

    selfKnowledgeElement.setValorized(valorized);

    return selfKnowledgeElementRepository.save(selfKnowledgeElement);
  }

  @Override
  public void deleteSelfKnowledgeElements(List<UUID> selfKnowledgeElementIds) {
    Student student = loggedInUserService.getLoggedInStudent();

    List<SelfKnowledgeElement> selfKnowledgeElements =
        selfKnowledgeElementRepository.findAllById(selfKnowledgeElementIds);

    if (!selfKnowledgeElements.stream().allMatch(e -> e.getStudent().equals(student))) {
      throw new UserNotAuthorizedException();
    }

    List<ESelfKnowledgeCategory> categories =
        selfKnowledgeElements.stream().map(SelfKnowledgeElement::getSelfKnowledgeCategory).toList();

    boolean allEqual = categories.size() <= 1 || categories.stream().distinct().count() == 1;

    if (!allEqual) {
      throw new SelfKnowledgeElementsAreNotInSameCategoryException();
    }

    selfKnowledgeElementRepository.removeAllFromDatabase(selfKnowledgeElements);
  }

  @Override
  public List<ESelfKnowledgeCategory> getSelfKnowledgeCategories() {
    Student student = loggedInUserService.getLoggedInStudent();
    return Stream.concat(
            student.getSelfKnowledgeCategories().stream(),
            Arrays.stream(ESelfKnowledgeCategory.values())
                .filter(ESelfKnowledgeCategory::isMandatory))
        .distinct()
        .sorted(Comparator.comparing(ESelfKnowledgeCategory::getOrder))
        .toList();
  }

  @Override
  public List<ESelfKnowledgeCategory> getSelfKnowledgeCategoriesAvailable() {
    Student student = loggedInUserService.getLoggedInStudent();
    return Arrays.stream(ESelfKnowledgeCategory.values())
        .filter(category -> !category.isMandatory())
        .filter(category -> !student.getSelfKnowledgeCategories().contains(category))
        .sorted(Comparator.comparing(ESelfKnowledgeCategory::getOrder))
        .toList();
  }

  @Override
  public void addSelfKnowledgeCategories(List<ESelfKnowledgeCategory> categories) {
    Student student = loggedInUserService.getLoggedInStudent();
    if (categories.isEmpty()) {
      throw new SelfKnowledgeCategoryListIsEmptyException();
    }
    List<ESelfKnowledgeCategory> available = getSelfKnowledgeCategoriesAvailable();
    if (!available.containsAll(categories)) {
      throw new SelfKnowledgeCategoryNotAvailableException();
    }
    studentService.addSelfKnowledgeCategories(student, categories);
  }

  @Override
  public void removeSelfKnowledgeCategory(ESelfKnowledgeCategory selfKnowledgeCategory) {
    Student student = loggedInUserService.getLoggedInStudent();
    if (selfKnowledgeCategory.isMandatory()) {
      throw new SelfKnowledgeCategoryIsMandatoryException();
    }
    selfKnowledgeElementRepository.deleteAllByStudentAndCategory(student, selfKnowledgeCategory);
    studentService.removeSelfKnowledgeCategory(student, selfKnowledgeCategory);
  }

  private static void checkTitleField(String title) {
    if (title != null && title.length() > TITLE_LENGTH) {
      log.error("Title too long: {} characters (max = " + TITLE_LENGTH + ")", title.length());
      throw new SelfKnowledgeInvalidTitleException(
          "Title exceeds " + TITLE_LENGTH + " characters (actual: " + title.length() + ")");
    }
  }

  private static void checkDescriptionField(String description) {
    if (description != null && description.length() > DESCRIPTION_LENGTH) {
      log.error(
          "Description too long: {} characters (max = " + DESCRIPTION_LENGTH + ")",
          description.length());
      throw new SelfKnowledgeInvalidDescriptionException(
          "Description exceeds "
              + DESCRIPTION_LENGTH
              + " characters (actual: "
              + description.length()
              + ")");
    }
  }

  private static void checkRating(Integer rating) {
    if (rating != null && (rating < RATING_MIN || rating > RATING_MAX)) {
      log.error(
          "Rating is out of bounce: {} (min = " + RATING_MIN + ", max = " + RATING_MAX + ")",
          rating);
      throw new SelfKnowledgeInvalidRatingException(
          "Rating is out of bounce ["
              + RATING_MIN
              + ","
              + RATING_MAX
              + "] (actual: "
              + rating
              + ")");
    }
  }
}
