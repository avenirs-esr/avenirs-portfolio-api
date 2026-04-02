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
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategoryType;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeCategoryRepository;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeElementRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SelfKnowledgeServiceImpl implements SelfKnowledgeService {
  private final StudentService studentService;
  private final SelfKnowledgeElementRepository selfKnowledgeElementRepository;
  private final SelfKnowledgeCategoryRepository selfKnowledgeCategoryRepository;
  private final LoggedInUserService loggedInUserService;

  @Override
  public PagedResult<SelfKnowledgeElement> getSelfKnowledgeElements(
      UUID selfKnowledgeCategoryId, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();

    selfKnowledgeCategoryRepository
        .findById(selfKnowledgeCategoryId)
        .orElseThrow(SelfKnowledgeCategoryNotFoundException::new);

    return selfKnowledgeElementRepository.findAllByStudentIdAndCategoryId(
        student.getId(), selfKnowledgeCategoryId, pageCriteria);
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
      UUID selfKnowledgeCategoryId, String title, String description, Integer rating) {
    Student student = loggedInUserService.getLoggedInStudent();

    checkTitleField(title);
    checkDescriptionField(description);
    checkRating(rating);

    SelfKnowledgeCategory selfKnowledgeCategory =
        selfKnowledgeCategoryRepository
            .findById(selfKnowledgeCategoryId)
            .orElseThrow(SelfKnowledgeCategoryNotFoundException::new);

    SelfKnowledgeElement selfKnowledgeElement =
        SelfKnowledgeElement.create(
            UUID.randomUUID(), student, title, description, rating, selfKnowledgeCategory);
    return selfKnowledgeElementRepository.save(selfKnowledgeElement);
  }

  @Override
  public SelfKnowledgeElement updateSelfKnowledgeElement(
      UUID selfKnowledgeElementId, String title, String description, Integer rating) {
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

    return selfKnowledgeElementRepository.save(selfKnowledgeElement);
  }

  public void deleteSelfKnowledgeElements(List<UUID> selfKnowledgeElementIds) {
    Student student = loggedInUserService.getLoggedInStudent();

    List<SelfKnowledgeElement> selfKnowledgeElements =
        selfKnowledgeElementRepository.findAllById(selfKnowledgeElementIds);

    if (!selfKnowledgeElements.stream().allMatch(e -> e.getStudent().equals(student))) {
      throw new UserNotAuthorizedException();
    }

    List<SelfKnowledgeCategory> categories =
        selfKnowledgeElements.stream().map(element -> element.getSelfKnowledgeCategory()).toList();

    boolean allEqual = categories.size() <= 1 || categories.stream().distinct().count() == 1;

    if (!allEqual) {
      throw new SelfKnowledgeElementsAreNotInSameCategoryException();
    }

    selfKnowledgeElementRepository.removeAllFromDatabase(selfKnowledgeElements);
  }

  @Override
  public List<SelfKnowledgeCategory> getSelfKnowledgeCategories() {
    Student student = loggedInUserService.getLoggedInStudent();
    return selfKnowledgeCategoryRepository.findAllByStudent(student).stream()
        .sorted(Comparator.comparing(c -> c.getType().getOrder()))
        .toList();
  }

  @Override
  public List<SelfKnowledgeCategory> getSelfKnowledgeCategoriesAvailable() {
    Student student = loggedInUserService.getLoggedInStudent();
    return selfKnowledgeCategoryRepository.findAllAvailableByStudent(student).stream()
        .sorted(Comparator.comparing(c -> c.getType().getOrder()))
        .toList();
  }

  @Override
  public void addSelfKnowledgeCategories(List<String> categories) {
    Student student = loggedInUserService.getLoggedInStudent();
    List<UUID> categoryIds = categories.stream().map(UUID::fromString).toList();
    if (categoryIds.isEmpty()) {
      throw new SelfKnowledgeCategoryListIsEmptyException();
    }
    List<SelfKnowledgeCategory> categoriesToAssociate =
        getAvailableCategoriesToAdd(student, categoryIds);
    if (categoriesToAssociate.isEmpty()) {
      throw new SelfKnowledgeCategoryNotFoundException();
    }
    studentService.addSelfKnowledgeCategories(student, categoriesToAssociate);
  }

  @Override
  public void initSelfKnowledgeCategoriesMandatory(Student student) {
    var mandatoryCategories = selfKnowledgeCategoryRepository.findAllMandatory();
    studentService.addSelfKnowledgeCategories(student, mandatoryCategories);
  }

  @Override
  public void removeSelfKnowledgeCategory(UUID categoryId) {
    Student student = loggedInUserService.getLoggedInStudent();
    SelfKnowledgeCategory selfKnowledgeCategory =
        selfKnowledgeCategoryRepository
            .findById(categoryId)
            .orElseThrow(SelfKnowledgeCategoryNotFoundException::new);
    if (selfKnowledgeCategory.isMandatory()) {
      throw new SelfKnowledgeCategoryIsMandatoryException();
    }
    selfKnowledgeElementRepository.deleteAllByStudentAndCategory(student, selfKnowledgeCategory);
    studentService.removeSelfKnowledgeCategory(student, selfKnowledgeCategory);
  }

  @Override
  public SelfKnowledgeCategory createSelfKnowledgeCategory(
      UUID id,
      String title,
      String description,
      ESelfKnowledgeCategoryType type,
      boolean isMandatory) {
    var category = SelfKnowledgeCategory.create(id, title, description, type, isMandatory);
    selfKnowledgeCategoryRepository.save(category);
    return category;
  }

  @Override
  public SelfKnowledgeCategory updateSelfKnowledgeCategory(
      UUID categoryId, String title, String description) {
    var category =
        selfKnowledgeCategoryRepository
            .findById(categoryId)
            .orElseThrow(SelfKnowledgeCategoryNotFoundException::new);
    category.setTitle(title);
    category.setDescription(description);
    selfKnowledgeCategoryRepository.save(category);
    return category;
  }

  private List<SelfKnowledgeCategory> getAvailableCategoriesToAdd(
      Student student, List<UUID> categoryIds) {
    List<SelfKnowledgeCategory> availableCategories =
        selfKnowledgeCategoryRepository.findAllAvailableByStudent(student);
    Set<UUID> requestedIds = new HashSet<>(categoryIds);
    Set<UUID> availableIds =
        availableCategories.stream()
            .map(SelfKnowledgeCategory::getId)
            .collect(java.util.stream.Collectors.toSet());
    if (!availableIds.containsAll(requestedIds)) {
      throw new SelfKnowledgeCategoryNotAvailableException();
    }
    return availableCategories.stream()
        .filter(category -> requestedIds.contains(category.getId()))
        .toList();
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
