package fr.avenirsesr.portfolio.student.skill.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.skill.domain.data.SkillPathSegment;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillReferentialFixture;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.SkillProgressDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.SkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressAssociationCount;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressDetails;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.ESkillLevel;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.TraceOverviewMapper;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SkillProgressMapperTest {

  @Spy private OptionalMapper optionalMapper = Mappers.getMapper(OptionalMapper.class);

  @Spy
  private TraceOverviewMapper traceOverviewMapper = Mappers.getMapper(TraceOverviewMapper.class);

  @InjectMocks private SkillProgressMapperImpl mapper;

  @Test
  void shouldMapSkillProgressDataToDTO() {
    BddLogger.given("a skill progress on a skill nested under two ancestors");
    Student student = StudentFixture.create().toModel();
    Skill domain = SkillFixture.create().withTitle("Technology").withAssessable(false).toModel();
    Skill issue =
        SkillFixture.create()
            .withTitle("Backend")
            .withParent(domain)
            .withAssessable(false)
            .toModel();
    Skill skill = SkillFixture.create().withTitle("Java Programming").withParent(issue).toModel();

    SkillProgress skillProgress =
        SkillProgress.create(student, skill, ESkillLevel.BEGINNER, "My reflection");
    SkillProgressData data =
        new SkillProgressData(skillProgress, skill, new SkillProgressAssociationCount(2, 1));

    BddLogger.when("mapping to SkillProgressDTO");
    SkillProgressDTO dto = mapper.toSkillProgressDTO(data);

    BddLogger.then("it should map the skill fields and the ancestor titles");
    assertNotNull(dto);
    assertEquals(skillProgress.getId(), dto.id());
    assertEquals(skill.getId(), dto.skillId());
    assertEquals("Java Programming", dto.title());
    assertEquals(List.of("Technology", "Backend"), dto.pathSegments());
    assertEquals(ESkillReferentialType.ROME4, dto.type());
    assertEquals(ESkillLevel.BEGINNER, dto.level());
    assertFalse(dto.valorized());
    assertEquals(2, dto.associationsCount().traceAssociationsCount());
    assertEquals(1, dto.associationsCount().declaredActivityAssociationsCount());
  }

  @Test
  void shouldMapDetailsWithTheKindOfEachPathSegment() {
    BddLogger.given("a skill progress detail carrying a typed path");
    Student student = StudentFixture.create().toModel();
    Skill skill =
        SkillFixture.create()
            .withReferential(
                SkillReferentialFixture.create().withType(ESkillReferentialType.XXI).toModel())
            .withTitle("Java Programming")
            .withDescription("A description")
            .toModel();
    SkillProgress skillProgress =
        SkillProgress.create(student, skill, ESkillLevel.EXPERT, "My reflection");

    SkillProgressDetails details =
        new SkillProgressDetails(
            skillProgress,
            skill,
            List.of(
                new SkillPathSegment("Technology", ESkillKind.DOMAIN),
                new SkillPathSegment("Backend", ESkillKind.ISSUE)));

    BddLogger.when("mapping to SkillProgressDetailsDTO");
    SkillProgressDetailsDTO dto = mapper.toSkillProgressDetailsDTO(details);

    BddLogger.then("each path segment keeps its libelle and its kind");
    assertEquals(skill.getId(), dto.skillId());
    assertEquals("A description", dto.description());
    assertEquals(ESkillReferentialType.XXI, dto.type());
    assertEquals(2, dto.pathSegments().size());
    assertEquals("Technology", dto.pathSegments().getFirst().libelle());
    assertEquals(ESkillKind.DOMAIN, dto.pathSegments().getFirst().type());
    assertEquals("Backend", dto.pathSegments().getLast().libelle());
    assertEquals(ESkillKind.ISSUE, dto.pathSegments().getLast().type());
  }
}
