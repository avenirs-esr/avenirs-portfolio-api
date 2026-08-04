package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper.SelfKnowledgeElementMapper;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeElementEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.data.SelfKnowledgeElementCreationData;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelfKnowledgeElementSeeder {
  private static final String PATH_FILE = "seeder/self-knowledge-elements.json";
  private final FileReader fileReader;
  private final StudentRepository studentRepository;
  private final SelfKnowledgeService selfKnowledgeService;

  @Transactional
  public List<SelfKnowledgeElementEntity> seed() {
    log.info("Seeding self knowledge elements...");
    List<SelfKnowledgeElementCreationData> creationData =
        fileReader.readJSON(PATH_FILE, new TypeReference<>() {});

    List<SelfKnowledgeElement> elements = new ArrayList<>();
    creationData.forEach(
        element -> {
          Student student =
              studentRepository
                  .findById(element.studentId())
                  .orElseThrow(UserIsNotStudentException::new);
          RequestContext.set(new RequestData(Optional.of(student.getUser()), ELanguage.FRENCH));

          elements.add(
              selfKnowledgeService.createSelfKnowledgeElement(
                  ESelfKnowledgeCategory.valueOf(element.category()),
                  element.title(),
                  element.description(),
                  element.rating()));
        });

    log.info("✔ {} studentSelfKnowledgeElements created", elements.size());
    return elements.stream().map(SelfKnowledgeElementMapper.INSTANCE::fromDomain).toList();
  }
}
