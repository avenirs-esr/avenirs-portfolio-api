package fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.seeder.data.DeclaredExperienceCreationData;
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.seeder.fake.FakeDeclaredExperience;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeclaredExperienceSeeder {
  private static final String PATH_FILE = "seeder/declared-experience.json";
  private final ObjectMapper objectMapper;
  private final DeclaredExperienceService declaredExperienceService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public void seed(List<StudentEntity> savedStudents) {
    ValidationUtils.requireNonEmpty(savedStudents, "savedStudents cannot be empty");

    List<DeclaredExperienceCreationData> creationData =
        switch (seederSource) {
          case CSV -> readInstitutionCreationDataFromFile();
          case FAKER ->
              IntStream.range(0, SeederConfig.NB_OF_DECLARED_EXPERIENCES)
                  .mapToObj(i -> savedStudents.get(new Random().nextInt(savedStudents.size())))
                  .map(FakeDeclaredExperience::of)
                  .map(FakeDeclaredExperience::toEntity)
                  .map(
                      entity ->
                          new DeclaredExperienceCreationData(
                              entity.getStudent().getId(),
                              entity.getTitle(),
                              entity.getExperienceType(),
                              entity.getOrganization(),
                              entity.getActivitySector(),
                              entity.getLocation(),
                              entity.getDescription(),
                              entity.getSourceOfInformation(),
                              entity.getSummary(),
                              entity.getExternalLink(),
                              entity.getStartDate(),
                              entity.getEndDate()))
                  .toList();
        };

    creationData.forEach(
        data ->
            declaredExperienceService.create(
                data.studentId(),
                data.title(),
                data.experienceType(),
                data.organization(),
                data.activitySector(),
                data.location(),
                data.description(),
                data.sourceOfInformation(),
                data.summary(),
                data.externalLink(),
                data.startDate(),
                data.endDate()));
  }

  private List<DeclaredExperienceCreationData> readInstitutionCreationDataFromFile() {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(PATH_FILE)) {
      if (is == null) {
        throw new IllegalStateException("File not found: " + PATH_FILE);
      }
      return objectMapper.readValue(is, new TypeReference<>() {});
    } catch (Exception e) {
      throw new RuntimeException("Error while reading %s".formatted(PATH_FILE), e);
    }
  }
}
