package fr.avenirsesr.portfolio.skill.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillReferentialFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SkillTest {

  @Test
  void shouldExposeItsAncestorsFromTheRootDown() {
    BddLogger.given("a skill nested under a domain and an issue");
    Skill domain = SkillFixture.create().withTitle("domain").withAssessable(false).toModel();
    Skill issue =
        SkillFixture.create().withTitle("issue").withParent(domain).withAssessable(false).toModel();
    Skill skill = SkillFixture.create().withTitle("skill").withParent(issue).toModel();

    BddLogger.when("reading its ancestors");
    List<Skill> ancestors = skill.getAncestors();

    BddLogger.then("they are ordered from the root down and exclude the skill itself");
    assertThat(ancestors).containsExactly(domain, issue);
    assertThat(skill.getPathSegments()).containsExactly("domain", "issue");
  }

  @Test
  void shouldExposeAnEmptyPathForARootSkill() {
    BddLogger.given("a skill without parent");
    Skill skill = SkillFixture.create().toModel();

    BddLogger.then("its path is empty and its parent is absent");
    assertThat(skill.getAncestors()).isEmpty();
    assertThat(skill.getPathSegments()).isEmpty();
    assertThat(skill.getParent()).isEmpty();
  }

  @Test
  void shouldExposeAnEmptyDescriptionWhenNoneIsSet() {
    BddLogger.given("a skill without description");
    Skill skill = SkillFixture.create().withDescription(null).toModel();

    BddLogger.then("the description is absent");
    assertThat(skill.getDescription()).isEmpty();
  }

  @Test
  void shouldCreateAnUnarchivedSkill() {
    BddLogger.given("a created skill");
    SkillReferential referential = SkillReferentialFixture.create().toModel();
    Skill skill = Skill.create(UUID.randomUUID(), referential, "CODE", "title", null, null, true);

    BddLogger.then("it is not archived");
    assertThat(skill.isArchived()).isFalse();
    assertThat(skill.isAssessable()).isTrue();
    assertThat(skill.getReferential()).isEqualTo(referential);
  }

  @Test
  void shouldTellWhetherTheReferentialIsNational() {
    BddLogger.given("a national referential and an institution referential");
    SkillReferential national = SkillReferentialFixture.create().toModel();
    SkillReferential institution =
        SkillReferentialFixture.create()
            .withType(ESkillReferentialType.APC)
            .withInstitutionId(UUID.randomUUID())
            .toModel();

    BddLogger.then("only the one without institution is national");
    assertThat(national.isNational()).isTrue();
    assertThat(national.getInstitutionId()).isEmpty();
    assertThat(institution.isNational()).isFalse();
    assertThat(institution.getInstitutionId()).isPresent();
  }
}
