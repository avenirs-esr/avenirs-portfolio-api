package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.utils.FileReader;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.seeder.data.DeclaredActivityCreationData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.seeder.data.FakeDeclaredActivity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeclaredActivitySeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(DeclaredActivitySeeder.class, SharedDataGenerator.class);
  private static final String PATH_FILE = "seeder/declared-activities.json";
  private final FileReader fileReader;
  private final DeclaredActivityService declaredActivityService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public void seed(List<StudentEntity> savedStudents, List<ActivityEntity> savedActivities) {
    ValidationUtils.requireNonEmpty(savedStudents, "savedStudents cannot be empty");
    ValidationUtils.requireNonEmpty(savedActivities, "savedActivities cannot be empty");

    List<DeclaredActivityCreationData> creationData =
        switch (seederSource) {
          case CSV -> {
            List<DeclaredActivityCreationData> rawData =
                fileReader.readJSON(PATH_FILE, new TypeReference<>() {});
            LocalDate today = LocalDate.now();
            yield rawData.stream().map(data -> adjustDates(data, today)).toList();
          }
          case FAKER ->
              savedStudents.stream()
                  .map(
                      student ->
                          IntStream.range(0, SeederConfig.NB_DECLARED_ACTIVITIES_PER_STUDENT)
                              .mapToObj(
                                  i ->
                                      FakeDeclaredActivity.create(
                                              student,
                                              dataGenerator
                                                  .with("activity")
                                                  .pickIn(savedActivities))
                                          .toEntity())
                              .map(
                                  fakeDeclaredActivity ->
                                      new DeclaredActivityCreationData(
                                          fakeDeclaredActivity.getStudent().getId(),
                                          fakeDeclaredActivity.getActivity().getId(),
                                          fakeDeclaredActivity.getReflection(),
                                          fakeDeclaredActivity.getStartDate(),
                                          fakeDeclaredActivity.getEndDate(),
                                          fakeDeclaredActivity.getFinishedAt()))
                              .toList())
                  .flatMap(List::stream)
                  .toList();
        };

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
          declaredActivityService.subscribe(data.activityId(), data.startDate(), data.endDate());
        });

    log.info("✔ {} declared activities created", creationData.size());
  }

  private DeclaredActivityCreationData adjustDates(
      DeclaredActivityCreationData data, LocalDate today) {
    LocalDate originalStart = data.startDate();
    LocalDate originalEnd = data.endDate();

    if (originalStart == null || originalEnd == null) {
      return data;
    }

    long durationInDays = ChronoUnit.DAYS.between(originalStart, originalEnd);

    LocalDate currentYearStart = originalStart.withYear(today.getYear());

    LocalDate adjustedStart =
        currentYearStart.isBefore(today) ? currentYearStart.plusYears(1) : currentYearStart;

    LocalDate adjustedEnd = adjustedStart.plusDays(durationInDays);

    return new DeclaredActivityCreationData(
        data.studentId(),
        data.activityId(),
        data.reflection(),
        adjustedStart,
        adjustedEnd,
        data.finishedAt());
  }
}
