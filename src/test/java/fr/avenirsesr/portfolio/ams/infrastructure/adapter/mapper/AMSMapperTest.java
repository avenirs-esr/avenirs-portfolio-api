package fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.AMSFixture;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.CohortFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.SkillLevelProgressFixture;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AMSMapperTest {

  private AMS ams;
  private User user;
  private List<SkillLevelProgress> skillLevelProgresses;
  private List<Trace> traces;
  private Set<Cohort> cohorts;
  private final String title = "Test AMS Title";
  private final ELanguage language = ELanguage.FRENCH;
  private final Instant startDate = Instant.parse("2023-01-01T00:00:00Z");
  private final Instant endDate = Instant.parse("2023-12-31T23:59:59Z");
  private final EAmsStatus status = EAmsStatus.IN_PROGRESS;
  private final UUID id = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    user = UserFixture.create().toModel();
    traces = TraceFixture.create().withCount(3);
    cohorts = new HashSet<>(CohortFixture.create().withCount(2));
    skillLevelProgresses =
        SkillLevelProgressFixture.createMany(user.toStudent(), 3).stream()
            .map(SkillLevelProgressFixture::toModel)
            .toList();

    ams =
        AMSFixture.create()
            .withId(id)
            .withUser(user)
            .withTitle(title)
            .withLanguage(language)
            .withStartDate(startDate)
            .withEndDate(endDate)
            .toModel();

    ams.setSkillLevels(skillLevelProgresses);
    ams.setTraces(traces);
    ams.setCohorts(cohorts);
    ams.setStatus(status);
  }

  @Test
  void shouldMapFromDomainToEntity() {
    AMSEntity entity = AMSMapper.fromDomain(ams);

    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(user.getId(), entity.getUser().getId());
    assertEquals(status, entity.getStatus());
    assertEquals(startDate, entity.getStartDate());
    assertEquals(endDate, entity.getEndDate());

    assertEquals(skillLevelProgresses.size(), entity.getSkillLevels().size());
    assertEquals(traces.size(), entity.getTraces().size());
    assertEquals(cohorts.size(), entity.getCohorts().size());

    for (SkillLevelProgressEntity skillLevelEntity : entity.getSkillLevels()) {
      assertTrue(
          skillLevelProgresses.stream()
              .anyMatch(sl -> sl.getId().equals(skillLevelEntity.getId())));
    }

    for (TraceEntity traceEntity : entity.getTraces()) {
      assertTrue(traces.stream().anyMatch(t -> t.getId().equals(traceEntity.getId())));
    }

    for (CohortEntity cohortEntity : entity.getCohorts()) {
      assertTrue(cohorts.stream().anyMatch(c -> c.getId().equals(cohortEntity.getId())));
    }
  }

  @Test
  void shouldMapFromEntityToDomain() {
    AMSEntity entity = new AMSEntity();
    entity.setId(id);
    entity.setUser(UserMapper.fromDomain(user));
    entity.setStatus(status);
    entity.setStartDate(startDate);
    entity.setEndDate(endDate);
    entity.setSkillLevels(new ArrayList<>());
    entity.setTraces(new ArrayList<>());
    entity.setCohorts(new HashSet<>());

    AMSTranslationEntity translationEntity = new AMSTranslationEntity();
    translationEntity.setLanguage(language);
    translationEntity.setTitle(title);
    translationEntity.setAms(entity);

    Set<AMSTranslationEntity> translations = new HashSet<>();
    translations.add(translationEntity);
    entity.setTranslations(translations);

    AMS mappedAms = AMSMapper.toDomain(entity);

    assertNotNull(mappedAms);
    assertEquals(id, mappedAms.getId());
    assertEquals(user.getId(), mappedAms.getUser().getId());
    assertEquals(title, mappedAms.getTitle());
    assertEquals(status, mappedAms.getStatus());
  }

  @Test
  void shouldMapWithEmptyCollections() {
    AMSEntity entity = new AMSEntity();
    entity.setId(id);
    entity.setUser(UserMapper.fromDomain(user));
    entity.setStatus(status);
    entity.setStartDate(startDate);
    entity.setEndDate(endDate);
    entity.setSkillLevels(new ArrayList<>());
    entity.setTraces(new ArrayList<>());
    entity.setCohorts(new HashSet<>());

    assertNotNull(entity);
    assertTrue(entity.getSkillLevels() != null && entity.getSkillLevels().isEmpty());
    assertTrue(entity.getTraces() != null && entity.getTraces().isEmpty());
    assertTrue(entity.getCohorts() != null && entity.getCohorts().isEmpty());

    AMSTranslationEntity translationEntity = new AMSTranslationEntity();
    translationEntity.setLanguage(language);
    translationEntity.setTitle(title);
    translationEntity.setAms(entity);

    Set<AMSTranslationEntity> translations = new HashSet<>();
    translations.add(translationEntity);
    entity.setTranslations(translations);

    AMS mappedAms = AMSMapper.toDomain(entity);

    assertNotNull(mappedAms);
    assertTrue(mappedAms.getSkillLevels() != null && mappedAms.getSkillLevels().isEmpty());
    assertTrue(mappedAms.getTraces() != null && mappedAms.getTraces().isEmpty());
    assertTrue(mappedAms.getCohorts() != null && mappedAms.getCohorts().isEmpty());
  }
}
