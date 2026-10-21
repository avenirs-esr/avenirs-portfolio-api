package fr.avenirsesr.portfolio.skill.infrastructure.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillFixture;
import fr.avenirsesr.portfolio.skill.infrastructure.fixture.SkillReferentialFixture;
import org.junit.jupiter.api.Test;

class SkillMapperTest {

  @Test
  void shouldMapASkillBackAndForthWithoutItsParent() {
    BddLogger.given("a root skill");
    Skill skill =
        SkillFixture.create()
            .withExternalCode("CODE")
            .withTitle("title")
            .withDescription("description")
            .toModel();

    BddLogger.when("mapping it to an entity and back");
    var entity = SkillMapper.INSTANCE.fromDomain(skill);
    var mapped = SkillMapper.INSTANCE.toDomain(entity);

    BddLogger.then("every field is preserved");
    assertThat(entity.getParent()).isNull();
    assertThat(mapped.getId()).isEqualTo(skill.getId());
    assertThat(mapped.getExternalCode()).isEqualTo("CODE");
    assertThat(mapped.getTitle()).isEqualTo("title");
    assertThat(mapped.getDescription()).contains("description");
    assertThat(mapped.isAssessable()).isTrue();
    assertThat(mapped.isArchived()).isFalse();
    assertThat(mapped.getReferential().getType()).isEqualTo(ESkillReferentialType.ROME4);
    assertThat(mapped.getParent()).isEmpty();
  }

  @Test
  void shouldKeepTheParentChainWhenMappingToAnEntity() {
    BddLogger.given("a skill nested under a domain");
    Skill domain = SkillFixture.create().withTitle("domain").withAssessable(false).toModel();
    Skill skill = SkillFixture.create().withTitle("skill").withParent(domain).toModel();

    BddLogger.when("mapping it to an entity");
    var entity = SkillMapper.INSTANCE.fromDomain(skill);

    BddLogger.then("the parent entity carries the parent identifier");
    assertThat(entity.getParent()).isNotNull();
    assertThat(entity.getParent().getId()).isEqualTo(domain.getId());
  }

  @Test
  void shouldAttachTheGivenParentWhenMappingToDomain() {
    BddLogger.given("a stored skill and its already mapped parent");
    Skill domain = SkillFixture.create().withTitle("domain").withAssessable(false).toModel();
    Skill skill = SkillFixture.create().withTitle("skill").toModel();
    var entity = SkillMapper.INSTANCE.fromDomain(skill);

    BddLogger.when("mapping it to domain with the parent");
    Skill mapped = SkillMapper.INSTANCE.toDomainWithParent(entity, domain);

    BddLogger.then("the path is rebuilt from the given parent");
    assertThat(mapped.getParent()).contains(domain);
    assertThat(mapped.getPathSegments()).containsExactly("domain");
  }

  @Test
  void shouldMapAReferentialBackAndForth() {
    BddLogger.given("an institution referential");
    var referential =
        SkillReferentialFixture.create()
            .withType(ESkillReferentialType.APC)
            .withTitle("Licence informatique")
            .withInstitutionId(java.util.UUID.randomUUID())
            .toModel();

    BddLogger.when("mapping it to an entity and back");
    var mapped =
        SkillReferentialMapper.INSTANCE.toDomain(
            SkillReferentialMapper.INSTANCE.fromDomain(referential));

    BddLogger.then("every field is preserved");
    assertThat(mapped.getId()).isEqualTo(referential.getId());
    assertThat(mapped.getType()).isEqualTo(ESkillReferentialType.APC);
    assertThat(mapped.getTitle()).isEqualTo("Licence informatique");
    assertThat(mapped.getInstitutionId()).isEqualTo(referential.getInstitutionId());
    assertThat(mapped.isNational()).isFalse();
  }
}
