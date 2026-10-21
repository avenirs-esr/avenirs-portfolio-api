package fr.avenirsesr.portfolio.skill.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.skill.domain.data.SkillPathSegment;
import fr.avenirsesr.portfolio.skill.domain.exception.SkillNotFoundException;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.NationalSkillDetailsRepository;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillFixture;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {

  @Mock private SkillRepository skillRepository;
  @Mock private NationalSkillDetailsRepository nationalSkillDetailsRepository;

  @InjectMocks private SkillServiceImpl service;

  @Test
  void shouldThrowWhenTheSkillDoesNotExist() {
    BddLogger.given("an unknown skill id");
    UUID id = UUID.randomUUID();
    when(skillRepository.findWithAncestorsById(id)).thenReturn(Optional.empty());

    BddLogger.then("it should throw a SkillNotFoundException");
    assertThatThrownBy(() -> service.getWithAncestorsById(id))
        .isInstanceOf(SkillNotFoundException.class);
  }

  @Test
  void shouldReturnTheDetailedPathWithTheKindOfEachAncestor() {
    BddLogger.given("a skill nested under a domain and an issue");
    Skill domain = SkillFixture.create().withTitle("domain").withAssessable(false).toModel();
    Skill issue =
        SkillFixture.create().withTitle("issue").withParent(domain).withAssessable(false).toModel();
    Skill skill = SkillFixture.create().withTitle("skill").withParent(issue).toModel();

    when(nationalSkillDetailsRepository.findKindsBySkillIds(List.of(domain.getId(), issue.getId())))
        .thenReturn(Map.of(domain.getId(), ESkillKind.DOMAIN, issue.getId(), ESkillKind.ISSUE));

    BddLogger.when("reading the detailed path");
    List<SkillPathSegment> path = service.getDetailedPath(skill);

    BddLogger.then("each segment carries its title and its kind");
    assertThat(path)
        .containsExactly(
            new SkillPathSegment("domain", ESkillKind.DOMAIN),
            new SkillPathSegment("issue", ESkillKind.ISSUE));
  }

  @Test
  void shouldReturnAnEmptyPathForARootSkill() {
    BddLogger.given("a root skill");
    Skill skill = SkillFixture.create().toModel();

    BddLogger.when("reading the detailed path");
    List<SkillPathSegment> path = service.getDetailedPath(skill);

    BddLogger.then("the path is empty and no kind is looked up");
    assertThat(path).isEmpty();
  }

  @Test
  void shouldLeaveTheKindEmptyWhenTheAncestorHasNoDetails() {
    BddLogger.given("a skill whose ancestor carries no national details");
    Skill domain = SkillFixture.create().withTitle("domain").withAssessable(false).toModel();
    Skill skill = SkillFixture.create().withTitle("skill").withParent(domain).toModel();

    when(nationalSkillDetailsRepository.findKindsBySkillIds(List.of(domain.getId())))
        .thenReturn(Map.of());

    BddLogger.when("reading the detailed path");
    List<SkillPathSegment> path = service.getDetailedPath(skill);

    BddLogger.then("the segment keeps its title with no kind");
    assertThat(path).containsExactly(new SkillPathSegment("domain", null));
  }

  @Test
  void shouldResolveThePathsOfSeveralSkillsInOneLookup() {
    BddLogger.given("two skills under the same domain");
    Skill domain = SkillFixture.create().withTitle("domain").withAssessable(false).toModel();
    Skill firstSkill = SkillFixture.create().withTitle("first").withParent(domain).toModel();
    Skill secondSkill = SkillFixture.create().withTitle("second").withParent(domain).toModel();

    when(nationalSkillDetailsRepository.findKindsBySkillIds(List.of(domain.getId())))
        .thenReturn(Map.of(domain.getId(), ESkillKind.DOMAIN));

    BddLogger.when("reading the detailed paths");
    Map<UUID, List<SkillPathSegment>> paths =
        service.getDetailedPaths(List.of(firstSkill, secondSkill));

    BddLogger.then("both skills share the same resolved path");
    assertThat(paths.get(firstSkill.getId()))
        .containsExactly(new SkillPathSegment("domain", ESkillKind.DOMAIN));
    assertThat(paths.get(secondSkill.getId()))
        .containsExactly(new SkillPathSegment("domain", ESkillKind.DOMAIN));
  }
}
