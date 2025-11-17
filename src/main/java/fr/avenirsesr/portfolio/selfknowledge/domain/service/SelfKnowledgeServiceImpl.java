package fr.avenirsesr.portfolio.selfknowledge.domain.service;

import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository.SelfKnowledgeCategoryRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SelfKnowledgeServiceImpl implements SelfKnowledgeService {
  private final StudentRepository studentRepository;
  private final SelfKnowledgeCategoryRepository selfKnowledgeCategoryRepository;

  @Override
  public List<SelfKnowledgeCategory> getSelfKnowledgeCategories() {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    return selfKnowledgeCategoryRepository.findAllByStudent(student).stream()
        .sorted(Comparator.comparing(c -> c.getType().getOrder()))
        .toList();
  }
}
