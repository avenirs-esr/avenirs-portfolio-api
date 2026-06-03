package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.mapper.FeedbackMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.FeedbackEntity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.seeder.data.FeedbackCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackSeeder {
  private static final String PATH_FILE = "seeder/feedbacks.json";
  private final FileReader fileReader;
  private final DeclaredActivityService declaredActivityService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<FeedbackEntity> seed(
      List<StudentEntity> savedStudents, List<DeclaredActivityEntity> savedDeclaredActivities) {
    ValidationUtils.requireNonEmpty(savedStudents, "savedStudents cannot be empty");
    ValidationUtils.requireNonEmpty(
        savedDeclaredActivities, "savedDeclaredActivities cannot be empty");

    log.info("Seeding Feedbacks...");

    List<FeedbackCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(PATH_FILE, new TypeReference<List<FeedbackCreationData>>() {});
          case FAKER ->
              savedDeclaredActivities.stream()
                  .filter(a -> a.getFinishedAt() != null)
                  .map(a -> new FeedbackCreationData(a.getId(), a.getStudent().getId()))
                  .toList();
        };

    List<Feedback> feedbacks = new ArrayList<>();

    creationData.forEach(
        data -> {
          var student =
              savedStudents.stream()
                  .filter(s -> s.getId().equals(data.studentId()))
                  .findAny()
                  .orElseThrow();

          RequestContext.set(
              new RequestData(
                  Optional.ofNullable(UserMapper.INSTANCE.toDomain(student.getUser())),
                  ELanguage.FRENCH));

          feedbacks.add(declaredActivityService.createFeedback(data.declaredActivityId()));
        });

    log.info("✔ {} feedbacks created", feedbacks.size());
    return feedbacks.stream().map(FeedbackMapper.INSTANCE::fromDomain).toList();
  }
}
