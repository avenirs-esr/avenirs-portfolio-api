package fr.avenirsesr.portfolio.student.association.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EAssociationTypeTest {

  @Test
  void
      EXPERIENCE_DECLARED_SKILL_should_declare_DeclaredExperience_as_key1_and_DeclaredSkillProgress_as_key2() {
    BddLogger.given("the EXPERIENCE_DECLARED_SKILL association type");

    BddLogger.when("reading its key classes");

    BddLogger.then("DeclaredExperience should be key1 and DeclaredSkillProgress should be key2");
    assertEquals(
        DeclaredExperience.class, EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL.getKey1());
    assertEquals(
        DeclaredSkillProgress.class, EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL.getKey2());
  }

  @Test
  void getAllBy_should_include_EXPERIENCE_DECLARED_SKILL_for_DeclaredExperience() {
    BddLogger.given("the DeclaredExperience class");

    BddLogger.when("calling getAllBy");

    BddLogger.then("it should include EXPERIENCE_DECLARED_SKILL and TRACE_DECLARED_EXPERIENCE");
    var types = EAssociationType.getAllBy(DeclaredExperience.class);
    assertTrue(types.contains(EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL));
    assertTrue(types.contains(EAssociationType.TRACE_DECLARED_EXPERIENCE));
  }

  @Test
  void getAllBy_should_include_EXPERIENCE_DECLARED_SKILL_for_DeclaredSkillProgress() {
    BddLogger.given("the DeclaredSkillProgress class");

    BddLogger.when("calling getAllBy");

    BddLogger.then("it should include EXPERIENCE_DECLARED_SKILL");
    var types = EAssociationType.getAllBy(DeclaredSkillProgress.class);
    assertTrue(types.contains(EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL));
    assertTrue(types.contains(EAssociationType.TRACE_DECLARED_SKILL));
    assertTrue(types.contains(EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL));
  }

  @Test
  void idExtractorFor_should_resolve_DeclaredExperience_side_using_id1() {
    BddLogger.given("an association of type EXPERIENCE_DECLARED_SKILL");
    UUID experienceId = UUID.randomUUID();
    UUID skillProgressId = UUID.randomUUID();
    Association association =
        Association.create(
            experienceId, skillProgressId, EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL);

    BddLogger.when("extracting the DeclaredExperience id");

    BddLogger.then("it should resolve to id1, the declared experience id");
    var extractor =
        EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL.idExtractorFor(
            DeclaredExperience.class);
    assertEquals(experienceId, extractor.apply(association));
  }

  @Test
  void idExtractorFor_should_resolve_DeclaredSkillProgress_side_using_id2() {
    BddLogger.given("an association of type EXPERIENCE_DECLARED_SKILL");
    UUID experienceId = UUID.randomUUID();
    UUID skillProgressId = UUID.randomUUID();
    Association association =
        Association.create(
            experienceId, skillProgressId, EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL);

    BddLogger.when("extracting the DeclaredSkillProgress id");

    BddLogger.then("it should resolve to id2, the declared skill progress id");
    var extractor =
        EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL.idExtractorFor(
            DeclaredSkillProgress.class);
    assertEquals(skillProgressId, extractor.apply(association));
  }
}
