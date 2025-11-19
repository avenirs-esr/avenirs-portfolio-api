package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeElementEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.repository.SelfKnowledgeElementDatabaseRepository;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.fake.FakeSelfKnowledgeElement;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelfKnowledgeElementSeeder {
  private final SelfKnowledgeElementDatabaseRepository selfKnowledgeElementDatabaseRepository;

  @Transactional
  public List<SelfKnowledgeElementEntity> seed(List<StudentEntity> savedStudents) {
    log.info("Seeding self knowledge elements...");
    List<SelfKnowledgeElementEntity> studentSelfKnowledgeElementEntities = new ArrayList<>();
    savedStudents.forEach(
        student ->
            student
                .getSelfKnowledgeCategories()
                .forEach(
                    category -> {
                      var fakeSelfKnowledgeElement = FakeSelfKnowledgeElement.of(student, category);
                      studentSelfKnowledgeElementEntities.add(fakeSelfKnowledgeElement.toEntity());
                    }));
    selfKnowledgeElementDatabaseRepository.saveAllEntities(studentSelfKnowledgeElementEntities);
    log.info(
        "✔ {} studentSelfKnowledgeElements created", studentSelfKnowledgeElementEntities.size());
    return studentSelfKnowledgeElementEntities;
  }
}
