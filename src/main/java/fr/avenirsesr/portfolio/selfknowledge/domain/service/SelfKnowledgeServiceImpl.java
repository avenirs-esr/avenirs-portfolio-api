package fr.avenirsesr.portfolio.selfknowledge.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeCategoryListIsEmptyException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeCategoryNotAvailableException;
import fr.avenirsesr.portfolio.selfknowledge.domain.exception.SelfKnowledgeCategoryNotFoundException;
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
}
