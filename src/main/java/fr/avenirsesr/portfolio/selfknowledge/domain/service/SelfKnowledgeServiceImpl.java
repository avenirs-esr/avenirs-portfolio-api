package fr.avenirsesr.portfolio.selfknowledge.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.selfknowledge.domain.data.SelfKnowledgeElementDetails;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.*;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeCategoryRepository;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeElementRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
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
  private final StudentRepository studentRepository;
  private final SelfKnowledgeElementRepository selfKnowledgeElementRepository;
  private final SelfKnowledgeCategoryRepository selfKnowledgeCategoryRepository;

  public static final int TITLE_LENGTH_MAX = 80;
  public static final int DESCRIPTION_LENGTH_MAX = 400;
  public static final int RATING_MIN = 1;
  public static final int RATING_MAX = 5;

  private Student getStudent() {
    User loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    return studentRepository
        .findById(loggedInUser.getId())
        .orElseThrow(UserIsNotStudentException::new);
  }

  @Override
  public PagedResult<SelfKnowledgeElement> getSelfKnowledgeElements(
      UUID selfKnowledgeCategoryId, PageCriteria pageCriteria) {
    Student student = getStudent();

    selfKnowledgeCategoryRepository
        .findById(selfKnowledgeCategoryId)
        .orElseThrow(SelfKnowledgeCategoryNotFoundException::new);

    return selfKnowledgeElementRepository.findAllByStudentIdAndCategoryId(
        student.getId(), selfKnowledgeCategoryId, pageCriteria);
  }

  @Override
  public SelfKnowledgeElementDetails getSelfKnowledgeElementDetails(UUID selfKnowledgeElementId) {
    Student student = getStudent();

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
    Student student = getStudent();

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
    Student student = getStudent();

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

  public void deleteSelfKnowledgeElement(UUID selfKnowledgeElementId) {
    Student student = getStudent();

    SelfKnowledgeElement selfKnowledgeElement =
        selfKnowledgeElementRepository
            .findById(selfKnowledgeElementId)
            .orElseThrow(SelfKnowledgeElementNotFoundException::new);

    if (!student.getId().equals(selfKnowledgeElement.getStudent().getId())) {
      throw new UserNotAuthorizedException();
    }

    selfKnowledgeElementRepository.removeFromDatabase(selfKnowledgeElement);
  }

  @Override
  public List<SelfKnowledgeCategory> getSelfKnowledgeCategories() {
    Student student = getStudent();
    return selfKnowledgeCategoryRepository.findAllByStudent(student).stream()
        .sorted(Comparator.comparing(c -> c.getType().getOrder()))
        .toList();
  }

  @Override
  public List<SelfKnowledgeCategory> getSelfKnowledgeCategoriesAvailable() {
    Student student = getStudent();
    return selfKnowledgeCategoryRepository.findAllAvailableByStudent(student).stream()
        .sorted(Comparator.comparing(c -> c.getType().getOrder()))
        .toList();
  }

  @Override
  public void addSelfKnowledgeCategories(List<String> categories) {
    Student student = getStudent();
    List<UUID> categoryIds = categories.stream().map(UUID::fromString).toList();
    if (categoryIds.isEmpty()) {
      throw new SelfKnowledgeCategoryListIsEmptyException();
    }
    List<SelfKnowledgeCategory> categoriesToAssociate =
        getAvailableCategoriesToAdd(student, categoryIds);
    if (categoriesToAssociate.isEmpty()) {
      throw new SelfKnowledgeCategoryNotFoundException();
    }
    studentRepository.addSelfKnowledgeCategories(student, categoriesToAssociate);
  }

  @Override
  public void removeSelfKnowledgeCategory(UUID categoryId) {
    Student student = getStudent();
    SelfKnowledgeCategory selfKnowledgeCategory =
        selfKnowledgeCategoryRepository
            .findById(categoryId)
            .orElseThrow(SelfKnowledgeCategoryNotFoundException::new);
    if (selfKnowledgeCategory.isMandatory()) {
      throw new SelfKnowledgeCategoryIsMandatoryException();
    }
    selfKnowledgeElementRepository.deleteAllByStudentAndCategory(student, selfKnowledgeCategory);
    studentRepository.removeSelfKnowledgeCategory(student, selfKnowledgeCategory);
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
    if (title != null && title.length() > TITLE_LENGTH_MAX) {
      log.error("Title too long: {} characters (max = " + TITLE_LENGTH_MAX + ")", title.length());
      throw new SelfKnowledgeInvalidTitleException(
          "Title exceeds " + TITLE_LENGTH_MAX + " characters (actual: " + title.length() + ")");
    }
  }

  private static void checkDescriptionField(String description) {
    if (description != null && description.length() > DESCRIPTION_LENGTH_MAX) {
      log.error(
          "Description too long: {} characters (max = " + DESCRIPTION_LENGTH_MAX + ")",
          description.length());
      throw new SelfKnowledgeInvalidDescriptionException(
          "Description exceeds "
              + DESCRIPTION_LENGTH_MAX
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
